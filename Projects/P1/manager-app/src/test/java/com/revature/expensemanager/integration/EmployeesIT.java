package com.revature.expensemanager.integration;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.revature.expensemanager.allure.ParentSuite;
import com.revature.expensemanager.dto.EmployeeSummary;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;

/**
 * Real-HTTP coverage for {@code GET /employees}.
 *
 * <p><strong>Regression history.</strong> This endpoint shipped unauthenticated
 * and served the full employee roster - every user id and username - to any
 * anonymous caller. The authorization tests below exist because of that defect;
 * please do not weaken them.
 *
 * <ul>
 * <li><b>82e65cc</b> "rebase: Initial merge with Group8 Project 0 for
 * foundation" introduced the route in {@code Main.java}. It was appended
 * directly beneath the three {@code /reports/*} handlers, but with the path
 * {@code /employees} rather than {@code /reports/employees}.</li>
 * <li><b>8bb56bd</b> "app factory to allow integration tests" moved the routing
 * block verbatim from {@code Main.java} into {@code AppFactory}. The defect
 * moved with it, untouched.</li>
 * <li><b>f22dac1</b> "added AUTH guard to /employees endpoint" added the missing
 * {@code before} filter and closed the hole.</li>
 * </ul>
 *
 * <p><strong>Why it was missed.</strong> Authorization here is applied by URL
 * prefix - {@code before("/expenses/*")} and {@code before("/reports/*")} - but
 * the routes are read as a visual block. {@code /employees} sits immediately
 * under the report handlers and looks like it belongs to that group, yet it
 * matches neither prefix, so {@code AuthMiddleware.requireManager} never ran
 * against it. Proximity in the source is not membership in a path pattern. Any
 * future route added outside those two prefixes needs its own guard, and
 * {@code /employees} needs an exact-path filter because the {@code *} wildcard
 * does not match the bare path.
 */
@Epic("Manager Portal Backend")
@ParentSuite("Manager - Integration Tests")
@Tag("api")
@Tag("integration")
@DisplayName("Viewing the Employee Roster")
class EmployeesIT extends AbstractApiIT {

    /** Every seeded employee, in the {@code ORDER BY username} order the DAO applies. */
    private static final List<EmployeeSummary> EXPECTED_ROSTER = List.of(
            new EmployeeSummary(1, "brian"),
            new EmployeeSummary(2, "landon"));

    // ---------------------------------------------------------------------
    // Authorization contract - regression coverage for the defect above
    // ---------------------------------------------------------------------

    @Test
        @Feature("Authentication")
        @Issue("KAN-20")
        @Story("Manager Login")
        @Tag("security")
        @Tag("negative")
    void anonymousCallerIsRejected() {
        // The exact request that used to leak the roster.
        given()
                .when()
                .get("/employees")
                .then()
                .statusCode(401);
    }

    @Test
        @Feature("Authentication")
        @Issue("KAN-20")
        @Story("Manager Login")
        @Tag("security")
        @Tag("negative")
    void anonymousCallerReceivesNoRosterData() {
        // A status assertion alone would still pass if the body leaked alongside
        // an error code, so check that no employee data rides along with the 401.
        String body = given()
                .when()
                .get("/employees")
                .then()
                .statusCode(401)
                .extract()
                .asString();

        assertAll(
                () -> assertFalse(body.contains("brian"), "A rejected request must not leak usernames."),
                () -> assertFalse(body.contains("landon"), "A rejected request must not leak usernames."));
    }

    @Test
        @Feature("Authentication")
        @Issue("KAN-20")
        @Story("Manager Login")
        @Tag("security")
        @Tag("negative")
    void employeeTokenIsForbidden() {
        given()
                .header("Authorization", "Bearer " + validEmployeeToken())
                .when()
                .get("/employees")
                .then()
                .statusCode(403);
    }

    @Test
        @Feature("Authentication")
        @Issue("KAN-20")
        @Story("Manager Login")
        @Tag("security")
        @Tag("negative")
    void expiredManagerTokenIsRejected() {
        given()
                .header("Authorization", "Bearer " + expiredManagerToken())
                .when()
                .get("/employees")
                .then()
                .statusCode(401);
    }

    @Test
        @Feature("Authentication")
        @Issue("KAN-20")
        @Story("Manager Login")
        @Tag("security")
        @Tag("negative")
    void malformedTokenIsRejected() {
        given()
                .header("Authorization", "Bearer not.a.jwt")
                .when()
                .get("/employees")
                .then()
                .statusCode(401);
    }

    @Test
        @Feature("Authentication")
        @Issue("KAN-20")
        @Story("Manager Login")
        @Tag("security")
        @Tag("negative")
    void nonBearerAuthorizationSchemeIsRejected() {
        given()
                .header("Authorization", "Basic c2lyaTpwYXNzd29yZA==")
                .when()
                .get("/employees")
                .then()
                .statusCode(401);
    }

    @Test
        @Feature("Authentication")
        @Issue("KAN-20")
        @Story("Manager Login")
        @Tag("security")
        @Tag("negative")
    void blankAuthorizationHeaderIsRejected() {
        given()
                .header("Authorization", "")
                .when()
                .get("/employees")
                .then()
                .statusCode(401);
    }

    // ---------------------------------------------------------------------
    // Manager happy path and roster payload
    // ---------------------------------------------------------------------

    @Test
        @Feature("Reporting")
        @Issue("KAN-23")
        @Story("Manager Reports")
        @Tag("smoke")
    void managerReceivesTheEmployeeRoster() {
        given()
                .header("Authorization", "Bearer " + validManagerToken())
                .when()
                .get("/employees")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
        @Feature("Reporting")
        @Issue("KAN-23")
        @Story("Manager Reports")
        @Tag("smoke")
    void managerAuthenticatedThroughLoginReceivesTheRoster() {
        // Exercises the token the real /login endpoint issues, not a hand-built one.
        given()
                .header("Authorization", "Bearer " + loginAsManager())
                .when()
                .get("/employees")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
        @Feature("Reporting")
        @Issue("KAN-23")
        @Story("Manager Reports")
        @Tag("smoke")
    void rosterMatchesEverySeededEmployeeInUsernameOrder() {
        List<EmployeeSummary> roster = fetchRoster(validManagerToken());

        assertEquals(EXPECTED_ROSTER.size(), roster.size(),
                "Roster should contain exactly the seeded employee accounts.");

        assertAll(
                () -> assertEquals(EXPECTED_ROSTER.get(0).getId(), roster.get(0).getId()),
                () -> assertEquals(EXPECTED_ROSTER.get(0).getUsername(), roster.get(0).getUsername()),
                () -> assertEquals(EXPECTED_ROSTER.get(1).getId(), roster.get(1).getId()),
                () -> assertEquals(EXPECTED_ROSTER.get(1).getUsername(), roster.get(1).getUsername()));
    }

    @Test
        @Feature("Reporting")
        @Issue("KAN-23")
        @Story("Manager Reports")
        @Tag("smoke")
    void rosterExcludesManagerAccounts() {
        List<String> usernames = fetchRoster(validManagerToken()).stream()
                .map(EmployeeSummary::getUsername)
                .toList();

        assertAll(
                () -> assertTrue(usernames.contains("brian"), "Employee brian should be listed."),
                () -> assertTrue(usernames.contains("landon"), "Employee landon should be listed."),
                () -> assertFalse(usernames.contains("siri"), "Manager siri must not appear in the roster."));
    }

    @Test
        @Feature("Reporting")
        @Issue("KAN-23")
        @Story("Manager Reports")
        @Tag("smoke")
    void rosterEntriesNeverExposeCredentials() {
        // The DAO selects password and role but EmployeeSummary must not leak them.
        String body = given()
                .header("Authorization", "Bearer " + validManagerToken())
                .when()
                .get("/employees")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        assertAll(
                () -> assertFalse(body.contains("password"), "Response must not expose a password field."),
                () -> assertFalse(body.contains("$2a$"), "Response must not expose a bcrypt hash."),
                () -> assertFalse(body.contains("role"), "Response must not expose a role field."));
    }

    /** Fetches and deserialises the roster for the supplied manager token. */
    private List<EmployeeSummary> fetchRoster(String token) {
        EmployeeSummary[] roster = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/employees")
                .then()
                .statusCode(200)
                .extract()
                .as(EmployeeSummary[].class);

        return Arrays.asList(roster);
    }
}
