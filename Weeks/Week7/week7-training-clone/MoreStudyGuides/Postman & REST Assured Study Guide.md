# Postman & REST Assured Study Guide

## Integration Testing Overview

### Integration Testing Introduction
**Integration Testing** is the process of testing how different modules or components of software work together. It ensures individual software modules created by different developers with different understandings and programming logic function together seamlessly. It also helps identify and catch any defects arising from combining modules. The key objectives include creating confidence in the interfaces of the application, ensuring that the join points other programs will interact with are sound, and finding defects to prevent them from escaping into higher levels of the software

### API Testing
**API testing** is a type of **Integration Testing** that involves validating HTTP requests and responses function as expected. There is a large pool of potential resources to validate:
- is the HTTP Status in the response correct?
- is the structure of the JSON in the response correct?
- are the correct headers returned in the response?
- is an incorrectly formatted JSON rejected by the application?
- is the correct data returned in the response json?

These questions and more can be answered via **API Testing**

### Typical Defects
**Integration Testing** can reveal defects with incorrect data input or output, which can lead to significant problems in the system being tested if they are not accounted for. Missing fields in the data can also cause errors and disrupt functionality. Data that is encoded differently than expected can create compatibility issues, while incompatible data types can result in processing errors. Unhandled communication errors can interrupt the flow of information between components. Failed security measures can expose the system to vulnerabilities, and unenforced business requirements can lead to the system not meeting its intended goals

## Postman

### Postman Overview
**Postman** is an API platform well suited for performing **API testing**. It provides tools to create workspaces with environment data, collections of tests, and a UI that helps streamline the creation of tests for a web API. **Workspaces** can be used solo or in collaboration with other team members: these are environments that have shared collections of resources, such as test requests, environment data, and test collections. Postman free-tier allows up to three collaborators in a workspace, but paid versions allow for unlimited users. **Collections** are a resource used for grouping similar test requests created in **Postman**. This allows for tracking work done on the tests, sharing tests in bulk, and generally keeping the resources in the **Workspace** organized. **Environments** can be used to save common values for use across **Collections** and tests

### Test Requests
HTTP test requests in **Postman** have two core components:
- **Method Type**: the type of request needs to be specified (POST, GET, etc.)
- **URL**: the request URL needs to be provided. This supports the use of variables for reused data

Test requests also have multiple optional components that augment the requests:
- **Params**: query parameters can be set in this section. They are appended to the route
- **Authorization**: authorization header presets can be selected and configured in this section
- **Headers**: header key/value pairs can be set in this section
- **Body**: the request body for the test is set here, multiple formats are supported
- **Scripts**: pre/post request actions can be configured in this section
- **Settings**: general settings regarding the test request can be configured here

### Pre-Test Scripts
**Postman** can execute JavaScript **setup scripts** to initialize any test environment requirements. To help testing teams get started, **Postman** offers a collection of pre-made scripts. Additionally, it provides the ability to create custom packages, allowing for reusable scripts across different test routes. It's important to note that **Postman** offers many setup solutions within its request builder UI; therefore, **pre-test scripts** should be used to handle setup tasks that go beyond the resources or capabilities of the request builder itself

### Post-Test Scripts
**Postman** also provides a **post-test scripting** section to validate responses. Similar to the **pre-test scripting** feature, it includes pre-made script solutions for common forms of validation, such as verifying the status code, response body, and headers. This is where the automation of **Postman** tests takes place, allowing for efficient and consistent validation of test results

### Environments
**Postman** enables users to work on multiple **Workspaces** and projects through a single interface. However, not all projects require the same collection of data. To better manage this, **Postman** allows for the creation of **Environments** where reusable data can be stored in variables. Testers can simply choose the **Environment** associated with the project they are currently working on, and they will have access to all the stored variables for that project. This feature helps streamline the testing process and ensures that the correct data is used for each project

### Data Sets
**Postman** test collections can be executed using the software's test runner feature. By default, each route will be run once. However, for routes that require different sets of data to be used in multiple runs, Postman supports uploading data sets in JSON or CSV format. For each **Data Set**, all routes in the collection will be run. For example, if the **Data Set** contains three collections of data, each test route will be executed three times, once for each collection

**Data Sets** can be used in conjunction with **Environments**. First, the **Environment** data is loaded, followed by the **Data Set** information. This allows **Data Sets** to override the values of information stored in the **Environment**, providing flexibility and ensuring that the correct data is used for each test run

Keep in mind **Postman** free-tier provides limited capabilities for doing large scale automation: if you want to run your test collections without limit you either need a paid subscription or you need to use **newman**, a CLI based test software that can run Postman tests via Node.js

## REST Assured

### REST Assured Overview
**REST Assured** is a Java framework designed to simplify the testing of REST services directly within a Java application. It follows a given/when/then pattern to set up, execute, and validate API tests, making the process intuitive and efficient. One of the key features of **REST Assured** is its ability to handle the serialization of Java data, ensuring that the data can be easily processed and validated. The framework excels in validating various aspects of the API response: it can verify status codes, ensuring that the correct HTTP status is returned, check the structure and content of the response body, and it also allows for the verification of headers

**REST Assured** integrates seamlessly with popular Java testing frameworks like JUnit and TestNG, providing a robust environment for automated API testing. It supports both JSON and XML formats, offering flexibility in handling different types of data. The framework also includes powerful matching techniques, leveraging [Hamcrest](https://hamcrest.org/JavaHamcrest/tutorial) matchers to perform complex assertions on the response data

### Test Requests
The structure of a **REST Assured** test request involves the initial request setup (if needed), executing the request, and then validating the response
```java
@Test
public void testAuthentication() {
    // Set the base URI
    RestAssured.baseURI = "https://example.com/api";

    // Create the JSON body, or Java object to be serialized as a JSON
    String requestBody = "{ \"username\": \"testuser\", \"password\": \"testpassword\" }";

    // Execute the request with an authentication cookie and JSON body
Response response = given() // Start building the request
        .contentType(ContentType.JSON) // Set the content type of the request to JSON
        .cookie("authCookie", "your_auth_cookie_value") // Add an authentication cookie to the request
        .body(requestBody) // Attach the JSON body containing the username and password
        .when() // Specify that the following action is the request execution
        .post("/login") // Send a POST request to the "/login" endpoint
        .then() // Start the response validation
        .statusCode(200) // Validate that the status code of the response is 200
        .body("message", equalTo("credentials accepted")) // Validate that the response body contains the message "credentials accepted"
        .extract().response(); // Extract the response for further use
}
```
In the example test above, if the response body does not return a 200 status code or response body of "credentials accepted" then an exception will be thrown, failing the test. There are three primary objects being used:
- **RequestSpecification**: this object handles request setup and making the actual test request
- **Response**: this object contains the details of the response
- **ResponseValidation**: this object provides access to the validation features of REST Assured

The request could be broken down in the following way
```java
// Create the RequestSpecification object
RequestSpecification request = given()
        .contentType(ContentType.JSON)
        .cookie("authCookie", "your_auth_cookie_value")
        .body("{ \"username\": \"testuser\", \"password\": \"testpassword\" }");

// Execute the request and get the Response object
Response response = request.when()
        .post("/login");

// Create the ValidatableResponse object and perform validations
response.then()
        .statusCode(200)
        .body("message", equalTo("credentials accepted"));
```

### Setup & Tear Down Methods
Due to its ability to integrate with other test frameworks, **REST Assured** can leverage the setup and tear down solutions provided by the primary testing framework. In a shared setup method, you can handle tasks such as setting authorization information, headers, base URI, and path resources. For tear down methods, you can manage tasks like resetting databases or releasing resources 

### Serializing Data
If **JacksonDatabind** is included in a project then Java objects can be serialized into JSON form, eliminating the need to manually create JSON Strings. If the Class being serialized is designed as a Java Bean no extra steps are needed, but if not, Jackson's annotations are required 