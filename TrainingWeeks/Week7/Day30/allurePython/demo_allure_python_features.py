"""
Demo: Allure Python Features - Steps, Attachments, and Rich Reporting

INSTRUCTOR TALKING POINTS:
1. allure.step() context manager for logical test phases
2. allure.attach() for adding evidence
3. Step functions with @allure.step decorator
4. Dynamic content and screenshots

RUN THIS WITH:
    pytest demo_allure_python_features.py --alluredir=allure-results -v
    allure serve allure-results
"""

import pytest
import allure
import json


# ==========================================================
# SECTION 1: Steps with Context Manager
# ==========================================================

@allure.epic("Week 6: Unit Testing")
@allure.feature("Allure Steps")
@allure.story("Context Manager Steps")
@allure.title("Login Flow with Steps")
@allure.severity(allure.severity_level.CRITICAL)
def test_login_with_steps():
    """Demonstrates step blocks for login workflow."""
    
    with allure.step("Navigate to login page"):
        # Simulated navigation
        url = "https://app.example.com/login"
        allure.attach(url, name="URL", attachment_type=allure.attachment_type.TEXT)
    
    with allure.step("Enter username"):
        username = "testuser@example.com"
        assert "@" in username
    
    with allure.step("Enter password"):
        password = "********"
        assert len(password) > 0
    
    with allure.step("Click login button"):
        # Simulated click
        clicked = True
        assert clicked
    
    with allure.step("Verify dashboard is displayed"):
        dashboard_title = "Dashboard"
        assert "Dashboard" in dashboard_title


@allure.story("Context Manager Steps")
@allure.title("Shopping Cart Flow")
def test_shopping_cart_flow():
    """Shopping cart workflow with detailed steps."""
    
    cart_items = []
    
    with allure.step("Search for product"):
        search_term = "Laptop"
        allure.attach(search_term, name="Search Term", 
                     attachment_type=allure.attachment_type.TEXT)
    
    with allure.step("Add product to cart"):
        product = {"name": "Laptop", "price": 999.99}
        cart_items.append(product)
        allure.attach(json.dumps(product, indent=2), name="Product Added",
                     attachment_type=allure.attachment_type.JSON)
    
    with allure.step("Verify cart contents"):
        assert len(cart_items) == 1
        assert cart_items[0]["name"] == "Laptop"


# ==========================================================
# SECTION 2: Step Functions with Decorator
# ==========================================================

@allure.step("Navigate to page: {url}")
def navigate_to(url: str):
    """Step function for navigation."""
    allure.attach(url, name="Navigated URL", attachment_type=allure.attachment_type.TEXT)
    return True


@allure.step("Enter value in field '{field_name}': {value}")
def enter_field(field_name: str, value: str):
    """Step function for form input."""
    return True


@allure.step("Click button: {button_name}")
def click_button(button_name: str):
    """Step function for button clicks."""
    return True


@allure.step("Verify text is present: {expected_text}")
def verify_text(expected_text: str):
    """Step function for text verification."""
    return True


@allure.story("Step Functions")
@allure.title("Registration Flow with Step Functions")
@allure.severity(allure.severity_level.CRITICAL)
def test_registration_flow():
    """Uses step functions for cleaner test code."""
    
    navigate_to("https://app.example.com/register")
    enter_field("Email", "newuser@example.com")
    enter_field("Password", "SecurePass123!")
    enter_field("Confirm Password", "SecurePass123!")
    click_button("Create Account")
    verify_text("Account created successfully")
    
    assert True


# ==========================================================
# SECTION 3: Attachments
# ==========================================================

@allure.story("Attachments")
@allure.title("Test with Various Attachments")
def test_with_attachments():
    """Demonstrates different attachment types."""
    
    # Text attachment
    allure.attach(
        "This is plain text content",
        name="Text Log",
        attachment_type=allure.attachment_type.TEXT
    )
    
    # JSON attachment
    json_data = {
        "request_id": "req-12345",
        "status": "success",
        "data": {"user": "testuser", "role": "admin"}
    }
    allure.attach(
        json.dumps(json_data, indent=2),
        name="API Response",
        attachment_type=allure.attachment_type.JSON
    )
    
    # HTML attachment
    html_content = """
    <html>
    <head><title>Test Report</title></head>
    <body>
        <h1>Test Results</h1>
        <p>All tests passed!</p>
    </body>
    </html>
    """
    allure.attach(
        html_content,
        name="HTML Report",
        attachment_type=allure.attachment_type.HTML
    )
    
    # CSV attachment
    csv_content = "name,value,status\ntest1,100,pass\ntest2,200,pass\ntest3,300,fail"
    allure.attach(
        csv_content,
        name="Test Data",
        attachment_type=allure.attachment_type.CSV
    )
    
    assert True


@allure.story("Attachments")
@allure.title("Test with Request/Response Logging")
def test_api_with_logging():
    """Simulates API test with request/response attachments."""
    
    # Simulate request
    request_data = {
        "method": "POST",
        "url": "/api/users",
        "headers": {"Content-Type": "application/json"},
        "body": {"name": "John", "email": "john@test.com"}
    }
    
    with allure.step("Send API request"):
        allure.attach(
            json.dumps(request_data, indent=2),
            name="Request",
            attachment_type=allure.attachment_type.JSON
        )
    
    # Simulate response
    response_data = {
        "status": 201,
        "body": {"id": 123, "name": "John", "email": "john@test.com"}
    }
    
    with allure.step("Receive API response"):
        allure.attach(
            json.dumps(response_data, indent=2),
            name="Response",
            attachment_type=allure.attachment_type.JSON
        )
    
    with allure.step("Verify response"):
        assert response_data["status"] == 201
        assert response_data["body"]["id"] == 123


# ==========================================================
# SECTION 4: Nested Steps
# ==========================================================

@allure.step("Perform complete checkout")
def perform_checkout():
    """Nested step function."""
    fill_shipping_info()
    fill_payment_info()
    confirm_order()


@allure.step("Fill shipping information")
def fill_shipping_info():
    enter_field("Street", "123 Main St")
    enter_field("City", "Test City")
    enter_field("Zip", "12345")


@allure.step("Fill payment information")
def fill_payment_info():
    enter_field("Card Number", "****-****-****-1234")
    enter_field("Expiry", "12/25")
    enter_field("CVV", "***")


@allure.step("Confirm order")
def confirm_order():
    click_button("Place Order")
    verify_text("Order confirmed")


@allure.story("Nested Steps")
@allure.title("Complete Order with Nested Steps")
def test_complete_order():
    """Demonstrates nested steps for complex workflows."""
    navigate_to("https://shop.example.com/checkout")
    perform_checkout()
    assert True


# ==========================================================
# SECTION 5: Using Fixtures with Allure
# ==========================================================

@pytest.fixture
def user_data():
    """Fixture that provides test data and logs to Allure."""
    data = {
        "name": "Test User",
        "email": "test@example.com",
        "role": "admin"
    }
    allure.attach(
        json.dumps(data, indent=2),
        name="Test User Data",
        attachment_type=allure.attachment_type.JSON
    )
    return data


@allure.story("Fixtures with Allure")
@allure.title("Test using fixture with Allure attachment")
def test_with_fixture(user_data):
    """Test that uses fixture data logged to Allure."""
    
    with allure.step("Verify user data"):
        assert user_data["name"] == "Test User"
        assert user_data["role"] == "admin"


# ==========================================================
# SECTION 6: Dynamic Parameters
# ==========================================================

@allure.story("Dynamic Parameters")
@allure.title("Test with dynamic parameters")
def test_with_parameters():
    """Demonstrates adding parameters to test report."""
    
    # Add test parameters
    allure.dynamic.parameter("Environment", "staging")
    allure.dynamic.parameter("Browser", "Chrome")
    allure.dynamic.parameter("OS", "Windows 10")
    allure.dynamic.parameter("Test Run ID", "run-2024-001")
    
    # Add labels
    allure.dynamic.label("layer", "unit")
    allure.dynamic.label("component", "user-service")
    
    assert True


# ==========================================================
# SECTION 7: Complete Real-World Example
# ==========================================================

@allure.epic("E-Commerce Platform")
@allure.feature("User Authentication")
@allure.story("Login")
@allure.severity(allure.severity_level.CRITICAL)
@allure.title("Complete Login Test with Full Reporting")
@allure.description("""
This test verifies the complete login workflow including:
- Navigation to login page
- Form submission
- Dashboard verification
- Session validation

Preconditions:
- User account exists
- No active sessions
""")
@allure.link("https://docs.example.com/login", name="Documentation")
@allure.issue("AUTH-123", "Related Issue")
def test_complete_login_example():
    """Complete example with all Allure features."""
    
    allure.dynamic.parameter("Username", "testuser@example.com")
    allure.dynamic.parameter("Environment", "QA")
    
    with allure.step("Precondition: Clear any existing sessions"):
        session_cleared = True
        assert session_cleared
    
    with allure.step("Navigate to login page"):
        navigate_to("https://app.example.com/login")
    
    with allure.step("Enter valid credentials"):
        enter_field("Email", "testuser@example.com")
        enter_field("Password", "********")
        
        # Log sanitized credentials
        allure.attach(
            json.dumps({"email": "testuser@example.com", "password": "[REDACTED]"}),
            name="Login Credentials (Sanitized)",
            attachment_type=allure.attachment_type.JSON
        )
    
    with allure.step("Submit login form"):
        click_button("Sign In")
    
    with allure.step("Verify successful login"):
        # Simulate response
        login_response = {"success": True, "redirect": "/dashboard"}
        allure.attach(
            json.dumps(login_response),
            name="Login Response",
            attachment_type=allure.attachment_type.JSON
        )
        assert login_response["success"]
    
    with allure.step("Verify dashboard displayed"):
        verify_text("Welcome, Test User")
    
    with allure.step("Verify session is active"):
        session_data = {"user_id": 123, "token": "jwt-token-here", "expires": "2024-12-31"}
        allure.attach(
            json.dumps(session_data, indent=2),
            name="Session Data",
            attachment_type=allure.attachment_type.JSON
        )
        assert session_data["user_id"] == 123


# ==========================================================
# LIVE CODING CHALLENGE
# ==========================================================

# INSTRUCTOR: Have students create a test that:
#
# 1. Has @allure.epic, @allure.feature, @allure.story
# 2. Uses at least 5 steps with allure.step()
# 3. Includes 3 different attachment types
# 4. Uses step functions with @allure.step decorator
# 5. Has appropriate severity and description
#
# Example scenario: User Registration or Order Placement
#
# Time: 20 minutes

