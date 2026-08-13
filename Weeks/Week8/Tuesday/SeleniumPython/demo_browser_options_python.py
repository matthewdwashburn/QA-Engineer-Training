"""
Demo: Browser Options Configuration
====================================

This demo shows how to configure browser options in Python Selenium,
including headless mode, window size, and various browser arguments.

Learning Objectives:
- Configure ChromeOptions for customized browser behavior
- Run browsers in headless mode
- Set browser arguments and preferences
- Compare Python vs Java options syntax
"""

from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options as ChromeOptions
from selenium.webdriver.firefox.options import Options as FirefoxOptions
from selenium.webdriver.edge.options import Options as EdgeOptions
from webdriver_manager.chrome import ChromeDriverManager


# =============================================================================
# PART 1: CHROME OPTIONS - BASIC CONFIGURATION
# =============================================================================
def demo_basic_chrome_options():
    """
    Basic ChromeOptions configuration.
    
    Java equivalent:
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        WebDriver driver = new ChromeDriver(options);
    """
    print("=" * 60)
    print("DEMO: Basic Chrome Options")
    print("=" * 60)
    
    # Create options object
    options = ChromeOptions()
    
    # Common browser arguments
    options.add_argument("--start-maximized")        # Start maximized
    options.add_argument("--disable-notifications")   # Block notifications
    options.add_argument("--disable-popup-blocking")  # Allow popups
    
    # Create driver with options
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=options)
    
    try:
        driver.get("https://www.google.com")
        
        # Get window size to verify maximized
        size = driver.get_window_size()
        print(f"✓ Browser launched with options")
        print(f"  Window size: {size['width']}x{size['height']}")
        print(f"  Page title: {driver.title}")
        
    finally:
        driver.quit()
    
    print("✓ Basic options demo completed\n")


# =============================================================================
# PART 2: HEADLESS MODE
# =============================================================================
def demo_headless_mode():
    """
    Run Chrome in headless mode (no visible browser window).
    Useful for CI/CD pipelines and server environments.
    
    Java equivalent:
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
    """
    print("=" * 60)
    print("DEMO: Headless Mode")
    print("=" * 60)
    
    options = ChromeOptions()
    
    # New headless mode syntax (Chrome 109+)
    options.add_argument("--headless=new")
    
    # Recommended headless arguments
    options.add_argument("--disable-gpu")           # Disable GPU (helps stability)
    options.add_argument("--no-sandbox")            # Required for some environments
    options.add_argument("--disable-dev-shm-usage") # Overcome limited resource problems
    options.add_argument("--window-size=1920,1080") # Set window size (important in headless)
    
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=options)
    
    try:
        driver.get("https://www.selenium.dev/")
        
        print(f"✓ Headless browser running!")
        print(f"  No browser window visible")
        print(f"  Page title: {driver.title}")
        print(f"  Current URL: {driver.current_url}")
        
        # Take screenshot to prove it works
        driver.save_screenshot("headless_screenshot.png")
        print(f"  Screenshot saved: headless_screenshot.png")
        
    finally:
        driver.quit()
    
    print("✓ Headless mode demo completed\n")


# =============================================================================
# PART 3: WINDOW SIZE AND POSITION
# =============================================================================
def demo_window_configuration():
    """
    Configure browser window size and position.
    Important for consistent test execution and screenshots.
    """
    print("=" * 60)
    print("DEMO: Window Size & Position")
    print("=" * 60)
    
    options = ChromeOptions()
    
    # Set initial window size
    options.add_argument("--window-size=1280,720")
    
    # Start at specific position (x, y from top-left)
    options.add_argument("--window-position=100,100")
    
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=options)
    
    try:
        driver.get("https://www.example.com")
        
        # Get current window info
        size = driver.get_window_size()
        position = driver.get_window_position()
        
        print(f"✓ Initial configuration:")
        print(f"  Size: {size['width']}x{size['height']}")
        print(f"  Position: ({position['x']}, {position['y']})")
        
        # Programmatically change window size
        driver.set_window_size(1920, 1080)
        new_size = driver.get_window_size()
        print(f"\n✓ After resize:")
        print(f"  Size: {new_size['width']}x{new_size['height']}")
        
        # Minimize, then maximize
        driver.minimize_window()
        print("  Window minimized...")
        
        import time
        time.sleep(1)
        
        driver.maximize_window()
        print("  Window maximized!")
        
    finally:
        driver.quit()
    
    print("✓ Window configuration demo completed\n")


# =============================================================================
# PART 4: BROWSER PREFERENCES
# =============================================================================
def demo_browser_preferences():
    """
    Set Chrome preferences using experimental options.
    Controls download directory, PDF handling, etc.
    """
    print("=" * 60)
    print("DEMO: Browser Preferences")
    print("=" * 60)
    
    import os
    
    options = ChromeOptions()
    
    # Set download directory
    download_dir = os.path.join(os.getcwd(), "downloads")
    os.makedirs(download_dir, exist_ok=True)
    
    prefs = {
        # Download settings
        "download.default_directory": download_dir,
        "download.prompt_for_download": False,
        "download.directory_upgrade": True,
        
        # PDF handling - open in Chrome instead of downloading
        "plugins.always_open_pdf_externally": False,
        
        # Disable images for faster loading (useful for scraping)
        # "profile.managed_default_content_settings.images": 2,
        
        # Set default language
        "intl.accept_languages": "en-US,en",
        
        # Disable password manager popup
        "credentials_enable_service": False,
        "profile.password_manager_enabled": False,
    }
    
    options.add_experimental_option("prefs", prefs)
    
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=options)
    
    try:
        driver.get("https://www.google.com")
        
        print(f"✓ Browser configured with custom preferences")
        print(f"  Download directory: {download_dir}")
        print(f"  Password manager: Disabled")
        print(f"  Language: en-US")
        
    finally:
        driver.quit()
    
    print("✓ Browser preferences demo completed\n")


# =============================================================================
# PART 5: EXCLUDE AUTOMATION FLAGS
# =============================================================================
def demo_exclude_automation_flags():
    """
    Hide automation flags to make browser appear more "human".
    Useful when dealing with anti-bot detection.
    """
    print("=" * 60)
    print("DEMO: Exclude Automation Flags")
    print("=" * 60)
    
    options = ChromeOptions()
    
    # Exclude automation flags
    options.add_experimental_option("excludeSwitches", ["enable-automation"])
    options.add_experimental_option("useAutomationExtension", False)
    
    # Disable automation info bar
    options.add_argument("--disable-blink-features=AutomationControlled")
    
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=options)
    
    try:
        driver.get("https://www.google.com")
        
        # Execute script to check if automation is detected
        is_webdriver = driver.execute_script(
            "return navigator.webdriver"
        )
        
        print(f"✓ Automation flags hidden")
        print(f"  navigator.webdriver: {is_webdriver}")
        print(f"  (False means automation is hidden)")
        
    finally:
        driver.quit()
    
    print("✓ Exclude automation flags demo completed\n")


# =============================================================================
# PART 6: ALL OPTIONS COMBINED - PRODUCTION CONFIGURATION
# =============================================================================
def demo_production_configuration():
    """
    Production-ready Chrome configuration combining all best practices.
    """
    print("=" * 60)
    print("DEMO: Production Configuration")
    print("=" * 60)
    
    import os
    
    options = ChromeOptions()
    
    # ---- Performance & Stability ----
    options.add_argument("--disable-gpu")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--disable-extensions")
    
    # ---- Window Configuration ----
    options.add_argument("--start-maximized")
    # Or for headless: options.add_argument("--headless=new")
    
    # ---- Security & Privacy ----
    options.add_argument("--incognito")
    options.add_argument("--disable-notifications")
    options.add_argument("--disable-popup-blocking")
    
    # ---- Hide Automation ----
    options.add_experimental_option("excludeSwitches", ["enable-automation"])
    options.add_argument("--disable-blink-features=AutomationControlled")
    
    # ---- Preferences ----
    download_dir = os.path.join(os.getcwd(), "downloads")
    os.makedirs(download_dir, exist_ok=True)
    
    prefs = {
        "download.default_directory": download_dir,
        "download.prompt_for_download": False,
        "credentials_enable_service": False,
        "profile.password_manager_enabled": False,
    }
    options.add_experimental_option("prefs", prefs)
    
    # Create driver
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=options)
    
    try:
        driver.get("https://www.selenium.dev/")
        
        print(f"✓ Production configuration applied:")
        print(f"  - Performance optimizations: ON")
        print(f"  - Incognito mode: ON")
        print(f"  - Automation hidden: ON")
        print(f"  - Custom download directory: {download_dir}")
        print(f"  - Page loaded: {driver.title}")
        
    finally:
        driver.quit()
    
    print("✓ Production configuration demo completed\n")


# =============================================================================
# MAIN EXECUTION
# =============================================================================
if __name__ == "__main__":
    print("\n⚙️ BROWSER OPTIONS CONFIGURATION DEMO ⚙️\n")
    print("This demo shows various Chrome configuration options")
    print("-" * 60)
    
    demo_basic_chrome_options()
    demo_headless_mode()
    demo_window_configuration()
    demo_browser_preferences()
    demo_exclude_automation_flags()
    demo_production_configuration()
    
    print("\n" + "=" * 60)
    print("BROWSER OPTIONS DEMOS COMPLETED! 🎉")
    print("=" * 60)

