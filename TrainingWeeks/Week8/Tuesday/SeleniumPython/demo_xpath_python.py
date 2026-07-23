"""
Demo: XPath Mastery in Python Selenium
======================================

This demo provides a deep dive into XPath locators,
covering syntax, functions, and advanced techniques.

Learning Objectives:
- Master XPath syntax fundamentals
- Use XPath functions (contains, starts-with, text, normalize-space)
- Understand XPath axes for DOM traversal
- Build robust XPath expressions for dynamic elements
"""

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager


def create_driver():
    """Create and return a Chrome WebDriver."""
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)
    driver.implicitly_wait(5)
    return driver


# =============================================================================
# PART 1: XPATH SYNTAX FUNDAMENTALS
# =============================================================================
def demo_xpath_basics():
    """
    Basic XPath syntax and notation.
    
    Key Concepts:
    - // : Select nodes from anywhere in document
    - /  : Select from root (absolute) or child (relative)
    - @  : Select attributes
    - [] : Predicate (filter)
    """
    print("=" * 60)
    print("DEMO: XPath Basics")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/")
        
        print("\nXPath Notation Guide:")
        print("-" * 40)
        
        # Absolute XPath (NOT recommended - fragile)
        print("\n1. Absolute XPath (avoid in tests):")
        print("   /html/body/div/div/h1")
        print("   ⚠️ Breaks when DOM structure changes")
        
        # Relative XPath (recommended)
        print("\n2. Relative XPath (recommended):")
        print("   //h1  - Find h1 anywhere in document")
        
        h1 = driver.find_element(By.XPATH, "//h1")
        print(f"   Found: <h1>{h1.text}</h1>")
        
        # With attributes
        print("\n3. XPath with attributes:")
        print("   //tag[@attribute='value']")
        
        heading = driver.find_element(By.XPATH, "//h1[@class='heading']")
        print(f"   //h1[@class='heading'] → '{heading.text}'")
        
        # Multiple attributes
        print("\n4. Multiple attributes (AND):")
        print("   //input[@id='x' and @type='text']")
        
        driver.get("https://the-internet.herokuapp.com/login")
        username = driver.find_element(
            By.XPATH, "//input[@id='username' and @type='text']"
        )
        print(f"   Found: <input id='{username.get_attribute('id')}'>")
        
        # OR condition
        print("\n5. OR condition:")
        print("   //input[@id='username' or @id='password']")
        
        inputs = driver.find_elements(
            By.XPATH, "//input[@id='username' or @id='password']"
        )
        print(f"   Found {len(inputs)} matching inputs")
        
    finally:
        driver.quit()
    
    print("\n✓ XPath basics demo completed\n")


# =============================================================================
# PART 2: XPATH FUNCTIONS
# =============================================================================
def demo_xpath_functions():
    """
    XPath functions for flexible element matching.
    
    Functions covered:
    - contains()
    - starts-with()
    - text()
    - normalize-space()
    """
    print("=" * 60)
    print("DEMO: XPath Functions")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/")
        
        # contains() function
        print("\n1. contains(attribute, 'value'):")
        print("   Matches partial attribute values")
        print("   //a[contains(@href, 'login')]")
        
        login_link = driver.find_element(
            By.XPATH, "//a[contains(@href, 'login')]"
        )
        print(f"   Found: {login_link.text}")
        print(f"   href: {login_link.get_attribute('href')}")
        
        # contains() with text
        print("\n2. contains(text(), 'value'):")
        print("   Matches partial text content")
        print("   //a[contains(text(), 'Form')]")
        
        form_links = driver.find_elements(
            By.XPATH, "//a[contains(text(), 'Form')]"
        )
        print(f"   Found {len(form_links)} links containing 'Form':")
        for link in form_links:
            print(f"     - {link.text}")
        
        # starts-with() function
        print("\n3. starts-with(attribute, 'value'):")
        print("   Matches attribute prefix")
        print("   //a[starts-with(@href, '/')]")
        
        internal_links = driver.find_elements(
            By.XPATH, "//a[starts-with(@href, '/')]"
        )
        print(f"   Found {len(internal_links)} internal links")
        
        # text() function
        print("\n4. text() - exact text match:")
        print("   //a[text()='Form Authentication']")
        
        exact_match = driver.find_element(
            By.XPATH, "//a[text()='Form Authentication']"
        )
        print(f"   Found: '{exact_match.text}'")
        
        # normalize-space() function
        print("\n5. normalize-space() - handles whitespace:")
        print("   //h1[normalize-space()='Welcome to the-internet']")
        
        # This handles elements with extra whitespace
        h1 = driver.find_element(
            By.XPATH, "//h1[normalize-space()='Welcome to the-internet']"
        )
        print(f"   Found: '{h1.text}'")
        
        # Combining functions
        print("\n6. Combining functions:")
        print("   //a[contains(text(), 'Auth') and contains(@href, 'login')]")
        
        combined = driver.find_element(
            By.XPATH, "//a[contains(text(), 'Auth') and contains(@href, 'login')]"
        )
        print(f"   Found: '{combined.text}'")
        
    finally:
        driver.quit()
    
    print("\n✓ XPath functions demo completed\n")


# =============================================================================
# PART 3: XPATH AXES
# =============================================================================
def demo_xpath_axes():
    """
    XPath axes for navigating DOM relationships.
    
    Axes covered:
    - parent, ancestor
    - child, descendant
    - following-sibling, preceding-sibling
    - following, preceding
    """
    print("=" * 60)
    print("DEMO: XPath Axes")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/tables")
        
        print("\nXPath Axes - Navigating DOM Relationships:")
        print("-" * 45)
        
        # Parent axis
        print("\n1. parent:: - Select parent element")
        print("   //td[@class='dues']/parent::tr")
        
        parent_row = driver.find_element(
            By.XPATH, "//td[@class='dues']/parent::tr"
        )
        print(f"   Found parent <tr>, text: {parent_row.text[:50]}...")
        
        # Ancestor axis
        print("\n2. ancestor:: - Select ancestor element(s)")
        print("   //td[@class='dues']/ancestor::table")
        
        table = driver.find_element(
            By.XPATH, "//td[@class='dues']/ancestor::table"
        )
        print(f"   Found ancestor <{table.tag_name}>")
        
        # Child axis
        print("\n3. child:: - Select child elements")
        print("   //table[@id='table1']/child::thead")
        
        thead = driver.find_element(
            By.XPATH, "//table[@id='table1']/child::thead"
        )
        print(f"   Found child <{thead.tag_name}>")
        
        # Following-sibling axis
        print("\n4. following-sibling:: - Select siblings after")
        print("   //th[text()='First Name']/following-sibling::th[1]")
        
        next_th = driver.find_element(
            By.XPATH, "//th[text()='First Name']/following-sibling::th[1]"
        )
        print(f"   Next header: '{next_th.text}'")
        
        # Preceding-sibling axis
        print("\n5. preceding-sibling:: - Select siblings before")
        print("   //th[text()='Due']/preceding-sibling::th[1]")
        
        prev_th = driver.find_element(
            By.XPATH, "//th[text()='Due']/preceding-sibling::th[1]"
        )
        print(f"   Previous header: '{prev_th.text}'")
        
        # Descendant axis
        print("\n6. descendant:: - Select all descendants")
        print("   //table[@id='table1']/descendant::td")
        
        all_cells = driver.find_elements(
            By.XPATH, "//table[@id='table1']/descendant::td"
        )
        print(f"   Found {len(all_cells)} <td> descendants")
        
    finally:
        driver.quit()
    
    print("\n✓ XPath axes demo completed\n")


# =============================================================================
# PART 4: PRACTICAL XPATH PATTERNS
# =============================================================================
def demo_practical_patterns():
    """
    Real-world XPath patterns for common scenarios.
    """
    print("=" * 60)
    print("DEMO: Practical XPath Patterns")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        # Pattern 1: Find by partial ID (dynamic IDs)
        print("\n1. Dynamic ID pattern:")
        print("   //input[contains(@id, 'user')]")
        
        driver.get("https://the-internet.herokuapp.com/login")
        dynamic_id = driver.find_element(
            By.XPATH, "//input[contains(@id, 'user')]"
        )
        print(f"   Found: id='{dynamic_id.get_attribute('id')}'")
        
        # Pattern 2: Find button by text
        print("\n2. Button by text:")
        print("   //button[contains(., 'Login')]")
        
        button = driver.find_element(
            By.XPATH, "//button[contains(., 'Login')]"
        )
        print(f"   Found: <button>'{button.text}'</button>")
        
        # Pattern 3: Table cell by row/column
        print("\n3. Table cell by position:")
        driver.get("https://the-internet.herokuapp.com/tables")
        print("   //table[@id='table1']//tr[2]/td[3]")
        
        cell = driver.find_element(
            By.XPATH, "//table[@id='table1']//tr[2]/td[3]"
        )
        print(f"   Row 2, Column 3: '{cell.text}'")
        
        # Pattern 4: Element after label
        print("\n4. Input after label (form pattern):")
        print("   //label[text()='Email']/following::input[1]")
        
        driver.get("https://the-internet.herokuapp.com/login")
        # Note: This site doesn't have labels, showing pattern only
        print("   (Pattern for forms with labels)")
        
        # Pattern 5: Last element
        print("\n5. Last element of type:")
        print("   //ul/li[last()]")
        
        driver.get("https://the-internet.herokuapp.com/")
        last_link = driver.find_element(
            By.XPATH, "//ul/li[last()]/a"
        )
        print(f"   Last link in list: '{last_link.text}'")
        
        # Pattern 6: Position-based
        print("\n6. Position-based selection:")
        print("   (//a)[1] - First link on page")
        print("   (//a)[position() <= 5] - First 5 links")
        
        first_five = driver.find_elements(
            By.XPATH, "(//ul)[1]/li[position() <= 5]/a"
        )
        print(f"   First 5 links in list:")
        for link in first_five:
            print(f"     - {link.text}")
        
        # Pattern 7: NOT condition
        print("\n7. NOT condition:")
        print("   //input[not(@type='hidden')]")
        
        driver.get("https://the-internet.herokuapp.com/login")
        visible_inputs = driver.find_elements(
            By.XPATH, "//input[not(@type='hidden')]"
        )
        print(f"   Found {len(visible_inputs)} non-hidden inputs")
        
    finally:
        driver.quit()
    
    print("\n✓ Practical patterns demo completed\n")


# =============================================================================
# PART 5: XPATH DEBUGGING TIPS
# =============================================================================
def demo_xpath_debugging():
    """
    Tips for debugging and validating XPath expressions.
    """
    print("=" * 60)
    print("DEMO: XPath Debugging Tips")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/tables")
        
        print("\n🔧 XPath Debugging Techniques:\n")
        
        # Technique 1: Validate in DevTools
        print("1. Validate in Browser DevTools:")
        print("   - Press F12 to open DevTools")
        print("   - Go to Console tab")
        print("   - Type: $x(\"//your/xpath\")")
        print("   - This returns matching elements")
        print()
        
        # Technique 2: Count elements first
        print("2. Count elements before single selection:")
        xpath = "//table[@id='table1']//td"
        elements = driver.find_elements(By.XPATH, xpath)
        print(f"   XPath: {xpath}")
        print(f"   Count: {len(elements)} elements found")
        print()
        
        # Technique 3: Build incrementally
        print("3. Build XPath incrementally:")
        print("   Start: //table")
        print("   Add:   //table[@id='table1']")
        print("   Add:   //table[@id='table1']//tr")
        print("   Add:   //table[@id='table1']//tr[2]/td[1]")
        
        final_xpath = "//table[@id='table1']//tr[2]/td[1]"
        result = driver.find_element(By.XPATH, final_xpath)
        print(f"   Result: '{result.text}'")
        print()
        
        # Technique 4: Check for unique match
        print("4. Verify XPath uniqueness:")
        test_xpaths = [
            "//td",  # Too broad
            "//td[@class='dues']",  # Still multiple
            "(//td[@class='dues'])[1]",  # Unique - first one
        ]
        
        for xpath in test_xpaths:
            count = len(driver.find_elements(By.XPATH, xpath))
            status = "✓ Unique" if count == 1 else f"⚠ {count} matches"
            print(f"   {xpath}")
            print(f"      {status}")
        
    finally:
        driver.quit()
    
    print("\n✓ XPath debugging demo completed\n")


# =============================================================================
# MAIN EXECUTION
# =============================================================================
if __name__ == "__main__":
    print("\n🎯 XPATH MASTERY DEMO 🎯\n")
    print("This demo provides a deep dive into XPath locators")
    print("-" * 60)
    
    demo_xpath_basics()
    demo_xpath_functions()
    demo_xpath_axes()
    demo_practical_patterns()
    demo_xpath_debugging()
    
    print("\n" + "=" * 60)
    print("XPATH MASTERY DEMOS COMPLETED! 🎉")
    print("=" * 60)
    print("\n📝 Quick Reference:")
    print("   //tag             - Select tag anywhere")
    print("   //tag[@attr='x']  - With attribute")
    print("   contains()        - Partial match")
    print("   text()            - Text content")
    print("   parent::          - Navigate to parent")
    print("   following-sibling:: - Next sibling")
    print("=" * 60)

