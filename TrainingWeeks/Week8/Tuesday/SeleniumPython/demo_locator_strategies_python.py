"""
Demo: Locator Strategies in Python Selenium
============================================

This demo covers all locator strategies available in Python Selenium,
showing when and how to use each type effectively.

Learning Objectives:
- Master all locator types in Python Selenium
- Understand the By class and its constants
- Choose the right locator for each situation
- Compare Python locators with Java locators
"""

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager

# Test website that has various elements to locate
TEST_URL = "https://the-internet.herokuapp.com/"


def create_driver():
    """Create and return a Chrome WebDriver."""
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)
    driver.implicitly_wait(5)
    return driver


# =============================================================================
# PART 1: BY.ID - Most Reliable Locator
# =============================================================================
def demo_by_id():
    """
    Locate elements by ID attribute.
    
    Python:  driver.find_element(By.ID, "element_id")
    Java:    driver.findElement(By.id("element_id"))
    
    Best for: Unique elements with ID attributes
    """
    print("=" * 60)
    print("DEMO: By.ID Locator")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        # Navigate to form page
        driver.get("https://the-internet.herokuapp.com/login")
        
        # Find elements by ID
        username_field = driver.find_element(By.ID, "username")
        password_field = driver.find_element(By.ID, "password")
        login_button = driver.find_element(By.CSS_SELECTOR, "button[type='submit']")
        
        # Interact with elements
        username_field.send_keys("tomsmith")
        password_field.send_keys("SuperSecretPassword!")
        
        print(f"✓ Found username field by ID: 'username'")
        print(f"✓ Found password field by ID: 'password'")
        print(f"  Username field tag: {username_field.tag_name}")
        print(f"  Password field type: {password_field.get_attribute('type')}")
        
        # Submit login
        login_button.click()
        
        # Verify success
        flash_message = driver.find_element(By.ID, "flash")
        print(f"✓ Login successful! Flash message found.")
        
    finally:
        driver.quit()
    
    print("✓ By.ID demo completed\n")


# =============================================================================
# PART 2: BY.NAME - Form Elements
# =============================================================================
def demo_by_name():
    """
    Locate elements by name attribute.
    
    Python:  driver.find_element(By.NAME, "element_name")
    Java:    driver.findElement(By.name("element_name"))
    
    Best for: Form inputs that use name attribute
    """
    print("=" * 60)
    print("DEMO: By.NAME Locator")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/login")
        
        # Find by name attribute
        username_by_name = driver.find_element(By.NAME, "username")
        password_by_name = driver.find_element(By.NAME, "password")
        
        print(f"✓ Found element by NAME 'username'")
        print(f"  Element ID: {username_by_name.get_attribute('id')}")
        print(f"  Element type: {username_by_name.get_attribute('type')}")
        
        print(f"✓ Found element by NAME 'password'")
        print(f"  Element ID: {password_by_name.get_attribute('id')}")
        
    finally:
        driver.quit()
    
    print("✓ By.NAME demo completed\n")


# =============================================================================
# PART 3: BY.CLASS_NAME - Styling Classes
# =============================================================================
def demo_by_class_name():
    """
    Locate elements by class name.
    
    Python:  driver.find_element(By.CLASS_NAME, "class_name")
    Java:    driver.findElement(By.className("class_name"))
    
    Note: Only use ONE class name, not compound classes
    """
    print("=" * 60)
    print("DEMO: By.CLASS_NAME Locator")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/")
        
        # Find by single class name
        # Note: If element has "class='large-12 columns'", 
        # use "large-12" OR "columns", NOT both
        heading = driver.find_element(By.CLASS_NAME, "heading")
        
        print(f"✓ Found element by CLASS_NAME 'heading'")
        print(f"  Tag: {heading.tag_name}")
        print(f"  Text: {heading.text}")
        
        # Find multiple elements with same class
        driver.get("https://the-internet.herokuapp.com/challenging_dom")
        
        buttons = driver.find_elements(By.CLASS_NAME, "button")
        print(f"\n✓ Found {len(buttons)} elements with CLASS_NAME 'button'")
        
        for i, btn in enumerate(buttons, 1):
            print(f"  Button {i}: {btn.text}")
        
    finally:
        driver.quit()
    
    print("✓ By.CLASS_NAME demo completed\n")


# =============================================================================
# PART 4: BY.TAG_NAME - HTML Tags
# =============================================================================
def demo_by_tag_name():
    """
    Locate elements by HTML tag name.
    
    Python:  driver.find_element(By.TAG_NAME, "tag")
    Java:    driver.findElement(By.tagName("tag"))
    
    Best for: Collecting all elements of a type
    """
    print("=" * 60)
    print("DEMO: By.TAG_NAME Locator")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/")
        
        # Find all links on the page
        all_links = driver.find_elements(By.TAG_NAME, "a")
        print(f"✓ Found {len(all_links)} <a> elements on page")
        
        # Show first 5 links
        print("  First 5 links:")
        for link in all_links[:5]:
            href = link.get_attribute("href")
            text = link.text or "[No text]"
            print(f"    - {text}: {href}")
        
        # Find all headers
        headers = driver.find_elements(By.TAG_NAME, "h1")
        headers.extend(driver.find_elements(By.TAG_NAME, "h2"))
        
        print(f"\n✓ Found {len(headers)} header elements (h1, h2)")
        for h in headers:
            print(f"    <{h.tag_name}>: {h.text}")
        
    finally:
        driver.quit()
    
    print("✓ By.TAG_NAME demo completed\n")


# =============================================================================
# PART 5: BY.LINK_TEXT - Full Link Text
# =============================================================================
def demo_by_link_text():
    """
    Locate links by their exact text content.
    
    Python:  driver.find_element(By.LINK_TEXT, "exact text")
    Java:    driver.findElement(By.linkText("exact text"))
    
    Best for: Clicking links when you know the exact text
    """
    print("=" * 60)
    print("DEMO: By.LINK_TEXT Locator")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/")
        
        # Find link by exact text
        form_auth_link = driver.find_element(By.LINK_TEXT, "Form Authentication")
        
        print(f"✓ Found link by LINK_TEXT 'Form Authentication'")
        print(f"  href: {form_auth_link.get_attribute('href')}")
        
        # Click the link
        form_auth_link.click()
        
        # Verify navigation
        print(f"  Navigated to: {driver.current_url}")
        assert "login" in driver.current_url
        print(f"✓ Link clicked successfully!")
        
    finally:
        driver.quit()
    
    print("✓ By.LINK_TEXT demo completed\n")


# =============================================================================
# PART 6: BY.PARTIAL_LINK_TEXT - Partial Match
# =============================================================================
def demo_by_partial_link_text():
    """
    Locate links by partial text match.
    
    Python:  driver.find_element(By.PARTIAL_LINK_TEXT, "partial")
    Java:    driver.findElement(By.partialLinkText("partial"))
    
    Best for: Links where you know part of the text
    """
    print("=" * 60)
    print("DEMO: By.PARTIAL_LINK_TEXT Locator")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/")
        
        # Find link by partial text
        # "Form Authentication" can be found with "Form" or "Authentication"
        auth_link = driver.find_element(By.PARTIAL_LINK_TEXT, "Authentication")
        
        print(f"✓ Found link by PARTIAL_LINK_TEXT 'Authentication'")
        print(f"  Full text: {auth_link.text}")
        print(f"  href: {auth_link.get_attribute('href')}")
        
        # Find another link with partial text
        dropdown_link = driver.find_element(By.PARTIAL_LINK_TEXT, "Dropdown")
        print(f"\n✓ Found link by PARTIAL_LINK_TEXT 'Dropdown'")
        print(f"  Full text: {dropdown_link.text}")
        
    finally:
        driver.quit()
    
    print("✓ By.PARTIAL_LINK_TEXT demo completed\n")


# =============================================================================
# PART 7: BY.CSS_SELECTOR - Powerful & Flexible
# =============================================================================
def demo_by_css_selector():
    """
    Locate elements using CSS selectors.
    
    Python:  driver.find_element(By.CSS_SELECTOR, "selector")
    Java:    driver.findElement(By.cssSelector("selector"))
    
    Best for: Complex selections, when ID/name not available
    """
    print("=" * 60)
    print("DEMO: By.CSS_SELECTOR Locator")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/login")
        
        # Various CSS selector examples
        examples = [
            ("By ID", "#username"),
            ("By class", ".radius"),
            ("By tag", "button"),
            ("By attribute", "input[type='password']"),
            ("Child selector", "form > div > input"),
            ("Attribute contains", "input[id*='user']"),
            ("Attribute starts with", "input[id^='user']"),
            ("Attribute ends with", "input[id$='name']"),
        ]
        
        print("CSS Selector Examples:")
        for description, selector in examples:
            try:
                element = driver.find_element(By.CSS_SELECTOR, selector)
                print(f"  ✓ {description}: '{selector}'")
                print(f"      Tag: {element.tag_name}, ID: {element.get_attribute('id') or 'N/A'}")
            except Exception as e:
                print(f"  ✗ {description}: '{selector}' - Not found")
        
    finally:
        driver.quit()
    
    print("\n✓ By.CSS_SELECTOR demo completed\n")


# =============================================================================
# PART 8: BY.XPATH - Most Powerful Locator
# =============================================================================
def demo_by_xpath():
    """
    Locate elements using XPath expressions.
    
    Python:  driver.find_element(By.XPATH, "//path")
    Java:    driver.findElement(By.xpath("//path"))
    
    Best for: Complex DOM traversal, dynamic elements
    (Detailed XPath demo in separate file)
    """
    print("=" * 60)
    print("DEMO: By.XPATH Locator (Basic Examples)")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/login")
        
        # XPath examples
        examples = [
            ("By ID", "//input[@id='username']"),
            ("By name", "//input[@name='password']"),
            ("By text", "//h2[text()='Login Page']"),
            ("Contains text", "//a[contains(text(),'Elemental')]"),
            ("By attribute", "//button[@type='submit']"),
            ("By position", "(//input)[1]"),  # First input
        ]
        
        print("XPath Examples:")
        for description, xpath in examples:
            try:
                element = driver.find_element(By.XPATH, xpath)
                tag = element.tag_name
                text = element.text[:30] if element.text else "N/A"
                print(f"  ✓ {description}")
                print(f"      XPath: {xpath}")
                print(f"      Found: <{tag}> - '{text}'")
            except Exception:
                print(f"  ✗ {description}: Not found")
        
    finally:
        driver.quit()
    
    print("\n✓ By.XPATH demo completed\n")


# =============================================================================
# PART 9: LOCATOR COMPARISON & BEST PRACTICES
# =============================================================================
def demo_locator_comparison():
    """
    Compare all locators finding the same element.
    Shows which locators work for a given element.
    """
    print("=" * 60)
    print("DEMO: Locator Comparison - Same Element, Different Locators")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/login")
        
        # Target: Username input field
        print("\nFinding the USERNAME input field using different locators:\n")
        
        locators = [
            ("By.ID", By.ID, "username"),
            ("By.NAME", By.NAME, "username"),
            ("By.CSS_SELECTOR (id)", By.CSS_SELECTOR, "#username"),
            ("By.CSS_SELECTOR (attr)", By.CSS_SELECTOR, "input[id='username']"),
            ("By.XPATH (id)", By.XPATH, "//input[@id='username']"),
            ("By.XPATH (name)", By.XPATH, "//input[@name='username']"),
        ]
        
        print("Locator Priority (recommended order):")
        print("-" * 40)
        
        for i, (name, by_type, value) in enumerate(locators, 1):
            try:
                import time
                start = time.time()
                element = driver.find_element(by_type, value)
                elapsed = (time.time() - start) * 1000
                
                print(f"{i}. {name}")
                print(f"   Value: '{value}'")
                print(f"   Found: ✓ ({elapsed:.2f}ms)")
            except Exception:
                print(f"{i}. {name}")
                print(f"   Value: '{value}'")
                print(f"   Found: ✗")
            print()
        
        print("📌 Best Practice Priority:")
        print("   1. ID (fastest, most reliable)")
        print("   2. Name (good for forms)")
        print("   3. CSS Selector (flexible, fast)")
        print("   4. XPath (powerful but slower)")
        print("   5. Link Text (only for links)")
        print("   6. Class Name (avoid if not unique)")
        print("   7. Tag Name (only for collections)")
        
    finally:
        driver.quit()
    
    print("\n✓ Locator comparison demo completed\n")


# =============================================================================
# MAIN EXECUTION
# =============================================================================
if __name__ == "__main__":
    print("\n🔍 LOCATOR STRATEGIES DEMO 🔍\n")
    print("This demo covers all locator types in Python Selenium")
    print("-" * 60)
    
    demo_by_id()
    demo_by_name()
    demo_by_class_name()
    demo_by_tag_name()
    demo_by_link_text()
    demo_by_partial_link_text()
    demo_by_css_selector()
    demo_by_xpath()
    demo_locator_comparison()
    
    print("\n" + "=" * 60)
    print("LOCATOR STRATEGIES DEMOS COMPLETED! 🎉")
    print("=" * 60)

