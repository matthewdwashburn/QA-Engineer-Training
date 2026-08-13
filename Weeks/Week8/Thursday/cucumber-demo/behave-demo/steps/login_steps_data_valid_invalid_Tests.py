from behave import given, when, then
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


BASE_URL = "https://the-internet.herokuapp.com"


# ---------- GIVEN STEPS ----------

@given("the application is running")
def step_application_running(context):
    options = Options()
    options.add_argument("--start-maximized")

    context.driver = webdriver.Chrome(options=options)
    context.wait = WebDriverWait(context.driver, 10)

    context.driver.get(BASE_URL)

    # Assert application is available
    heading = context.wait.until(
        EC.visibility_of_element_located((By.TAG_NAME, "h1"))
    )
    assert heading.text == "Welcome to the-internet"


@given("the test database is seeded with users")
def step_database_seeded(context):
    # Demo placeholder
    assert True


@given("the user is on the login page")
def step_user_on_login_page(context):
    context.driver.get(f"{BASE_URL}/login")

    # Assert login page loaded
    context.wait.until(
        EC.visibility_of_element_located((By.ID, "login"))
    )


# ---------- WHEN STEPS ----------

@when('the user enters username "{username}"')
def step_enter_username(context, username):
    username_input = context.wait.until(
        EC.visibility_of_element_located((By.ID, "username"))
    )
    username_input.clear()

    # Handle empty username from Examples table
    if username:
        username_input.send_keys(username)


@when('the user enters password "{password}"')
def step_enter_password(context, password):
    password_input = context.wait.until(
        EC.visibility_of_element_located((By.ID, "password"))
    )
    password_input.clear()

    # Handle empty password from Examples table
    if password:
        password_input.send_keys(password)


@when("the user clicks the login button")
def step_click_login(context):
    login_button = context.wait.until(
        EC.element_to_be_clickable((By.CSS_SELECTOR, "button[type='submit']"))
    )
    login_button.click()

@when('the user enters username ""')
def step_enter_empty_username(context):
    """
    Explicit step for empty username.
    Clears the username field and enters nothing.
    """
    username_input = context.wait.until(
        EC.visibility_of_element_located((By.ID, "username"))
    )
    username_input.clear()


@when('the user enters password ""')
def step_enter_empty_password(context):
    """
    Explicit step for empty password.
    Clears the password field and enters nothing.
    """
    password_input = context.wait.until(
        EC.visibility_of_element_located((By.ID, "password"))
    )
    password_input.clear()


# ---------- THEN STEPS ----------

@then('the "{expected_result}" should be displayed')
def step_verify_result(context, expected_result):
    flash_message = context.wait.until(
        EC.visibility_of_element_located((By.ID, "flash"))
    )
    message_text = flash_message.text

    if expected_result == "success message":
        assert "You logged into a secure area!" in message_text

    elif expected_result == "error message":
        # Covers invalid user, invalid password, empty fields
        assert "is invalid!" in message_text or "must be filled" in message_text

    else:
        raise AssertionError(f"Unknown expected result: {expected_result}")


@then('the user should be on the "{expected_page}" page')
def step_verify_page(context, expected_page):
    if expected_page == "secure":
        context.wait.until(EC.url_contains("/secure"))
        assert "/secure" in context.driver.current_url

    elif expected_page == "login":
        context.wait.until(EC.url_contains("/login"))
        assert "/login" in context.driver.current_url

    else:
        raise AssertionError(f"Unknown expected page: {expected_page}")

    # Close browser after each scenario
    context.driver.quit()
