package com.revature;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Demo: REST Assured Integration with JUnit5
 *
 * 1. JUnit5 provides the test framework, REST Assured handles HTTP
 * 2. Parameterized tests reduce code duplication
 * 3. @BeforeAll/@AfterAll for setup/teardown
 * 4. Combine JUnit assertions with Hamcrest matchers
 *
 * RUN THIS IN IDE:
 * - Note how parameterized tests appear as multiple test runs
 * - Each parameter combination is a separate test
 */
@DisplayName("REST Assured with JUnit5 Integration")
public class demo_rest_assured_junit5 {

    @BeforeAll
    static void setup() {
        /*
         * @BeforeAll runs once before all tests.
         * Perfect for base configuration.
         */
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        System.out.println("===== Test Suite Started =====");
    }

    @AfterAll
    static void teardown() {
        RestAssured.reset();
        System.out.println("===== Test Suite Completed =====");
    }

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        /*
         * @BeforeEach runs before each test.
         * TestInfo provides test metadata.
         */
        System.out.println("Running: " + testInfo.getDisplayName());
    }

    @AfterEach
    void afterEach() {
        // Cleanup after each test if needed
    }

    // ==========================================================
    // SECTION 1: Parameterized Tests with @ValueSource
    // ==========================================================

    @ParameterizedTest(name = "GET /posts/{0} returns 200")
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("GET multiple posts by ID")
    void getPost_variousIds_return200(int postId) {
        /*
         * @ValueSource provides simple values.
         * Each value becomes a separate test run.
         */

        given()
                .when()
                .get("/posts/" + postId)
                .then()
                .statusCode(200)
                .body("id", equalTo(postId));
    }

    @ParameterizedTest(name = "GET /users/{0} returns 200")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @DisplayName("Validate all 10 users exist")
    void getUser_allIds_return200(int userId) {
        given()
                .when()
                .get("/users/" + userId)
                .then()
                .statusCode(200)
                .body("id", equalTo(userId))
                .body("email", containsString("@"));
    }

    // ==========================================================
    // SECTION 2: Parameterized Tests with @CsvSource
    // ==========================================================

    @ParameterizedTest(name = "User {0} has name {1}")
    @CsvSource({
            "1, Leanne Graham",
            "2, Ervin Howell",
            "3, Clementine Bauch",
            "4, Patricia Lebsack",
            "5, Chelsey Dietrich"
    })
    @DisplayName("Validate user names")
    void getUser_validateName_matchesExpected(int userId, String expectedName) {
        /*
         * INSTRUCTOR NOTE:
         * @CsvSource for multiple parameters per test.
         * Format: "value1, value2, value3"
         */

        given()
                .when()
                .get("/users/" + userId)
                .then()
                .statusCode(200)
                .body("name", equalTo(expectedName));
    }

    @ParameterizedTest(name = "{0} /posts/{2} returns {3}")
    @CsvSource({
            "GET,  /posts, 1, 200",
            "GET,  /posts, 100, 200",
            "GET,  /posts, 999, 404",
            "DELETE, /posts, 1, 200"
    })
    @DisplayName("Various HTTP operations")
    void httpOperations_variousCases(String method, String endpoint, int id, int expectedStatus) {
        /*
         * INSTRUCTOR NOTE:
         * Test different methods and expected outcomes.
         */

        given()
                .contentType(ContentType.JSON)
                .when()
                .request(method, endpoint + "/" + id)
                .then()
                .statusCode(expectedStatus);
    }

    // ==========================================================
    // SECTION 3: Parameterized Tests with @CsvFileSource
    // ==========================================================

    /*
     * Uncomment and create test-data.csv in src/test/resources:
     *
     * userId,expectedName,expectedEmail
     * 1,Leanne Graham,Sincere@april.biz
     * 2,Ervin Howell,Shanna@melissa.tv
     * 3,Clementine Bauch,Nathan@yesenia.net
     */

    // @ParameterizedTest
    // @CsvFileSource(resources = "/test-data.csv", numLinesToSkip = 1)
    // @DisplayName("Data-driven user validation from CSV")
    // void validateUsers_fromCsvFile(int userId, String name, String email) {
    //     given()
    //     .when()
    //         .get("/users/" + userId)
    //     .then()
    //         .statusCode(200)
    //         .body("name", equalTo(name))
    //         .body("email", equalTo(email));
    // }

    // ==========================================================
    // SECTION 4: Parameterized Tests with @MethodSource
    // ==========================================================

    @ParameterizedTest(name = "Post {0}: userId={1}, has title")
    @MethodSource("postDataProvider")
    @DisplayName("Validate posts from method source")
    void validatePosts_fromMethodSource(int postId, int expectedUserId) {
        /*
         * @MethodSource for complex test data.
         * Data comes from static method.
         */

        given()
                .when()
                .get("/posts/" + postId)
                .then()
                .statusCode(200)
                .body("id", equalTo(postId))
                .body("userId", equalTo(expectedUserId))
                .body("title", notNullValue());
    }

    static Stream<Arguments> postDataProvider() {
        /*
         * This method provides test data.
         * Can be any complex logic to generate data.
         */
        return Stream.of(
                Arguments.of(1, 1),
                Arguments.of(11, 2),
                Arguments.of(21, 3),
                Arguments.of(31, 4),
                Arguments.of(41, 5)
        );
    }

    // ==========================================================
    // SECTION 5: @Nested Test Classes
    // ==========================================================

    @Nested
    @DisplayName("Posts Endpoint Tests")
    class PostsTests {

        @Test
        @DisplayName("GET /posts returns 100 posts")
        void getAllPosts_returns100() {
            given()
                    .when()
                    .get("/posts")
                    .then()
                    .statusCode(200)
                    .body("size()", equalTo(100));
        }

        @Test
        @DisplayName("POST /posts creates new post")
        void createPost_returns201() {
            String body = """
                {"title": "Nested Test", "body": "Content", "userId": 1}
                """;

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when()
                    .post("/posts")
                    .then()
                    .statusCode(201);
        }
    }

    @Nested
    @DisplayName("Users Endpoint Tests")
    class UsersTests {

        @Test
        @DisplayName("GET /users returns 10 users")
        void getAllUsers_returns10() {
            given()
                    .when()
                    .get("/users")
                    .then()
                    .statusCode(200)
                    .body("size()", equalTo(10));
        }

        @Test
        @DisplayName("All users have email addresses")
        void allUsers_haveEmails() {
            given()
                    .when()
                    .get("/users")
                    .then()
                    .statusCode(200)
                    .body("email", everyItem(containsString("@")));
        }
    }

    // ==========================================================
    // SECTION 6: more Junit
    // ==========================================================

    @Test
    @DisplayName("Extract and assert with JUnit")
    void extractAndAssert_withJUnit() {
        /*
         * Sometimes you need to extract values and use JUnit assertions.
         * This gives more flexibility for complex validations.
         */

        var response = given()
                .when()
                .get("/users")
                .then()
                .extract()
                .response();

        // JUnit assertions
        int statusCode = response.statusCode();
        Assertions.assertEquals(200, statusCode, "Status should be 200");

        int userCount = response.jsonPath().getList("$").size();
        Assertions.assertEquals(10, userCount, "Should have 10 users");

        String firstUserName = response.jsonPath().getString("[0].name");
        Assertions.assertNotNull(firstUserName, "First user should have a name");
        Assertions.assertFalse(firstUserName.isEmpty(), "Name should not be empty");
    }

    @Test
    @DisplayName("AssertAll for multiple validations")
    void assertAll_multipleValidations() {
        /*
         * assertAll runs all assertions even if some fail.
         * Shows ALL failures, not just the first one.
         */

        var response = given()
                .when()
                .get("/users/1")
                .then()
                .extract()
                .response();

        var json = response.jsonPath();

        Assertions.assertAll("User Validations",
                () -> Assertions.assertEquals(200, response.statusCode()),
                () -> Assertions.assertEquals(1, json.getInt("id")),
                () -> Assertions.assertEquals("Leanne Graham", json.getString("name")),
                () -> Assertions.assertTrue(json.getString("email").contains("@")),
                () -> Assertions.assertNotNull(json.getString("address.city"))
        );
    }

    // ==========================================================
    // SECTION 7: Conditional Tests
    // ==========================================================

    @Test
    @DisplayName("Test only when API is available")
    @EnabledIf("isApiAvailable")
    void conditionalTest_whenApiAvailable() {
        /*
         * Conditional tests run only when condition is true.
         * Useful for environment-specific tests.
         */

        given()
                .when()
                .get("/posts/1")
                .then()
                .statusCode(200);
    }

    static boolean isApiAvailable() {
        try {
            int status = given()
                    .baseUri("https://jsonplaceholder.typicode.com")
                    .when()
                    .get("/posts/1")
                    .then()
                    .extract()
                    .statusCode();
            return status == 200;
        } catch (Exception e) {
            return false;
        }
    }

    // ==========================================================
    // SECTION 8: Disabled Tests
    // ==========================================================

    @Test
    @Disabled("Demonstrating disabled test")
    @DisplayName("This test is skipped")
    void disabledTest_notExecuted() {
        /*
         *
         * @Disabled skips the test with a reason.
         * Use for tests that are temporarily broken or not implemented.
         */
        Assertions.fail("This should not run");
    }

    // ==========================================================
    // SECTION 9: Timeouts
    // ==========================================================

    @Test
    @Timeout(5)  // 5 seconds timeout
    @DisplayName("Test with timeout")
    void testWithTimeout_mustCompleteIn5Seconds() {
        /*
         * @Timeout fails the test if it takes too long.
         * Protects against hanging tests.
         */

        given()
                .when()
                .get("/posts")
                .then()
                .statusCode(200);
    }


}


