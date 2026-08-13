package com.revature.expensemanager.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.Map;

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
 * Real-HTTP coverage for {@code POST /login}, the only unauthenticated route.
 *
 * <p>Every rejection path asserts the same generic message. That is deliberate:
 * a wrong password, an unknown username and a valid employee account must be
 * indistinguishable to the caller, or the response itself becomes a username
 * oracle.
 */
@Epic("Manager Portal Backend")
@ParentSuite("Manager - Integration Tests")
@Feature("Authentication")
@Issue("KAN-20")
@Story("Manager Login")
@Tag("api")
@Tag("integration")
@DisplayName("Manager Login")
class LoginIT extends AbstractApiIT {

    private static final String LOGIN_FAILURE_MESSAGE = "Invalid username or password. This login portal is for managers only.";

    private static final String INVALID_REQUEST_BODY_MESSAGE = "Invalid login request body.";

    @Test
    @Tag("smoke")
    void validManagerCredentials_returnCompleteLoginResponse() {
        login(Map.of("username", "siri", "password", "password"))
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(3))
                .body("username", equalTo("siri"))
                .body("role", equalTo("manager"))
                .body("token", not(nullValue()))
                .body("token", not(equalTo("")));
    }

    @Test
    @Tag("security")
    @Tag("negative")
    void wrongPassword_returnsGenericUnauthorizedResponse() {
        login(Map.of("username", "siri", "password", "wrong-password"))
                .then()
                .statusCode(401)
                .body("message", equalTo(LOGIN_FAILURE_MESSAGE));
    }

    @Test
    @Tag("security")
    @Tag("negative")
    void unknownUsername_returnsGenericUnauthorizedResponse() {
        login(Map.of("username", "nobody", "password", "password"))
                .then()
                .statusCode(401)
                .body("message", equalTo(LOGIN_FAILURE_MESSAGE));
    }

    @Test
    @Tag("security")
    @Tag("negative")
    void validEmployeeCredentials_returnGenericUnauthorizedResponse() {
        login(Map.of("username", "brian", "password", "password"))
                .then()
                .statusCode(401)
                .body("message", equalTo(LOGIN_FAILURE_MESSAGE));
    }

    @Test
    @Tag("negative")
    void blankCredentials_returnGenericUnauthorizedResponse() {
        login(Map.of("username", "", "password", ""))
                .then()
                .statusCode(401)
                .body("message", equalTo(LOGIN_FAILURE_MESSAGE));
    }

    @Test
    @Issue("KAN-87")
    @Tag("negative")
    @Tag("regression")
    void emptyRequestBody_returns400WithClearErrorResponse() {
        loginWithoutBody()
                .then()
                .statusCode(400)
                .body("message", equalTo(INVALID_REQUEST_BODY_MESSAGE));
    }

    @Test
    @Issue("KAN-87")
    @Tag("negative")
    @Tag("regression")
    void malformedJsonRequestBody_returns400WithClearErrorResponse() {
        login("{\"username\": \"siri\"")
                .then()
                .statusCode(400)
                .body("message", equalTo(INVALID_REQUEST_BODY_MESSAGE));
    }

    private Response login(Map<String, String> requestBody) {
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/login");
    }

    private Response login(String requestBody) {
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/login");
    }

    private Response loginWithoutBody() {
        return given()
                .contentType(ContentType.JSON)
                .when()
                .post("/login");
    }
}
