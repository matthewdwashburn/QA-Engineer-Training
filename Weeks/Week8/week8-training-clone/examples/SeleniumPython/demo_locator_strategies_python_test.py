import pytest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

LOGIN_URL = "https://the-internet.herokuapp.com/login"


@pytest.fixture(scope="function")
def driver():
    """Create a Chrome WebDriver and quit after the test."""
    service = Service(ChromeDriverManager().install())
    options = webdriver.ChromeOptions()
    options.add_argument("--disable-gpu")
    options.add_argument("--window-size=1920,1080")

    driver = webdriver.Chrome(service=service, options=options)
    driver.implicitly_wait(5)
    yield driver
    driver.quit()


@pytest.mark.parametrize(
    "description, selector, expected_tag, expected_id",
    [
        ("By ID", "#username", "input", "username"),
        ("By class", ".radius", "button", None),
        ("By tag", "button", "button", None),
        ("By attribute", "input[type='password']", "input", None),
        ("Attribute contains", "input[id*='user']", "input", "username"),
        ("Attribute starts with", "input[id^='user']", "input", "username"),
        ("Attribute ends with", "input[id$='name']", "input", "username"),
    ],
)
def test_css_selector_locates_login_elements(driver, description, selector, expected_tag, expected_id):
    """Verify CSS selector examples can locate elements on the login page."""
    driver.get(LOGIN_URL)

    element = driver.find_element(By.CSS_SELECTOR, selector)

    assert element is not None
    assert element.tag_name == expected_tag
    if expected_id:
        assert element.get_attribute("id") == expected_id
