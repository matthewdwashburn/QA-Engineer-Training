package com.revature.expensemanager.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.revature.expensemanager.allure.ParentSuite;
import com.revature.expensemanager.config.DatabaseConfig;
import com.revature.expensemanager.model.Expense;
import com.revature.expensemanager.testsupport.TestPostgres;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;

/**
 * SQL-level coverage for {@link JdbcReportDAO} against a real Postgres database.
 *
 * <p>Until now the DAOs were the one layer nothing tested directly - the pom
 * excludes {@code **}{@code /dao/**} from JaCoCo on the grounds that "DAOs are
 * verified through the controller layer", which was an assertion rather than a
 * measurement. Everything asserted here is a property of the SQL: which rows a
 * WHERE clause selects, whether BETWEEN is inclusive, and how a row becomes an
 * {@link Expense}. A mocked DAO cannot get any of it wrong, and an HTTP test
 * that fails on it cannot tell you which layer broke.
 *
 * <p>The fixture is built once for the class rather than per test. Nothing here
 * writes, so there is no state to reset - and rebuilding per test is what makes
 * the HTTP suite slow.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("Manager Portal Backend")
@Feature("Reporting")
@ParentSuite("Manager - Repository Layer")
@Issue("KAN-23")
@Story("Manager Reports")
@Tag("db")
@DisplayName("Report DAO - SQL")
class JdbcReportDAOTest {

    private final JdbcReportDAO reportDAO = new JdbcReportDAO();

    private String previousDatabaseUrl;

    @BeforeAll
    void buildDatabase() throws IOException {
        previousDatabaseUrl = TestPostgres.claim(TestPostgres.databaseUrlFor("report_dao_test"));

        try (Connection connection = DatabaseConfig.getConnection();
                Statement statement = connection.createStatement()) {

            executeSqlFile(statement, databaseScript("schema.sql"));
            seedUsers(connection);
            executeSqlFile(statement, databaseScript("seed.sql"));

        } catch (SQLException e) {
            throw new IllegalStateException("Unable to build the report DAO test database.", e);
        }
    }

    @AfterAll
    void tearDownDatabase() {
        TestPostgres.release(previousDatabaseUrl);
    }

    @Nested
    @DisplayName("findExpensesByEmployee")
    class FindByEmployee {

        @Test
        @Tag("smoke")
        @DisplayName("Returns every row belonging to the employee and none belonging to anyone else")
        void returnsOnlyThatEmployeesRows() {

            // Act
            List<Expense> expenses = reportDAO.findExpensesByEmployee(1);

            // Assert: the seed gives user 1 exactly ids 1-4
            assertEquals(Set.of(1, 2, 3, 4), idsOf(expenses));
            assertTrue(expenses.stream().allMatch(expense -> expense.getUserId() == 1));
        }

        @Test
        @Tag("smoke")
        @DisplayName("Maps every selected column onto the Expense object")
        void mapsAllColumns() {

            // Act
            Expense expense = reportDAO.findExpensesByEmployee(1).stream()
                    .filter(candidate -> candidate.getId() == 1)
                    .findFirst()
                    .orElseThrow();

            // Assert: a column dropped from the mapper is invisible to a test
            // that only counts rows, so every field is named here.
            assertEquals(1, expense.getId());
            assertEquals(1, expense.getUserId());
            assertEquals(23.75, expense.getAmount(), 0.001);
            assertEquals("Lunch with client", expense.getDescription());
            assertEquals("MEALS", expense.getCategory());
            assertEquals("2026-07-01", expense.getDate());
        }

        @Test
        @Tag("edge_case")
        @DisplayName("A user who exists but has filed nothing returns an empty list, not null")
        void existingUserWithNoExpensesReturnsEmpty() {

            // Act: user 3 is the manager, and files no expenses
            List<Expense> expenses = reportDAO.findExpensesByEmployee(3);

            // Assert
            assertTrue(expenses.isEmpty());
        }

        @Test
        @Tag("edge_case")
        @DisplayName("An unknown user id returns an empty list rather than throwing")
        void unknownUserReturnsEmpty() {

            // Act
            List<Expense> expenses = reportDAO.findExpensesByEmployee(999);

            // Assert
            assertTrue(expenses.isEmpty());
        }
    }

    @Nested
    @DisplayName("findExpensesByCategory")
    class FindByCategory {

        @Test
        @Tag("smoke")
        @DisplayName("Returns matching rows across all employees")
        void returnsMatchingRowsAcrossEmployees() {

            // Act
            List<Expense> expenses = reportDAO.findExpensesByCategory("MEALS");

            // Assert: id 1 belongs to user 1 and id 7 to user 2 - a category
            // report deliberately crosses employee boundaries
            assertEquals(Set.of(1, 7), idsOf(expenses));
        }

        @ParameterizedTest(name = "category=[{0}] matches nothing")
        @ValueSource(strings = { "meals", "Meals", " MEALS ", "mEaLs" })
        @Tag("edge_case")
        @DisplayName("The SQL comparison is exact - casing and padding are the caller's problem")
        void categoryMatchIsExact(String category) {

            // Act
            List<Expense> expenses = reportDAO.findExpensesByCategory(category);

            // Assert: this is why ReportController normalises through
            // ExpenseCategory.fromInput before it ever reaches this query.
            // Reaching the DAO with raw user input would silently return nothing.
            assertTrue(expenses.isEmpty());
        }

        @Test
        @Tag("edge_case")
        @DisplayName("A category no expense uses returns an empty list")
        void unusedCategoryReturnsEmpty() {

            // Act: OTHER is a valid schema category that the seed never uses
            List<Expense> expenses = reportDAO.findExpensesByCategory("OTHER");

            // Assert
            assertTrue(expenses.isEmpty());
        }

        @Test
        @Tag("security")
        @DisplayName("A SQL metacharacter payload is bound as a literal, not executed")
        void categoryIsBoundNotConcatenated() {

            // Act: were the clause built by concatenation, this would return
            // every row in the table
            List<Expense> expenses = reportDAO.findExpensesByCategory("MEALS' OR '1'='1");

            // Assert: bound as one nonsense category, matching nothing
            assertTrue(expenses.isEmpty());
        }
    }

    @Nested
    @DisplayName("findExpensesByDateRange")
    class FindByDateRange {

        @Test
        @Tag("smoke")
        @DisplayName("Both bounds are inclusive")
        void bothBoundsAreInclusive() {

            // Act
            List<Expense> expenses = reportDAO.findExpensesByDateRange("2026-07-02", "2026-07-04");

            // Assert: ids 2 and 5 sit on the start bound, 4 and 7 on the end
            // bound - an exclusive BETWEEN would drop all four
            assertEquals(Set.of(2, 3, 4, 5, 6, 7), idsOf(expenses));
        }

        @Test
        @Tag("edge_case")
        @DisplayName("Rows outside the range are excluded")
        void excludesRowsOutsideTheRange() {

            // Act
            List<Expense> expenses = reportDAO.findExpensesByDateRange("2026-07-03", "2026-07-03");

            // Assert: only the two rows dated 07-03
            assertEquals(Set.of(3, 6), idsOf(expenses));
        }

        @Test
        @Tag("edge_case")
        @DisplayName("An identical start and end date selects that single day")
        void singleDayRange() {

            // Act
            List<Expense> expenses = reportDAO.findExpensesByDateRange("2026-07-01", "2026-07-01");

            // Assert
            assertEquals(Set.of(1), idsOf(expenses));
        }

        @Test
        @Tag("negative")
        @DisplayName("A start date after the end date returns nothing rather than throwing")
        void invertedRangeReturnsEmpty() {

            // Act: the controller rejects this before it gets here, but the DAO
            // must not blow up if anything else calls it directly
            List<Expense> expenses = reportDAO.findExpensesByDateRange("2026-07-05", "2026-07-01");

            // Assert
            assertTrue(expenses.isEmpty());
        }

        @Test
        @Tag("edge_case")
        @DisplayName("A range covering everything returns every seeded expense")
        void wideRangeReturnsAllRows() {

            // Act
            List<Expense> expenses = reportDAO.findExpensesByDateRange("2026-01-01", "2026-12-31");

            // Assert: dates are stored as ISO text, so lexical ordering and
            // calendar ordering agree - this is what makes BETWEEN safe here
            assertEquals(Set.of(1, 2, 3, 4, 5, 6, 7, 8), idsOf(expenses));
        }
    }

    private static Set<Integer> idsOf(List<Expense> expenses) {
        return expenses.stream().map(Expense::getId).collect(Collectors.toSet());
    }

    /**
     * Users exist only to satisfy the {@code expenses.userId} foreign key, and
     * the report queries never read a password. Storing a plain placeholder
     * keeps this fixture off bcrypt entirely.
     */
    private void seedUsers(Connection connection) throws SQLException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            insertUser(statement, "brian", "employee");
            insertUser(statement, "landon", "employee");
            insertUser(statement, "siri", "manager");
        }
    }

    private void insertUser(PreparedStatement statement, String username, String role) throws SQLException {
        statement.setString(1, username);
        statement.setString(2, "not-a-real-hash");
        statement.setString(3, role);
        statement.executeUpdate();
    }

    private void executeSqlFile(Statement statement, Path script) throws IOException, SQLException {
        for (String sql : Files.readString(script).split(";")) {
            if (!sql.isBlank()) {
                statement.execute(sql);
            }
        }
    }

    private Path databaseScript(String name) {
        Path fromManagerApp = Path.of("..", "database", name);
        if (Files.isRegularFile(fromManagerApp)) {
            return fromManagerApp;
        }

        Path fromRepositoryRoot = Path.of("database", name);
        if (Files.isRegularFile(fromRepositoryRoot)) {
            return fromRepositoryRoot;
        }

        throw new IllegalStateException("Cannot locate database script: " + name);
    }
}
