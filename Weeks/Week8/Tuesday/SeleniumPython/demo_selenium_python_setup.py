"""
Demo: Selenium Python Project Setup
===================================

This demo shows how to set up a Python Selenium project from scratch,
comparing the Python approach to the Java approach learned in Week 7.

Learning Objectives:
- Create a basic Selenium test in Python
- Use webdriver-manager for automatic driver management
- Compare Python syntax with Java Selenium
"""
#pip install selenium webdriver-manager
# =============================================================================
# PART 1: Basic Imports
# =============================================================================
# Compare to Java:
#   import org.openqa.selenium.WebDriver;
#   import org.openqa.selenium.chrome.ChromeDriver;

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

# =============================================================================
# PART 2: Simple Script Approach (Quick and Easy)
# =============================================================================
def demo_simple_approach():
    """
    Basic Selenium script - no class required!
    In Java, you'd need a class with public static void main.
    Python lets you script directly.
    """
    print("=" * 60)
    print("DEMO: Simple Script Approach")
    print("=" * 60)
    
    # Setup driver with automatic ChromeDriver management
    # Java equivalent: System.setProperty("webdriver.chrome.driver", path);
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)
    
    try:
        # Navigate to a page
        # Java: driver.get("https://www.python.org");
        driver.get("https://www.python.org")
        
        # Get page title
        # Java: String title = driver.getTitle();
        print(f"Page Title: {driver.title}")
        
        # Get current URL
        # Java: String url = driver.getCurrentUrl();
        print(f"Current URL: {driver.current_url}")
        
        # Find an element and interact
        # Java: driver.findElement(By.id("id-search-field")).sendKeys("selenium");
        search_box = driver.find_element(By.ID, "id-search-field")
        search_box.send_keys("selenium")
        search_box.submit()
        
        print(f"After search URL: {driver.current_url}")
        print("✓ Simple demo completed successfully!")
        
    finally:
        # Always clean up - Java: driver.quit();
        driver.quit()


# =============================================================================
# PART 3: Context Manager Approach (Pythonic Best Practice)
# =============================================================================
from contextlib import contextmanager

@contextmanager
def create_chrome_driver():
    """
    Context manager ensures browser closes even if errors occur.
    This is a Pythonic pattern not available in Java.
    
    Usage:
        with create_chrome_driver() as driver:
            driver.get("https://example.com")
            # Browser automatically closes after this block
    """
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)
    try:
        yield driver
    finally:
        driver.quit()


def demo_context_manager_approach():
    """
    Using context manager for safe browser handling.
    Browser is guaranteed to close even if exceptions occur.
    """
    print("\n" + "=" * 60)
    print("DEMO: Context Manager Approach (Pythonic)")
    print("=" * 60)
    
    with create_chrome_driver() as driver:
        driver.get("https://www.selenium.dev/")
        print(f"Page Title: {driver.title}")
        
        # Find all navigation links using list comprehension
        # Java would need a loop: for (WebElement link : links) { ... }
        nav_links = driver.find_elements(By.CSS_SELECTOR, "nav a")
        link_texts = [link.text for link in nav_links if link.text.strip()]
        
        print(f"Navigation links found: {len(link_texts)}")
        for text in link_texts[:5]:  # Show first 5
            print(f"  - {text}")
        
        print("✓ Context manager demo completed successfully!")
    
    # Browser is automatically closed here - no explicit quit() needed!


# =============================================================================
# PART 4: Class-Based Approach (For Larger Projects)
# =============================================================================
class SeleniumDemo:
    """
    Class-based approach for more structured test automation.
    Similar to Java class structure but more flexible.
    """
    
    def __init__(self):
        """Initialize the WebDriver - like Java constructor."""
        self.service = Service(ChromeDriverManager().install())
        self.driver = webdriver.Chrome(service=self.service)
        # Set implicit wait (10 seconds) - Java: driver.manage().timeouts().implicitlyWait()
        self.driver.implicitly_wait(10)
    
    def navigate_to(self, url: str) -> None:
        """Navigate to a URL."""
        self.driver.get(url)
    
    def get_title(self) -> str:
        """Get page title."""
        return self.driver.title
    
    def find_element_by_id(self, element_id: str):
        """Find element by ID."""
        return self.driver.find_element(By.ID, element_id)
    
    def close(self) -> None:
        """Close the browser."""
        self.driver.quit()


def demo_class_based_approach():
    """Demonstrate class-based Selenium usage."""
    print("\n" + "=" * 60)
    print("DEMO: Class-Based Approach")
    print("=" * 60)
    
    demo = SeleniumDemo()
    try:
        demo.navigate_to("https://the-internet.herokuapp.com/")
        print(f"Page Title: {demo.get_title()}")
        
        # Find all example links
        links = demo.driver.find_elements(By.TAG_NAME, "a")
        print(f"Found {len(links)} links on the page")
        
        print("✓ Class-based demo completed successfully!")
        
    finally:
        demo.close()


# =============================================================================
# MAIN EXECUTION
# =============================================================================
if __name__ == "__main__":
    print("\n🐍 SELENIUM PYTHON SETUP DEMO 🐍\n")
    print("This demo shows three approaches to Python Selenium:\n")
    print("1. Simple Script - Quick and easy for small tasks")
    print("2. Context Manager - Pythonic, safe resource handling")
    print("3. Class-Based - Structured approach for larger projects")
    print("\n" + "-" * 60 + "\n")
    
    # Run all demos
    demo_simple_approach()
    demo_context_manager_approach()
    demo_class_based_approach()
    
    print("\n" + "=" * 60)
    print("ALL DEMOS COMPLETED SUCCESSFULLY! 🎉")
    print("=" * 60)

