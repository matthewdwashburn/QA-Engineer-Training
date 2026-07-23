"""
Demo: Element Interactions in Python Selenium
=============================================

This demo covers all common WebElement interaction methods
for automating form inputs, buttons, checkboxes, and dropdowns.

Learning Objectives:
- Master WebElement interaction methods
- Handle different input types (text, checkbox, radio, select)
- Get element properties and attributes
- Compare Python vs Java interaction syntax
"""

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.support.ui import Select
from selenium.webdriver.common.keys import Keys
from webdriver_manager.chrome import ChromeDriverManager
import time


def create_driver():
    """Create and return a Chrome WebDriver."""
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)
    driver.implicitly_wait(10)
    return driver


# =============================================================================
# PART 1: TEXT INPUT INTERACTIONS
# =============================================================================
def demo_text_input():
    """
    Working with text input fields.
    
    Methods covered:
    - send_keys(): Type text into field
    - clear(): Clear field contents
    - get_attribute(): Get field value
    """
    print("=" * 60)
    print("DEMO: Text Input Interactions")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/login")
        
        # Find the username input
        username = driver.find_element(By.ID, "username")
        
        # Type text into the field
        # Java: element.sendKeys("text");
        print("\n1. send_keys() - Type text:")
        username.send_keys("tomsmith")
        print(f"   Typed 'tomsmith' into username field")
        print(f"   Current value: {username.get_attribute('value')}")
        
        # Clear the field
        # Java: element.clear();
        print("\n2. clear() - Clear field:")
        username.clear()
        print(f"   Field cleared")
        print(f"   Current value: '{username.get_attribute('value')}'")
        
        # Type again with special keys
        print("\n3. send_keys() with special keys:")
        username.send_keys("demo_user")
        username.send_keys(Keys.TAB)  # Tab to next field
        print(f"   Typed 'demo_user' and pressed TAB")
        
        # Get attribute value
        print("\n4. get_attribute() - Get field value:")
        password = driver.find_element(By.ID, "password")
        password.send_keys("secret123")
        value = password.get_attribute("value")
        input_type = password.get_attribute("type")
        print(f"   Password value: '{value}'")
        print(f"   Input type: '{input_type}'")
        
    finally:
        driver.quit()
    
    print("\n✓ Text input demo completed\n")


# =============================================================================
# PART 2: BUTTON AND CLICK INTERACTIONS
# =============================================================================
def demo_button_interactions():
    """
    Working with buttons and clickable elements.
    
    Methods covered:
    - click(): Click an element
    - is_enabled(): Check if clickable
    - is_displayed(): Check visibility
    """
    print("=" * 60)
    print("DEMO: Button Interactions")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/login")
        
        # Find the login button
        login_btn = driver.find_element(By.CSS_SELECTOR, "button[type='submit']")
        
        # Check element properties before clicking
        print("\n1. Element properties:")
        print(f"   is_displayed(): {login_btn.is_displayed()}")
        print(f"   is_enabled(): {login_btn.is_enabled()}")
        print(f"   text: '{login_btn.text}'")
        print(f"   tag_name: '{login_btn.tag_name}'")
        
        # Fill form first
        driver.find_element(By.ID, "username").send_keys("tomsmith")
        driver.find_element(By.ID, "password").send_keys("SuperSecretPassword!")
        
        # Click the button
        print("\n2. click() - Click button:")
        login_btn.click()
        print(f"   Clicked login button")
        
        # Verify navigation
        time.sleep(1)  # Wait for page load
        print(f"   Current URL: {driver.current_url}")
        
        # Find and verify success message
        flash = driver.find_element(By.ID, "flash")
        print(f"   Success message: {flash.text[:50]}...")
        
    finally:
        driver.quit()
    
    print("\n✓ Button interactions demo completed\n")


# =============================================================================
# PART 3: CHECKBOX AND RADIO BUTTON INTERACTIONS
# =============================================================================
def demo_checkbox_radio():
    """
    Working with checkboxes and radio buttons.
    
    Methods covered:
    - click(): Toggle checkbox/select radio
    - is_selected(): Check if selected
    """
    print("=" * 60)
    print("DEMO: Checkbox & Radio Button Interactions")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        # Checkboxes
        driver.get("https://the-internet.herokuapp.com/checkboxes")
        
        print("\n1. Working with Checkboxes:")
        
        checkboxes = driver.find_elements(By.CSS_SELECTOR, "input[type='checkbox']")
        
        for i, checkbox in enumerate(checkboxes, 1):
            initial_state = checkbox.is_selected()
            print(f"\n   Checkbox {i}:")
            print(f"   Initial state: {'Checked' if initial_state else 'Unchecked'}")
            
            # Toggle the checkbox
            checkbox.click()
            new_state = checkbox.is_selected()
            print(f"   After click: {'Checked' if new_state else 'Unchecked'}")
        
        # Only check if not already checked
        print("\n2. Conditional checkbox selection:")
        checkbox1 = checkboxes[0]
        if not checkbox1.is_selected():
            checkbox1.click()
            print("   Checkbox was unchecked, now checked")
        else:
            print("   Checkbox already checked, no action needed")
        
    finally:
        driver.quit()
    
    print("\n✓ Checkbox & radio demo completed\n")


# =============================================================================
# PART 4: DROPDOWN (SELECT) INTERACTIONS
# =============================================================================
def demo_dropdown_select():
    """
    Working with dropdown/select elements.
    
    Uses the Select class for dropdown handling.
    Methods covered:
    - select_by_visible_text()
    - select_by_value()
    - select_by_index()
    - first_selected_option
    - all_selected_options
    - options
    """
    print("=" * 60)
    print("DEMO: Dropdown (Select) Interactions")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/dropdown")
        
        # Find the dropdown element
        dropdown_element = driver.find_element(By.ID, "dropdown")
        
        # Create Select object
        # Java: Select select = new Select(element);
        dropdown = Select(dropdown_element)
        
        print("\n1. Get all options:")
        all_options = dropdown.options
        for opt in all_options:
            print(f"   - '{opt.text}' (value='{opt.get_attribute('value')}')")
        
        # Select by visible text
        print("\n2. select_by_visible_text():")
        dropdown.select_by_visible_text("Option 1")
        selected = dropdown.first_selected_option
        print(f"   Selected: '{selected.text}'")
        
        # Select by value attribute
        print("\n3. select_by_value():")
        dropdown.select_by_value("2")
        selected = dropdown.first_selected_option
        print(f"   Selected: '{selected.text}'")
        
        # Select by index (0-based)
        print("\n4. select_by_index():")
        dropdown.select_by_index(1)  # Second option
        selected = dropdown.first_selected_option
        print(f"   Selected: '{selected.text}'")
        
        # Get currently selected
        print("\n5. first_selected_option:")
        current = dropdown.first_selected_option
        print(f"   Currently selected: '{current.text}'")
        
    finally:
        driver.quit()
    
    print("\n✓ Dropdown demo completed\n")


# =============================================================================
# PART 5: ELEMENT PROPERTIES AND ATTRIBUTES
# =============================================================================
def demo_element_properties():
    """
    Getting element properties, attributes, and CSS values.
    
    Properties covered:
    - text: Get visible text
    - tag_name: Get HTML tag
    - get_attribute(): Get any attribute
    - value_of_css_property(): Get CSS value
    - size, location: Element dimensions and position
    """
    print("=" * 60)
    print("DEMO: Element Properties & Attributes")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/login")
        
        # Find various elements
        heading = driver.find_element(By.TAG_NAME, "h2")
        button = driver.find_element(By.CSS_SELECTOR, "button[type='submit']")
        username = driver.find_element(By.ID, "username")
        
        # text property
        print("\n1. text - Get visible text:")
        print(f"   Heading text: '{heading.text}'")
        print(f"   Button text: '{button.text}'")
        
        # tag_name property
        print("\n2. tag_name - Get HTML tag:")
        print(f"   Heading tag: '{heading.tag_name}'")
        print(f"   Button tag: '{button.tag_name}'")
        
        # get_attribute()
        print("\n3. get_attribute() - Get HTML attributes:")
        print(f"   Username id: '{username.get_attribute('id')}'")
        print(f"   Username name: '{username.get_attribute('name')}'")
        print(f"   Username type: '{username.get_attribute('type')}'")
        print(f"   Username class: '{username.get_attribute('class')}'")
        
        # value_of_css_property()
        print("\n4. value_of_css_property() - Get CSS values:")
        print(f"   Button color: '{button.value_of_css_property('color')}'")
        print(f"   Button background: '{button.value_of_css_property('background-color')}'")
        print(f"   Button font-size: '{button.value_of_css_property('font-size')}'")
        
        # size property
        print("\n5. size - Element dimensions:")
        print(f"   Button size: {button.size}")
        print(f"   Width: {button.size['width']}px")
        print(f"   Height: {button.size['height']}px")
        
        # location property
        print("\n6. location - Element position:")
        print(f"   Button location: {button.location}")
        print(f"   X: {button.location['x']}")
        print(f"   Y: {button.location['y']}")
        
        # rect property (combines size and location)
        print("\n7. rect - Combined dimensions and position:")
        print(f"   Button rect: {button.rect}")
        
    finally:
        driver.quit()
    
    print("\n✓ Element properties demo completed\n")


# =============================================================================
# PART 6: FORM SUBMISSION PATTERNS
# =============================================================================
def demo_form_submission():
    """
    Complete form interaction patterns.
    """
    print("=" * 60)
    print("DEMO: Complete Form Submission")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/login")
        
        print("\n📝 Form Submission Pattern:")
        print("-" * 40)
        
        # Step 1: Find all form elements
        print("\n1. Locating form elements...")
        username = driver.find_element(By.ID, "username")
        password = driver.find_element(By.ID, "password")
        submit_btn = driver.find_element(By.CSS_SELECTOR, "button[type='submit']")
        
        # Step 2: Clear fields (in case of pre-filled data)
        print("2. Clearing fields...")
        username.clear()
        password.clear()
        
        # Step 3: Enter data
        print("3. Entering form data...")
        username.send_keys("tomsmith")
        password.send_keys("SuperSecretPassword!")
        
        # Step 4: Verify data before submit
        print("4. Verifying entered data...")
        assert username.get_attribute("value") == "tomsmith"
        print(f"   Username: {username.get_attribute('value')}")
        
        # Step 5: Submit form
        print("5. Submitting form...")
        submit_btn.click()
        
        # Alternative: Use submit() method
        # username.submit()  # Submits the form containing this element
        
        # Step 6: Verify success
        print("6. Verifying result...")
        time.sleep(1)
        
        flash = driver.find_element(By.ID, "flash")
        if "secure" in driver.current_url:
            print(f"   ✓ Login successful!")
            print(f"   Message: {flash.text.strip()}")
        else:
            print(f"   ✗ Login failed")
            print(f"   Message: {flash.text.strip()}")
        
    finally:
        driver.quit()
    
    print("\n✓ Form submission demo completed\n")


# =============================================================================
# MAIN EXECUTION
# =============================================================================
if __name__ == "__main__":
    print("\n🖱️ ELEMENT INTERACTIONS DEMO 🖱️\n")
    print("This demo covers all WebElement interaction methods")
    print("-" * 60)
    
    demo_text_input()
    demo_button_interactions()
    demo_checkbox_radio()
    demo_dropdown_select()
    demo_element_properties()
    demo_form_submission()
    
    print("\n" + "=" * 60)
    print("ELEMENT INTERACTIONS DEMOS COMPLETED! 🎉")
    print("=" * 60)
    print("\n📝 Quick Reference:")
    print("   element.click()       - Click element")
    print("   element.send_keys()   - Type text")
    print("   element.clear()       - Clear field")
    print("   element.text          - Get visible text")
    print("   element.get_attribute('x') - Get attribute")
    print("   element.is_displayed() - Check visibility")
    print("   element.is_enabled()   - Check if enabled")
    print("   element.is_selected()  - Check if selected")
    print("   Select(element)        - Dropdown wrapper")
    print("=" * 60)

