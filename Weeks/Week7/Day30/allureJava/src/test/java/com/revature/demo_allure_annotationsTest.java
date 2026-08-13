package com.revature;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demo: Allure Annotations - Steps, Attachments, and Rich Reporting
 *
 * 1. @Step breaks tests into logical, reportable phases
 * 2. Attachments add evidence (screenshots, logs, data)
 * 3. Dynamic titles with parameters
 * 4. Steps can be nested for complex workflows
 *
 * This demo shows how to create professional, detailed test reports.
 */
@Epic("Week 6: Unit Testing")
@Feature("Allure Advanced Features")
@DisplayName("Allure Annotations Deep Dive")
public class demo_allure_annotationsTest {
    // ==========================================================
    // SECTION 1: @Step Annotation - Breaking Tests into Steps
    // ==========================================================

    @Test
    @Story("Steps")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Demonstrates how steps appear in Allure report")
    @DisplayName("Login Flow with Steps")
    void testLoginWithSteps() {
        navigateToLoginPage();
        enterCredentials("user@example.com", "password123");
        clickLoginButton();
        verifyDashboardDisplayed();
    }

    @Step("Navigate to login page")
    void navigateToLoginPage() {
        // In real test: driver.get("/login");
        Allure.addAttachment("URL", "text/plain", "https://app.example.com/login");
    }

    @Step("Enter credentials for user: {email}")
    void enterCredentials(String email, String password) {
        // In real test: fill form fields
        // Note: password is not included in step name for security
        assertNotNull(email);
        assertNotNull(password);
    }

    @Step("Click login button")
    void clickLoginButton() {
        // In real test: click submit
        assertTrue(true, "Login button clicked");
    }

    @Step("Verify dashboard is displayed")
    void verifyDashboardDisplayed() {
        // In real test: check page title/elements
        assertTrue(true, "Dashboard verification passed");
    }

    // ==========================================================
    // SECTION 2: Attachments - Adding Evidence
    // ==========================================================

    @Test
    @Story("Attachments")
    @Severity(SeverityLevel.NORMAL)
    @Description("Shows various attachment types in Allure")
    @DisplayName("Test with Multiple Attachments")
    void testWithAttachments() {
        // Text attachment
        Allure.addAttachment("Test Input", "text/plain", "Sample input data");

        // JSON attachment
        String jsonData = """
            {
                "username": "testuser",
                "action": "login",
                "timestamp": "2024-01-15T10:30:00Z"
            }
            """;
        Allure.addAttachment("Request Body", "application/json", jsonData);

        // HTML attachment
        String htmlContent = "<html><body><h1>Test Report</h1></body></html>";
        Allure.addAttachment("HTML Content", "text/html", htmlContent);

        assertTrue(true);
    }

    @Test
    @Story("Attachments")
    @Description("Demonstrates annotation-based attachments")
    @DisplayName("Test with Annotation Attachments")
    void testWithAnnotationAttachments() {
        // Method-based attachment
        String source = attachPageSource();
        assertNotNull(source);

        // Direct attachment
        attachTestData();
    }

    @Attachment(value = "Page Source", type = "text/html")
    String attachPageSource() {
        return "<html><body>Simulated page source</body></html>";
    }

    @Attachment(value = "Test Data", type = "text/csv")
    String attachTestData() {
        return "name,value\ntest1,100\ntest2,200";
    }

    // ==========================================================
    // SECTION 3: Dynamic Step Names with Parameters
    // ==========================================================

    @Test
    @Story("Dynamic Steps")
    @Description("Steps with dynamic parameter values")
    @DisplayName("Shopping Cart Operations")
    void testShoppingCartOperations() {
        addProductToCart("Laptop", 999.99);
        addProductToCart("Mouse", 29.99);
        updateQuantity("Laptop", 2);
        verifyCartTotal(2029.97);
    }

    @Step("Add product to cart: {productName} - ${price}")
    void addProductToCart(String productName, double price) {
        // In real test: add product
        assertTrue(price > 0);
    }

    @Step("Update quantity for {productName} to {quantity}")
    void updateQuantity(String productName, int quantity) {
        assertTrue(quantity > 0);
    }

    @Step("Verify cart total is ${expectedTotal}")
    void verifyCartTotal(double expectedTotal) {
        // In real test: compare actual total
        assertTrue(expectedTotal > 0);
    }

    // ==========================================================
    // SECTION 4: Nested Steps
    // ==========================================================

    @Test
    @Story("Nested Steps")
    @Description("Complex workflow with nested steps")
    @DisplayName("Complete Order Flow")
    void testCompleteOrderFlow() {
        performCheckout();
    }

    @Step("Perform complete checkout process")
    void performCheckout() {
        fillShippingAddress();
        selectPaymentMethod();
        confirmOrder();
    }

    @Step("Fill shipping address")
    void fillShippingAddress() {
        enterField("Street", "123 Main St");
        enterField("City", "Test City");
        enterField("Zip", "12345");
    }

    @Step("Select payment method")
    void selectPaymentMethod() {
        selectOption("Credit Card");
        enterField("Card Number", "****-****-****-1234");
    }

    @Step("Confirm order")
    void confirmOrder() {
        clickButton("Place Order");
        verifyConfirmation();
    }

    @Step("Enter {fieldName}: {value}")
    void enterField(String fieldName, String value) {
        assertNotNull(value);
    }

    @Step("Select option: {option}")
    void selectOption(String option) {
        assertNotNull(option);
    }

    @Step("Click button: {buttonName}")
    void clickButton(String buttonName) {
        assertNotNull(buttonName);
    }

    @Step("Verify order confirmation")
    void verifyConfirmation() {
        assertTrue(true, "Order confirmed");
    }

    // ==========================================================
    // SECTION 5: Allure API for Programmatic Control
    // ==========================================================

    @Test
    @Story("Allure API")
    @Description("Using Allure API for dynamic reporting")
    @DisplayName("Programmatic Allure Features")
    void testAllureApi() {
        // Dynamic description
        Allure.description("This test was run at: " + java.time.LocalDateTime.now());

        // Add parameter
        Allure.parameter("Environment", "Test");
        Allure.parameter("Browser", "Chrome");

        // Add label
        Allure.label("layer", "unit");
        Allure.label("component", "allure-demo");

        // Add link
        Allure.link("Documentation", "https://docs.qameta.io/allure/");

        assertTrue(true);
    }

    // ==========================================================
    // SECTION 6: Handling Test with Preconditions
    // ==========================================================

    @Test
    @Story("Preconditions")
    @Description("Test with explicit preconditions documented")
    @DisplayName("Test with Preconditions")
    void testWithPreconditions() {
        // Document preconditions
        Allure.step("Precondition: User must be logged in", () -> {
            // Setup code
            assertTrue(true, "User logged in");
        });

        Allure.step("Precondition: Shopping cart must be empty", () -> {
            // Setup code
            assertTrue(true, "Cart cleared");
        });

        // Actual test
        Allure.step("Add item to cart", () -> {
            assertTrue(true);
        });

        Allure.step("Verify cart count is 1", () -> {
            assertEquals(1, 1);
        });
    }
}
