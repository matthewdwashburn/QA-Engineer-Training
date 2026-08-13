package com.revature.expensemanager.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.revature.expensemanager.allure.ParentSuite;
import com.revature.expensemanager.model.Expense;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Real-HTTP coverage for the three report endpoints:
 * {@code GET /reports/employee}, {@code /reports/category} and
 * {@code /reports/date}.
 *
 * <p><strong>Why these exist alongside the unit tests.</strong> The controller
 * branches are already covered against a mocked {@code Context}, where the mock
 * decides what {@code queryParam()} returns. That proves the branch logic but
 * not that a real query string ever reaches it: a renamed parameter, a bad
 * route path, or a missing {@code before} filter would leave every one of those
 * unit tests green. These tests drive real HTTP against the wired application,
 * so the query string, the routing, the auth filter, the DAO's SQL and the JSON
 * serialisation are all in the loop.
 *
 * <p><strong>What belongs here, and what does not.</strong> A case earns a place
 * in this class only if a mocked-{@code Context} test could not catch the failure
 * it targets. Concretely:
 *
 * <ul>
 *   <li>Validation branches and their exact messages belong to
 *       {@code ReportControllerTest}, which captures the serialised
 *       {@code ErrorResponse}. This class keeps <em>one</em> case per distinct
 *       message - enough to prove the branch is reachable over real HTTP, not a
 *       second enumeration of the same guard.</li>
 *   <li>Row filtering, bound inclusivity and column mapping belong to
 *       {@code JdbcReportDAOTest}, which drives the SQL directly.</li>
 *   <li>The shared auth contract belongs to {@code AuthContractIT}.</li>
 * </ul>
 *
 * <p>This class once held four cases for the single null/blank date guard and
 * three for the one {@code exportIfRequested} helper - written when it was the
 * only coverage of the report path, and left in place when the controller and
 * DAO layers were added underneath it. Re-driving a branch here that a lower
 * layer already owns costs a full database rebuild per case (see
 * {@code AbstractApiIT.resetDatabase}) and buys nothing.
 *
 * <p><strong>Seed data these assertions depend on</strong>
 * ({@code database/seed.sql}, reloaded {@code @BeforeEach}):
 *
 * <pre>
 * id  userId  category         date        amount
 *  1     1    MEALS            2026-07-01   23.75
 *  2     1    LODGING          2026-07-02  145.00
 *  3     1    EQUIPMENT        2026-07-03   49.99
 *  4     1    TRAVEL           2026-07-04   18.40
 *  5     2    OFFICE_SUPPLIES  2026-07-02   82.15
 *  6     2    SOFTWARE         2026-07-03  299.99
 *  7     2    MEALS            2026-07-04   12.80
 *  8     2    TRAINING         2026-07-05  525.00
 * </pre>
 *
 * <p>Users are {@code 1 brian} and {@code 2 landon} (employees) and
 * {@code 3 siri} (manager). Reports read the {@code expenses} table directly and
 * ignore approval state, so every row above is in scope regardless of whether it
 * was approved, denied or is still pending.
 *
 * <p><strong>Exported files.</strong> {@code reports/} is not git-ignored, so the
 * export cases below delete the CSV they cause to be written. Without that, a
 * test run leaves untracked files in the working tree.
 */
@Epic("Manager Portal Backend")
@ParentSuite("Manager - Integration Tests")
@Feature("Reporting")
@Issue("KAN-23")
@Story("Manager Reports")
@Tag("api")
@Tag("integration")
@DisplayName("Manager Reports")
class ReportsIT extends AbstractApiIT {

    private static final int BRIAN_ID = 1;
    private static final int MANAGER_ID = 3;
    private static final int UNKNOWN_USER_ID = 9999;

    /** Directory {@code ReportExportService} writes into, relative to the module. */
    private static final Path REPORTS_DIRECTORY = Path.of("reports");

    // One case per message, asserting the text rather than only the status: these
    // 400s are otherwise indistinguishable, so a status-only check here would not
    // notice a branch being reachable but answering with the wrong contract.
    // Enumerating the inputs that reach each message is ReportControllerTest's job.
    private static final String USER_ID_REQUIRED = "userId query parameter is required.";
    private static final String USER_ID_NOT_A_NUMBER = "userId must be a valid number.";
    private static final String USER_ID_NOT_POSITIVE = "userId must be a positive number.";
    private static final String EMPLOYEE_NOT_FOUND = "Employee not found.";
    private static final String INVALID_CATEGORY = "Invalid category. Valid categories: "
            + "TRAVEL, MEALS, LODGING, OFFICE_SUPPLIES, EQUIPMENT, SOFTWARE, TRAINING, OTHER.";
    private static final String DATES_REQUIRED = "startDate and endDate query parameters are required.";
    private static final String DATE_FORMAT_REQUIRED = "startDate and endDate must use YYYY-MM-DD format.";
    private static final String DATE_ORDER_REQUIRED = "startDate must be on or before endDate.";
    private static final String MANAGER_REQUIRED = "Manager access required.";

    // =====================================================================
    // GET /reports/employee
    // =====================================================================

    @Test
    @Tag("smoke")
    void existingEmployeeId_returns200WithOnlyThatEmployeesExpenses() {
        // Asserting the ids - not merely "200 with a list" - is the point: a
        // report that ignored its filter and returned all eight rows would pass
        // any status-only check while leaking one employee's spending to a
        // report requested about another.
        managerRequest()
                .queryParam("userId", BRIAN_ID)
                .when()
                .get("/reports/employee")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", containsInAnyOrder(1, 2, 3, 4))
                .body("userId", containsInAnyOrder(BRIAN_ID, BRIAN_ID, BRIAN_ID, BRIAN_ID));
    }

    @Test
    @Tag("smoke")
    void existingEmployeeId_returns200WithEveryExpenseFieldPopulated() {
        // Round-trips the payload through the model to prove the JSON keys match
        // what a client binds to; a renamed field would deserialise as null/0.
        Expense lunch = fetchExpenses(managerRequest().queryParam("userId", BRIAN_ID), "/reports/employee")
                .stream()
                .filter(expense -> expense.getId() == 1)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expense 1 is missing from brian's report."));

        assertAll(
                () -> assertEquals(BRIAN_ID, lunch.getUserId()),
                () -> assertEquals(23.75, lunch.getAmount(), 0.001),
                () -> assertEquals("Lunch with client", lunch.getDescription()),
                () -> assertEquals("MEALS", lunch.getCategory()),
                () -> assertEquals("2026-07-01", lunch.getDate()));
    }

    @Test
    @Tag("negative")
    void missingUserId_returns400AndContractMessage() {
        managerRequest()
                .when()
                .get("/reports/employee")
                .then()
                .statusCode(400)
                .body("message", equalTo(USER_ID_REQUIRED));
    }

    @Test
    @Tag("negative")
    void nonNumericUserId_returns400AndContractMessage() {
        managerRequest()
                .queryParam("userId", "abc")
                .when()
                .get("/reports/employee")
                .then()
                .statusCode(400)
                .body("message", equalTo(USER_ID_NOT_A_NUMBER));
    }

    @Test
    @Tag("negative")
    void zeroUserId_returns400AndContractMessage() {
        managerRequest()
                .queryParam("userId", 0)
                .when()
                .get("/reports/employee")
                .then()
                .statusCode(400)
                .body("message", equalTo(USER_ID_NOT_POSITIVE));
    }

    @Test
    @Tag("negative")
    void unknownUserId_returns404AndContractMessage() {
        managerRequest()
                .queryParam("userId", UNKNOWN_USER_ID)
                .when()
                .get("/reports/employee")
                .then()
                .statusCode(404)
                .body("message", equalTo(EMPLOYEE_NOT_FOUND));
    }

    @Test
    @Tag("negative")
    void managerUserId_returns404AndContractMessage() {
        // Id 3 exists in users but is a manager. ReportService.employeeExists
        // filters on the role, so an existing-but-wrong-role id must be treated
        // as not found rather than returning an empty report.
        managerRequest()
                .queryParam("userId", MANAGER_ID)
                .when()
                .get("/reports/employee")
                .then()
                .statusCode(404)
                .body("message", equalTo(EMPLOYEE_NOT_FOUND));
    }

    @Test
    @Tag("smoke")
    void employeeRouteWithExportTrue_returns200AndWritesTheReportCsv() throws IOException {
        Response response = managerRequest()
                .queryParam("userId", BRIAN_ID)
                .queryParam("export", "true")
                .when()
                .get("/reports/employee")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", containsInAnyOrder(1, 2, 3, 4))
                .extract()
                .response();

        assertCsvWrittenThenDelete(response.header("Report-File"), "employee_report", 4);
    }

    // =====================================================================
    // GET /reports/category
    // =====================================================================

    @Test
    @Tag("smoke")
    void validCategory_returns200WithOnlyThatCategorysExpenses() {
        // MEALS deliberately spans two employees (1 and 7), so this also proves
        // the category report is not implicitly scoped to a single user.
        managerRequest()
                .queryParam("category", "MEALS")
                .when()
                .get("/reports/category")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", containsInAnyOrder(1, 7))
                .body("category", containsInAnyOrder("MEALS", "MEALS"));
    }

    @Test
    @Tag("edge_case")
    void lowercaseAndPaddedCategory_returns200WithThatCategorysExpenses() {
        // ExpenseCategory.fromInput trims and upper-cases. Through a mocked
        // Context that is trivially true; over real HTTP it also depends on the
        // server decoding "%20meals%20" back to " meals " before the handler
        // sees it.
        managerRequest()
                .queryParam("category", " meals ")
                .when()
                .get("/reports/category")
                .then()
                .statusCode(200)
                .body("id", containsInAnyOrder(1, 7));
    }

    @Test
    @Tag("negative")
    void missingCategory_returns400AndContractMessage() {
        managerRequest()
                .when()
                .get("/reports/category")
                .then()
                .statusCode(400)
                .body("message", equalTo(INVALID_CATEGORY));
    }

    @Test
    @Tag("negative")
    void reportRouteWithEmployeeToken_returns403AndContractMessage() {
        // The one authorisation assertion kept on this route family. AuthContractIT
        // proves /reports/* is wired to the guard (401 without a header) and that
        // the guard 403s an employee token (on /expenses/pending); this composes
        // the two into a single end-to-end check on a report route, because an
        // authorisation boundary is worth one direct assertion rather than an
        // inference from two.
        given()
                .header("Authorization", "Bearer " + validEmployeeToken())
                .queryParam("category", "MEALS")
                .when()
                .get("/reports/category")
                .then()
                .statusCode(403)
                .body("message", equalTo(MANAGER_REQUIRED));
    }

    // =====================================================================
    // GET /reports/date
    // =====================================================================

    @Test
    @Tag("smoke")
    void validRange_returns200WithEveryExpenseInRange() {
        managerRequest()
                .queryParam("startDate", "2026-07-01")
                .queryParam("endDate", "2026-07-31")
                .when()
                .get("/reports/date")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", containsInAnyOrder(1, 2, 3, 4, 5, 6, 7, 8));
    }

    @Test
    @Tag("negative")
    void missingEndDate_returns400AndContractMessage() {
        managerRequest()
                .queryParam("startDate", "2026-07-01")
                .when()
                .get("/reports/date")
                .then()
                .statusCode(400)
                .body("message", equalTo(DATES_REQUIRED));
    }

    @Test
    @Tag("negative")
    void nonIsoDateFormat_returns400AndContractMessage() {
        managerRequest()
                .queryParam("startDate", "07/01/2026")
                .queryParam("endDate", "07/31/2026")
                .when()
                .get("/reports/date")
                .then()
                .statusCode(400)
                .body("message", equalTo(DATE_FORMAT_REQUIRED));
    }

    @Test
    @Tag("negative")
    void startDateAfterEndDate_returns400AndContractMessage() {
        managerRequest()
                .queryParam("startDate", "2026-07-31")
                .queryParam("endDate", "2026-07-01")
                .when()
                .get("/reports/date")
                .then()
                .statusCode(400)
                .body("message", equalTo(DATE_ORDER_REQUIRED));
    }

    // =====================================================================
    // Helpers
    // =====================================================================

    /** A request already carrying a valid manager bearer token. */
    private RequestSpecification managerRequest() {
        return given().header("Authorization", "Bearer " + validManagerToken());
    }

    /** Sends the request and binds the report body to the production model. */
    private List<Expense> fetchExpenses(RequestSpecification request, String path) {
        Expense[] expenses = request
                .when()
                .get(path)
                .then()
                .statusCode(200)
                .extract()
                .as(Expense[].class);

        return Arrays.asList(expenses);
    }

    /**
     * Verifies the exported CSV named by the {@code Report-File} header exists,
     * is named for the report that produced it, and holds a header row plus one
     * row per expense - then deletes it, since {@code reports/} is tracked.
     */
    private void assertCsvWrittenThenDelete(String reportFile, String expectedPrefix, int expectedRows)
            throws IOException {
        assertNotNull(reportFile, "export=true must return a Report-File header.");

        Path exported = Path.of(reportFile);
        try {
            assertAll(
                    () -> assertEquals(REPORTS_DIRECTORY, exported.getParent(),
                            "Exports belong in the reports directory."),
                    () -> assertTrue(exported.getFileName().toString().startsWith(expectedPrefix + "_"),
                            "Exported file should be named for its report, but was: " + exported.getFileName()),
                    () -> assertTrue(exported.getFileName().toString().endsWith(".csv"),
                            "Exported file should be a .csv, but was: " + exported.getFileName()),
                    () -> assertTrue(Files.isRegularFile(exported),
                            "Report-File named a file that was never written: " + exported),
                    // A header-only file would mean the export ran but serialised
                    // nothing - the failure mode a header-presence check misses.
                    () -> assertEquals(expectedRows + 1, Files.readAllLines(exported).size(),
                            "Exported CSV should hold a header row plus one row per expense."));
        } finally {
            Files.deleteIfExists(exported);
        }
    }
}
