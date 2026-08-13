package com.revature;

//REST assured Setup and First Tests

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("REST Assured Setup Demo")
public class demo_rest_assured_setup {

    //Setup

    @BeforeAll
    static void setup(){
        //configure base URI once for all tests
        //This is like setting baseUrl in Postman

        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

        //Optional: Enable loggin and debugging
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();


    }
    @AfterAll
    static void teardown(){
        //Reset REST Assured to Defaults
        RestAssured.reset();
    }

    //First REST Assured Test
    @Test
    @DisplayName("First REST Assured Test - GET a post")
    void firstTest_getPost_returnSuccessfully() {

        given() //Setup
            .log().all() //Log the request (optional)
        .when() //Action
            .get("/posts/1")
        .then() //Validation
            .log().all() //log the response
            .statusCode(200);
    }

    //Given-When-Then Breakdown

    @Test
    @DisplayName("Detailed Given-When-Then example")
    void givenWhenThen_detailed_breakdown(){

        given()
            //Headers
            .header("Accept","application/json")
            //Query Parameters
            .queryParam("userId",1)

        .when()
            //HTTP method +endpoint
            .get("/posts")

        .then()
            //status code validation
            .statusCode(200)
            //Content-Type validation
            .contentType(ContentType.JSON)
            //Body validation using Hamcrest matchers
            .body("size()",greaterThan(0));

    }

//Response Body Validation

    @Test
    @DisplayName("Validate response body fields")
    void validateBody_postFields_areCorrect() {

        given()
            .when()
                .get("/posts/1")
            .then()
                .statusCode(200)
                //Validate specific fields using JSONPath
                .body("id",equalTo(1))
                .body("userId",equalTo(1))
                .body("title",notNullValue())
                .body("title",not(emptyString()))
                .body("body",containsString("quia"));
    }

    @Test
    @DisplayName("Validate user with nested objects")
    void validateBody_nestedObjects_accessedCorrectly() {
        /*
         *
         * Show how to access nested JSON properties.
         * address.city, address.geo.lat, etc.
         */

        given()
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("Leanne Graham"))
                .body("email", containsString("@"))
                // Nested object access
                .body("address.city", equalTo("Gwenborough"))
                .body("address.geo.lat", notNullValue())
                // Company nested object
                .body("company.name", equalTo("Romaguera-Crona"));
    }

    // ==========================================================
    // SECTION 5: Response Time Validation
    // ==========================================================

    @Test
    @DisplayName("Validate response time")
    void validateResponseTime_isAcceptable() {
        /*
         *
         * Response time matters for performance.
         * This is a quick smoke test - JMeter is better for load testing.
         */

        given()
                .when()
                .get("/posts/1")
                .then()
                .statusCode(200)
                .time(lessThan(5000L)); // Less than 5 seconds
    }

    // ==========================================================
    // SECTION 6: Extracting Response Data
    // ==========================================================

    @Test
    @DisplayName("Extract response for further use")
    void extractResponse_forFurtherProcessing() {
        /*
         *
         * Sometimes we need to capture data for use in other tests.
         * Similar to Postman's pm.environment.set() pattern.
         */

        // Extract entire response
        Response response = given()
                .when()
                .get("/posts/1")
                .then()
                .extract()
                .response();

        // Now we can work with the response object
        int statusCode = response.getStatusCode();
        String title = response.jsonPath().getString("title");
        int userId = response.jsonPath().getInt("userId");

        System.out.println("Status: " + statusCode);
        System.out.println("Title: " + title);
        System.out.println("User ID: " + userId);

        // Use JUnit assertions on extracted values
        Assertions.assertEquals(200, statusCode);
        Assertions.assertNotNull(title);
        Assertions.assertEquals(1, userId);
    }

    @Test
    @DisplayName("Extract specific path value")
    void extractPath_singleValue() {
        /*
         *
         * Quick way to extract just one value.
         */

        String email = given()
                .when()
                .get("/users/1")
                .then()
                .extract()
                .path("email");

        System.out.println("User email: " + email);
        Assertions.assertTrue(email.contains("@"));
    }

    // ==========================================================
    // SECTION 7: Logging Options
    // ==========================================================

    @Test
    @DisplayName("Demonstrate logging options")
    void loggingOptions_variousLevels() {
        /*
         *
         * Logging is crucial for debugging.
         * Show different log levels.
         */

        given()
                .log().method()      // Log HTTP method
                .log().uri()         // Log full URI
                .log().headers()     // Log request headers
                .log().body()        // Log request body (if any)
                .when()
                .get("/posts/1")
                .then()
                .log().status()      // Log status code
                .log().headers()     // Log response headers
                .log().body()        // Log response body
                .statusCode(200);
    }

    @Test
    @DisplayName("Log only on failure")
    void loggingOnFailure_forCleanOutput() {
        /*
         *
         * In CI/CD, you want clean output unless something fails.
         * This logs only when validation fails.
         */

        given()
                .log().ifValidationFails()
                .when()
                .get("/posts/1")
                .then()
                .log().ifValidationFails()
                .statusCode(200);
    }


}
