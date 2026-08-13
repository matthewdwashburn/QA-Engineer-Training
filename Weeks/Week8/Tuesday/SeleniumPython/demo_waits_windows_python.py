"""
Demo: Waits and Window Handling in Python Selenium
=================================================

This demo covers waiting strategies and multi-window handling.

Learning Objectives:
- Implement implicit and explicit waits
- Use WebDriverWait with expected_conditions
- Handle multiple windows/tabs
- Work with frames and iframes
"""

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException
from webdriver_manager.chrome import ChromeDriverManager
import time


def create_driver():
    """Create and return a Chrome WebDriver."""
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)
    return driver


# =============================================================================
# PART 1: IMPLICIT WAIT
# =============================================================================
def demo_implicit_wait():
    """
    Implicit wait - global setting that applies to all find operations.
    
    Java equivalent:
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    """
    print("=" * 60)
    print("DEMO: Implicit Wait")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        # Set implicit wait (applies globally)
        driver.implicitly_wait(10)  # Wait up to 10 seconds
        
        print("\n1. Implicit wait set to 10 seconds")
        print("   - Applies to ALL find_element calls")
        print("   - Polls DOM until element found or timeout")
        
        driver.get("https://the-internet.herokuapp.com/dynamic_loading/1")
        
        # Click to start loading
        start_btn = driver.find_element(By.CSS_SELECTOR, "#start button")
        start_btn.click()
        
        # Element exists but is hidden - implicit wait WON'T help here!
        print("\n2. ⚠️ Implicit wait limitation:")
        print("   - Only waits for element presence in DOM")
        print("   - Does NOT wait for visibility/clickability")
        
    finally:
        driver.quit()
    
    print("\n✓ Implicit wait demo completed\n")


# =============================================================================
# PART 2: EXPLICIT WAIT (RECOMMENDED)
# =============================================================================
def demo_explicit_wait():
    """
    Explicit wait - waits for specific conditions.
    
    Java equivalent:
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("x")));
    """
    print("=" * 60)
    print("DEMO: Explicit Wait (Recommended)")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/dynamic_loading/1")
        
        # Create WebDriverWait instance
        wait = WebDriverWait(driver, 10)  # 10 second timeout
        
        print("\n1. WebDriverWait with expected_conditions:")
        
        # Click start button
        start_btn = driver.find_element(By.CSS_SELECTOR, "#start button")
        start_btn.click()
        
        # Wait for element to be visible (not just present)
        print("   Waiting for 'Hello World!' to appear...")
        
        hello_element = wait.until(
            EC.visibility_of_element_located((By.ID, "finish"))
        )
        
        print(f"   ✓ Element visible: '{hello_element.text}'")
        
        # Common expected conditions
        print("\n2. Common Expected Conditions:")
        conditions = [
            "EC.presence_of_element_located((By.ID, 'x'))",
            "EC.visibility_of_element_located((By.ID, 'x'))",
            "EC.element_to_be_clickable((By.ID, 'x'))",
            "EC.text_to_be_present_in_element((By.ID, 'x'), 'text')",
            "EC.title_contains('text')",
            "EC.url_contains('path')",
            "EC.alert_is_present()",
        ]
        for cond in conditions:
            print(f"   - {cond}")
        
    finally:
        driver.quit()
    
    print("\n✓ Explicit wait demo completed\n")


# =============================================================================
# PART 3: HANDLING TIMEOUT EXCEPTIONS
# =============================================================================
def demo_timeout_handling():
    """
    Handling timeout exceptions gracefully.
    """
    print("=" * 60)
    print("DEMO: Timeout Exception Handling")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/")
        
        wait = WebDriverWait(driver, 3)  # Short timeout for demo
        
        print("\n1. Trying to find non-existent element...")
        
        try:
            element = wait.until(
                EC.presence_of_element_located((By.ID, "non_existent"))
            )
        except TimeoutException:
            print("   ✓ TimeoutException caught!")
            print("   - Element not found within 3 seconds")
        
        print("\n2. Best practice - with fallback:")
        try:
            element = wait.until(
                EC.presence_of_element_located((By.ID, "non_existent"))
            )
            print(f"   Found: {element.text}")
        except TimeoutException:
            print("   Element not found, using fallback behavior")
            # Continue with alternative logic
        
    finally:
        driver.quit()
    
    print("\n✓ Timeout handling demo completed\n")


# =============================================================================
# PART 4: WINDOW HANDLING
# =============================================================================
def demo_window_handling():
    """
    Handle multiple browser windows/tabs.
    """
    print("=" * 60)
    print("DEMO: Window Handling")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/windows")
        
        # Get current window handle
        main_window = driver.current_window_handle
        print(f"\n1. Main window handle: {main_window[:20]}...")
        print(f"   Window handles: {len(driver.window_handles)}")
        
        # Click link that opens new window
        driver.find_element(By.LINK_TEXT, "Click Here").click()
        time.sleep(1)
        
        print(f"\n2. After clicking link:")
        print(f"   Window handles: {len(driver.window_handles)}")
        
        # Switch to new window
        for handle in driver.window_handles:
            if handle != main_window:
                driver.switch_to.window(handle)
                break
        
        print(f"\n3. Switched to new window:")
        print(f"   Title: {driver.title}")
        print(f"   URL: {driver.current_url}")
        
        # Get content from new window
        new_window_text = driver.find_element(By.TAG_NAME, "h3").text
        print(f"   Content: {new_window_text}")
        
        # Close new window
        driver.close()
        print("\n4. Closed new window")
        
        # Switch back to main window
        driver.switch_to.window(main_window)
        print(f"   Back to main: {driver.title}")
        
    finally:
        driver.quit()
    
    print("\n✓ Window handling demo completed\n")


# =============================================================================
# PART 5: OPENING NEW WINDOWS/TABS (SELENIUM 4)
# =============================================================================
def demo_new_window_tab():
    """
    Open new windows/tabs programmatically (Selenium 4 feature).
    """
    print("=" * 60)
    print("DEMO: Opening New Windows/Tabs (Selenium 4)")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://www.google.com")
        print(f"\n1. Initial window: {driver.title}")
        
        # Open new tab (Selenium 4)
        driver.switch_to.new_window('tab')
        driver.get("https://www.python.org")
        print(f"\n2. New tab opened: {driver.title}")
        print(f"   Total windows: {len(driver.window_handles)}")
        
        # Open new window
        driver.switch_to.new_window('window')
        driver.get("https://selenium.dev")
        print(f"\n3. New window opened: {driver.title}")
        print(f"   Total windows: {len(driver.window_handles)}")
        
    finally:
        driver.quit()
    
    print("\n✓ New window/tab demo completed\n")


# =============================================================================
# PART 6: FRAME HANDLING
# =============================================================================
def demo_frame_handling():
    """
    Working with iframes and frames.
    """
    print("=" * 60)
    print("DEMO: Frame/IFrame Handling")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/iframe")
        
        print("\n1. Page loaded with iframe")
        
        # Switch to iframe by element
        wait = WebDriverWait(driver, 10)
        iframe = wait.until(
            EC.presence_of_element_located((By.ID, "mce_0_ifr"))
        )
        
        driver.switch_to.frame(iframe)
        print("   Switched to iframe by element")
        
        # Now we can interact with content inside iframe
        body = driver.find_element(By.ID, "tinymce")
        print(f"   Content inside iframe: '{body.text[:50]}...'")
        
        # Switch back to main content
        driver.switch_to.default_content()
        print("\n2. Switched back to main content")
        
        # Verify we're back by finding element outside iframe
        heading = driver.find_element(By.TAG_NAME, "h3")
        print(f"   Heading: '{heading.text}'")
        
        print("\n3. Frame switching methods:")
        print("   - driver.switch_to.frame(element)")
        print("   - driver.switch_to.frame('frame_name')")
        print("   - driver.switch_to.frame(index)")
        print("   - driver.switch_to.default_content()")
        print("   - driver.switch_to.parent_frame()")
        
    finally:
        driver.quit()
    
    print("\n✓ Frame handling demo completed\n")


# =============================================================================
# MAIN EXECUTION
# =============================================================================
if __name__ == "__main__":
    print("\n⏱️ WAITS & WINDOW HANDLING DEMO ⏱️\n")
    print("This demo covers waiting strategies and multi-window handling")
    print("-" * 60)
    
    demo_implicit_wait()
    demo_explicit_wait()
    demo_timeout_handling()
    demo_window_handling()
    demo_new_window_tab()
    demo_frame_handling()
    
    print("\n" + "=" * 60)
    print("WAITS & WINDOW HANDLING DEMOS COMPLETED! 🎉")
    print("=" * 60)

