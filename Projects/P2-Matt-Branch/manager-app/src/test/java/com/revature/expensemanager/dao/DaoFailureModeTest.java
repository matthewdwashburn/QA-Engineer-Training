package com.revature.expensemanager.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.revature.expensemanager.allure.ParentSuite;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

/**
 * What every DAO does when the database refuses the query.
 *
 * <p>Each JDBC DAO ends in the same shape: catch {@link java.sql.SQLException},
 * wrap it in a {@link RuntimeException} carrying a message that names the
 * operation, and rethrow with the original as the cause. Until now nothing
 * exercised those blocks - they were the bulk of the uncovered instructions in
 * the {@code dao} package.
 *
 * <p><strong>The contract being pinned is not "an exception happens".</strong>
 * {@code AppFactory} maps {@code RuntimeException} to a {@code 500}, so these
 * blocks are the reason a database failure reaches the caller as an error at
 * all. A DAO that swallowed the exception and returned an empty list instead
 * would show a manager "no expenses" when the truth is "the database is
 * unreachable" - a wrong answer that looks exactly like a right one, and one no
 * other test in the suite could see. Three things are asserted per method:
 *
 * <ul>
 *   <li>it throws rather than returning an empty or default result;</li>
 *   <li>the message names <em>this</em> operation - ten near-identical catch
 *       blocks are precisely where a copy-pasted message survives review, which
 *       is the shape BUG-2 already took once in this codebase;</li>
 *   <li>the {@code SQLException} is preserved as the cause, so the stack trace
 *       still says what the database actually objected to.</li>
 * </ul>
 *
 * <p>The failure is induced honestly: the DAOs are pointed at a real but
 * schemaless SQLite file, so the driver raises a genuine "no such table" on
 * every query. No mocking of {@code DatabaseConfig} - a static mock here would
 * only prove the mock throws.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("Manager Portal Backend")
@Feature("Persistence")
@ParentSuite("Manager - Repository Layer")
@Story("Database failure handling")
@Tag("db")
@Tag("negative")
@DisplayName("DAO failure modes - schemaless database")
class DaoFailureModeTest {

    private static final String DB_URL_PROPERTY = "db.url";

    private Path databaseFile;
    private String previousDatabaseUrl;

    @BeforeAll
    void pointDaosAtSchemalessDatabase() throws IOException {
        previousDatabaseUrl = System.getProperty(DB_URL_PROPERTY);

        // A zero-byte file is a valid, entirely empty SQLite database. The
        // connection opens; every statement then fails with "no such table".
        databaseFile = Files.createTempFile("dao-failure-mode-", ".db");
        System.setProperty(DB_URL_PROPERTY, "jdbc:sqlite:" + databaseFile.toAbsolutePath());
    }

    @AfterAll
    void restoreDatabaseUrl() throws IOException {
        if (previousDatabaseUrl == null) {
            System.clearProperty(DB_URL_PROPERTY);
        } else {
            System.setProperty(DB_URL_PROPERTY, previousDatabaseUrl);
        }
        if (databaseFile != null) {
            Files.deleteIfExists(databaseFile);
        }
    }

    /**
     * Every DAO method that owns a catch block, paired with the message it is
     * supposed to report. Listing them together is deliberate: a duplicated
     * message shows up as two identical strings in one place.
     */
    static Stream<Arguments> daoOperations() {
        JdbcReportDAO reportDAO = new JdbcReportDAO();
        JdbcExpenseDAO expenseDAO = new JdbcExpenseDAO();
        JdbcUserDAO userDAO = new JdbcUserDAO();
        JdbcApprovalDAO approvalDAO = new JdbcApprovalDAO();

        return Stream.of(
                Arguments.of("JdbcReportDAO.findExpensesByCategory",
                        (Executable) () -> reportDAO.findExpensesByCategory("MEALS"),
                        "Error finding expenses by category."),
                Arguments.of("JdbcReportDAO.findExpensesByDateRange",
                        (Executable) () -> reportDAO.findExpensesByDateRange("2026-07-01", "2026-07-31"),
                        "Error finding expenses by date range."),
                Arguments.of("JdbcReportDAO.findExpensesByEmployee",
                        (Executable) () -> reportDAO.findExpensesByEmployee(1),
                        "Error finding expenses by employee."),

                Arguments.of("JdbcExpenseDAO.findById",
                        (Executable) () -> expenseDAO.findById(1),
                        "Error finding expense by id."),
                Arguments.of("JdbcExpenseDAO.findPendingExpenses",
                        (Executable) () -> expenseDAO.findPendingExpenses(),
                        "Error finding pending expenses."),

                Arguments.of("JdbcUserDAO.findById",
                        (Executable) () -> userDAO.findById(1),
                        "Error finding user by id."),
                Arguments.of("JdbcUserDAO.findByUsername",
                        (Executable) () -> userDAO.findByUsername("siri"),
                        "Error finding user by username."),
                Arguments.of("JdbcUserDAO.getEmployees",
                        (Executable) () -> userDAO.getEmployees(),
                        "Error retrieving employees."),

                Arguments.of("JdbcApprovalDAO.findByExpenseId",
                        (Executable) () -> approvalDAO.findByExpenseId(1),
                        "Error finding approval by expense id."),
                Arguments.of("JdbcApprovalDAO.reviewExpense",
                        (Executable) () -> approvalDAO.reviewExpense(1, "approved", 3, "ok"),
                        "Error reviewing expense."));
    }

    @ParameterizedTest(name = "{0} reports \"{2}\"")
    @MethodSource("daoOperations")
    @DisplayName("A database error is rethrown with an operation-specific message and the SQLException intact")
    void databaseErrorIsWrappedNotSwallowed(String operation, Executable action, String expectedMessage) {

        // Act: the table does not exist, so the driver refuses the statement
        RuntimeException thrown = assertThrows(RuntimeException.class, action,
                operation + " returned normally instead of reporting the database failure");

        // Assert: the caller is told which operation failed...
        assertEquals(expectedMessage, thrown.getMessage());

        // ...and the driver's own complaint survives for whoever reads the log
        assertNotNull(thrown.getCause(), operation + " discarded the SQLException cause");
        assertInstanceOf(java.sql.SQLException.class, thrown.getCause());
    }
}
