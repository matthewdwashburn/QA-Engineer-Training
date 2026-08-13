package com.revature.expensemanager.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.revature.expensemanager.allure.ParentSuite;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;

/**
 * HTTP contract tests for the shared manager-authentication middleware.
 *
 * <p>
 * The complete shared auth contract runs once against a representative
 * protected
 * endpoint `GET /expenses/pending`. Repeating it per route would retest the
 * same middleware branches
 * without materially increasing confidence; individual endpoint happy paths
 * belong in their respective integration-test classes.
 */
@Epic("Manager Portal Backend")
@ParentSuite("Manager - Integration Tests")
@Feature("Authentication")
@Issue("KAN-20")
@Story("Manager Login")
@Tag("api")
@Tag("integration")
@DisplayName("Authentication Contract")
class AuthContractIT extends AbstractApiIT {

    private static final String MISSING_HEADER_MESSAGE = "Authorization header is required.";
    private static final String BEARER_SCHEME_MESSAGE = "Authorization header must use Bearer token.";
    private static final String INVALID_TOKEN_MESSAGE = "Invalid or expired token.";
    private static final String MANAGER_REQUIRED_MESSAGE = "Manager access required.";

    @Test
    @Tag("security")
    @Tag("negative")
    void missingAuthorizationHeader_returns401AndContractMessage() {
        given()
                .when()
                .get("/expenses/pending")
                .then()
                .statusCode(401)
                .body("message", equalTo(MISSING_HEADER_MESSAGE));
    }

    @Test
    @Tag("security")
    @Tag("negative")
    void nonBearerAuthorizationScheme_returns401AndContractMessage() {
        given()
                .header("Authorization", "Basic xyz")
                .when()
                .get("/expenses/pending")
                .then()
                .statusCode(401)
                .body("message", equalTo(BEARER_SCHEME_MESSAGE));
    }

    @Test
    @Tag("security")
    @Tag("negative")
    void malformedBearerToken_returns401AndContractMessage() {
        given()
                .header("Authorization", "Bearer garbage.token")
                .when()
                .get("/expenses/pending")
                .then()
                .statusCode(401)
                .body("message", equalTo(INVALID_TOKEN_MESSAGE));
    }

    @Test
    @Tag("security")
    @Tag("negative")
    void expiredManagerToken_returns401AndContractMessage() {
        given()
                .header("Authorization", "Bearer " + expiredManagerToken())
                .when()
                .get("/expenses/pending")
                .then()
                .statusCode(401)
                .body("message", equalTo(INVALID_TOKEN_MESSAGE));
    }

    @Test
    @Tag("security")
    @Tag("negative")
    void validEmployeeToken_returns403AndContractMessage() {
        // /login intentionally issues tokens only to managers, so this role branch
        // must use the production-compatible employee JWT provided by the base class.
        given()
                .header("Authorization", "Bearer " + validEmployeeToken())
                .when()
                .get("/expenses/pending")
                .then()
                .statusCode(403)
                .body("message", equalTo(MANAGER_REQUIRED_MESSAGE));
    }

    @Test
    @Tag("security")
    @Tag("smoke")
    void validManagerToken_allowsTheRequestToReachTheEndpointHandler() {
        given()
                .header("Authorization", "Bearer " + validManagerToken())
                .when()
                .get("/expenses/pending")
                .then()
                .statusCode(200)
                .body("$", instanceOf(List.class));
    }

    @Test
    @Tag("security")
    @Tag("negative")
    void reportsRouteFamily_rejectsMissingAuthorizationHeader() {
        // This is a separate before-filter path from /expenses/*, so one check
        // confirms that the report route family is wired to the shared guard.
        given()
                .when()
                .get("/reports/employee")
                .then()
                .statusCode(401)
                .body("message", equalTo(MISSING_HEADER_MESSAGE));
    }

    @Test
    @Tag("security")
    @Tag("negative")
    void employeesRoute_rejectsMissingAuthorizationHeader() {
        // /employees is a top-level route and was previously unprotected. This
        // regression assertion verifies its planned dedicated auth configuration.
        given()
                .when()
                .get("/employees")
                .then()
                .statusCode(401)
                .body("message", equalTo(MISSING_HEADER_MESSAGE));
    }
}
