package com.revature.expensemanager.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.revature.expensemanager.allure.ParentSuite;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * Real-HTTP coverage for the pending-expense queue and the manager review
 * endpoint.
 *
 * <p>This class spans two stories - viewing the queue (KAN-21) and approving or
 * denying an expense (KAN-22) - so the issue and story labels are applied per
 * test rather than to the class, the same way {@link EmployeesIT} does it.
 */
@Epic("Manager Portal Backend")
@ParentSuite("Manager - Integration Tests")
@Feature("Expense Approvals")
@Tag("api")
@Tag("integration")
@DisplayName("Manager Expense Review")
class ExpenseIT extends AbstractApiIT {

    private static final String REVIEW_SUCCESS_MESSAGE = "Expense reviewed successfully";
    private static final String REVIEW_FAILURE_MESSAGE = "Unable to review expense";
    private static final String INVALID_REVIEW_BODY_MESSAGE = "Invalid review request body.";

    // =====================================================================
    // GET /expenses/pending
    // =====================================================================

    @Test
    @Issue("KAN-21")
    @Story("Manager View Queue")
    @Tag("smoke")
    void validManagerCanGetExactlyTheSeededPendingExpenses() {
        Response response = given()
                .header("Authorization", "Bearer " + validManagerToken())
                .when()
                .get("/expenses/pending")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", instanceOf(List.class))
                .extract()
                .response();

        List<Integer> expenseIds = response.jsonPath().getList("id", Integer.class);

        assertEquals(3, expenseIds.size());
        assertEquals(Set.of(4, 6, 8), Set.copyOf(expenseIds));
    }

    // =====================================================================
    // PUT /expenses/{id}/review
    // =====================================================================

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("smoke")
    void managerCanApprovePendingExpense() {
        review(4, Map.of("status", "approved", "comment", "ok"))
                .then()
                .statusCode(200)
                .body("message", equalTo(REVIEW_SUCCESS_MESSAGE));

        assertExpenseIsNoLongerPending(4);
    }

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("smoke")
    void managerCanDenyPendingExpense() {
        review(6, Map.of("status", "denied", "comment", "no"))
                .then()
                .statusCode(200)
                .body("message", equalTo(REVIEW_SUCCESS_MESSAGE));

        assertExpenseIsNoLongerPending(6);
    }

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("edge_case")
    void reviewStatusIsCaseInsensitive() {
        review(8, Map.of("status", "APPROVED"))
                .then()
                .statusCode(200)
                .body("message", equalTo(REVIEW_SUCCESS_MESSAGE));

        assertExpenseIsNoLongerPending(8);
    }

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("negative")
    void nonNumericExpenseId_returns400() {
        given()
                .header("Authorization", "Bearer " + validManagerToken())
                .when()
                .put("/expenses/abc/review")
                .then()
                .statusCode(400)
                .body("message", equalTo("Invalid expense id."));
    }

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("negative")
    void invalidStatus_returns400() {
        review(8, Map.of("status", "maybe"))
                .then()
                .statusCode(400)
                .body("message", equalTo(REVIEW_FAILURE_MESSAGE));
    }

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("negative")
    void missingOrBlankStatus_returns400() {
        review(8, Map.of("comment", "x"))
                .then()
                .statusCode(400)
                .body("message", equalTo(REVIEW_FAILURE_MESSAGE));

        review(8, Map.of("status", "", "comment", "x"))
                .then()
                .statusCode(400)
                .body("message", equalTo(REVIEW_FAILURE_MESSAGE));
    }

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("negative")
    void unknownExpenseId_returns400() {
        review(9999, Map.of("status", "approved"))
                .then()
                .statusCode(400)
                .body("message", equalTo(REVIEW_FAILURE_MESSAGE));
    }

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("negative")
    void alreadyReviewedExpense_returns400() {
        review(1, Map.of("status", "approved"))
                .then()
                .statusCode(400)
                .body("message", equalTo(REVIEW_FAILURE_MESSAGE));
    }

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("security")
    @Tag("negative")
    void employeeTokenCannotReviewExpense() {
        // This confirms the review route itself enforces the manager-role rule.
        given()
                .header("Authorization", "Bearer " + validEmployeeToken())
                .when()
                .put("/expenses/4/review")
                .then()
                .statusCode(403)
                .body("message", equalTo("Manager access required."));
    }

    @Test
    @Issue("KAN-22")
    @Issue("KAN-90")
    @Story("Manager Approve Deny")
    @Tag("negative")
    @Tag("regression")
    void emptyReviewRequestBody_returns400WithClearErrorResponse() {
        given()
                .header("Authorization", "Bearer " + validManagerToken())
                .contentType(ContentType.JSON)
                .when()
                .put("/expenses/8/review")
                .then()
                .statusCode(400)
                .body("message", equalTo(INVALID_REVIEW_BODY_MESSAGE));
    }

    @Test
    @Issue("KAN-22")
    @Issue("KAN-90")
    @Story("Manager Approve Deny")
    @Tag("negative")
    @Tag("regression")
    void malformedReviewRequestBody_returns400WithClearErrorResponse() {
        given()
                .header("Authorization", "Bearer " + validManagerToken())
                .contentType(ContentType.JSON)
                .body("{\"status\": \"approved\"")
                .when()
                .put("/expenses/8/review")
                .then()
                .statusCode(400)
                .body("message", equalTo(INVALID_REVIEW_BODY_MESSAGE));
    }

    // =====================================================================
    // Helpers
    // =====================================================================

    private Response review(int expenseId, Map<String, String> requestBody) {
        return given()
                .header("Authorization", "Bearer " + validManagerToken())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/expenses/{id}/review", expenseId);
    }

    private void assertExpenseIsNoLongerPending(int expenseId) {
        List<Integer> pendingExpenseIds = given()
                .header("Authorization", "Bearer " + validManagerToken())
                .when()
                .get("/expenses/pending")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("id", Integer.class);

        assertFalse(pendingExpenseIds.contains(expenseId));
    }
}
