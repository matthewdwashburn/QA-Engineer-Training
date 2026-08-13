"""
Demo: ActionChains API in Python Selenium
=========================================

This demo covers the ActionChains class for complex mouse
and keyboard interactions.

Learning Objectives:
- Use ActionChains for mouse actions (hover, drag, double-click)
- Perform keyboard actions (key combinations)
- Chain multiple actions together
"""

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.action_chains import ActionChains
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
# PART 1: MOUSE HOVER (MOVE TO ELEMENT)
# =============================================================================
def demo_mouse_hover():
    """
    Mouse hover action using move_to_element().
    
    Java equivalent:
        Actions actions = new Actions(driver);
        actions.moveToElement(element).perform();
    """
    print("=" * 60)
    print("DEMO: Mouse Hover")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/hovers")
        
        # Create ActionChains object
        actions = ActionChains(driver)
        
        # Find the user images
        figures = driver.find_elements(By.CLASS_NAME, "figure")
        
        print("\nHovering over user profiles:")
        
        for i, figure in enumerate(figures, 1):
            # Move mouse to element
            actions.move_to_element(figure).perform()
            time.sleep(0.5)
            
            # After hover, caption becomes visible
            caption = figure.find_element(By.CLASS_NAME, "figcaption")
            name = caption.find_element(By.TAG_NAME, "h5").text
            
            print(f"   User {i}: {name}")
        
        print("\n✓ Mouse hover revealed hidden content!")
        
    finally:
        driver.quit()
    
    print("✓ Mouse hover demo completed\n")


# =============================================================================
# PART 2: DRAG AND DROP
# =============================================================================
def demo_drag_and_drop():
    """
    Drag and drop using ActionChains.
    
    Methods:
    - drag_and_drop(source, target)
    - drag_and_drop_by_offset(source, x, y)
    """
    print("=" * 60)
    print("DEMO: Drag and Drop")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/drag_and_drop")
        
        actions = ActionChains(driver)
        
        # Find source and target elements
        column_a = driver.find_element(By.ID, "column-a")
        column_b = driver.find_element(By.ID, "column-b")
        
        print("\nBefore drag:")
        print(f"   Column A header: {column_a.text}")
        print(f"   Column B header: {column_b.text}")
        
        # Perform drag and drop
        actions.drag_and_drop(column_a, column_b).perform()
        time.sleep(1)
        
        # Verify the swap
        column_a = driver.find_element(By.ID, "column-a")
        column_b = driver.find_element(By.ID, "column-b")
        
        print("\nAfter drag:")
        print(f"   Column A header: {column_a.text}")
        print(f"   Column B header: {column_b.text}")
        
    finally:
        driver.quit()
    
    print("\n✓ Drag and drop demo completed\n")


# =============================================================================
# PART 3: DOUBLE CLICK AND CONTEXT CLICK
# =============================================================================
def demo_click_variants():
    """
    Different click types using ActionChains.
    
    Methods:
    - double_click(element)
    - context_click(element) - right click
    """
    print("=" * 60)
    print("DEMO: Double Click & Context Click")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        # Context click (right-click)
        driver.get("https://the-internet.herokuapp.com/context_menu")
        
        actions = ActionChains(driver)
        
        hot_spot = driver.find_element(By.ID, "hot-spot")
        
        print("\n1. Context Click (Right-click):")
        actions.context_click(hot_spot).perform()
        time.sleep(1)
        
        # Handle the alert that appears
        alert = driver.switch_to.alert
        print(f"   Alert text: '{alert.text}'")
        alert.accept()
        print("   ✓ Context menu triggered!")
        
    finally:
        driver.quit()
    
    print("\n✓ Click variants demo completed\n")


# =============================================================================
# PART 4: KEYBOARD ACTIONS
# =============================================================================
def demo_keyboard_actions():
    """
    Keyboard actions using ActionChains.
    
    Methods:
    - send_keys(keys) - Type keys
    - key_down(key) - Hold key down
    - key_up(key) - Release key
    """
    print("=" * 60)
    print("DEMO: Keyboard Actions")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/key_presses")
        
        actions = ActionChains(driver)
        input_field = driver.find_element(By.ID, "target")
        result = driver.find_element(By.ID, "result")
        
        print("\n1. Simple key press:")
        input_field.click()
        actions.send_keys("A").perform()
        time.sleep(0.5)
        print(f"   Result: {result.text}")
        
        print("\n2. Special keys:")
        actions.send_keys(Keys.TAB).perform()
        time.sleep(0.5)
        print(f"   TAB pressed: {result.text}")
        
        actions.send_keys(Keys.ENTER).perform()
        time.sleep(0.5)
        print(f"   ENTER pressed: {result.text}")
        
        print("\n3. Key combinations (Ctrl+A):")
        # key_down holds the key, send_keys types, key_up releases
        actions.key_down(Keys.CONTROL).send_keys("a").key_up(Keys.CONTROL).perform()
        time.sleep(0.5)
        
        print("\n4. Available special keys (Keys class):")
        special_keys = ['ENTER', 'TAB', 'ESCAPE', 'BACKSPACE', 'DELETE',
                       'ARROW_UP', 'ARROW_DOWN', 'F1', 'F12', 'CONTROL', 'SHIFT', 'ALT']
        for key in special_keys[:6]:
            print(f"   Keys.{key}")
        
    finally:
        driver.quit()
    
    print("\n✓ Keyboard actions demo completed\n")


# =============================================================================
# PART 5: CHAINING MULTIPLE ACTIONS
# =============================================================================
def demo_action_chaining():
    """
    Chain multiple actions together before performing.
    """
    print("=" * 60)
    print("DEMO: Action Chaining")
    print("=" * 60)
    
    driver = create_driver()
    
    try:
        driver.get("https://the-internet.herokuapp.com/login")
        
        actions = ActionChains(driver)
        
        username = driver.find_element(By.ID, "username")
        password = driver.find_element(By.ID, "password")
        
        print("\nChaining multiple actions:")
        print("   1. Click username field")
        print("   2. Type username")
        print("   3. Tab to password")
        print("   4. Type password")
        print("   5. Press Enter to submit")
        
        # Chain all actions together
        actions.click(username) \
               .send_keys("tomsmith") \
               .send_keys(Keys.TAB) \
               .send_keys("SuperSecretPassword!") \
               .send_keys(Keys.ENTER) \
               .perform()
        
        time.sleep(1)
        print(f"\n   Current URL: {driver.current_url}")
        print("   ✓ All actions performed in sequence!")
        
    finally:
        driver.quit()
    
    print("\n✓ Action chaining demo completed\n")


# =============================================================================
# MAIN EXECUTION
# =============================================================================
if __name__ == "__main__":
    print("\n⌨️ ACTIONCHAINS API DEMO ⌨️\n")
    print("This demo covers advanced mouse and keyboard interactions")
    print("-" * 60)
    
    demo_mouse_hover()
    demo_drag_and_drop()
    demo_click_variants()
    demo_keyboard_actions()
    demo_action_chaining()
    
    print("\n" + "=" * 60)
    print("ACTIONCHAINS DEMOS COMPLETED! 🎉")
    print("=" * 60)

