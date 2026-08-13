"""
Demo: Driver Management - Manual vs Automated
==============================================

This demo compares manual WebDriver setup with the webdriver-manager
package for automatic driver management.

Learning Objectives:
- Understand manual driver setup challenges
- Use webdriver-manager for automatic driver management
- Configure drivers for Chrome, Firefox, and Edge
"""

from selenium import webdriver
from selenium.webdriver.chrome.service import Service as ChromeService
from selenium.webdriver.firefox.service import Service as FirefoxService
from selenium.webdriver.edge.service import Service as EdgeService

# =============================================================================
# PART 1: MANUAL DRIVER SETUP (The Old Way - Comment Out in Practice)
# =============================================================================
"""
MANUAL SETUP - NOT RECOMMENDED (Shown for educational purposes)
----------------------------------------------------------------
This approach requires:
1. Download the correct driver version for your browser
2. Match driver version to browser version EXACTLY
3. Update drivers whenever browser updates
4. Manage PATH or specify executable location

Problems:
- Version mismatches cause cryptic errors
- Manual updates are tedious
- Different team members may have different versions
- CI/CD pipelines need driver management

# Manual setup example (DO NOT USE):
from selenium.webdriver.chrome.service import Service
service = Service(executable_path="C:/drivers/chromedriver.exe")
driver = webdriver.Chrome(service=service)
"""


# =============================================================================
# PART 2: AUTOMATED DRIVER SETUP (The Modern Way)
# =============================================================================
from webdriver_manager.chrome import ChromeDriverManager
from webdriver_manager.firefox import GeckoDriverManager
from webdriver_manager.microsoft import EdgeChromiumDriverManager


def demo_chrome_with_manager():
    """
    Chrome setup with webdriver-manager.
    Driver is automatically downloaded and cached.
    """
    print("=" * 60)
    print("DEMO: Chrome with webdriver-manager")
    print("=" * 60)
    
    # webdriver-manager handles everything:
    # 1. Detects Chrome version
    # 2. Downloads matching ChromeDriver
    # 3. Caches it for future use
    # 4. Returns the path to the driver
    
    service = ChromeService(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)
    
    try:
        driver.get("https://www.google.com")
        print(f"✓ Chrome launched successfully!")
        print(f"  Browser: Chrome")
        print(f"  Page Title: {driver.title}")
        print(f"  URL: {driver.current_url}")
    finally:
        driver.quit()
    
    print("✓ Chrome demo completed\n")


def demo_firefox_with_manager():
    """
    Firefox setup with webdriver-manager.
    Uses GeckoDriver for Firefox automation.
    """
    print("=" * 60)
    print("DEMO: Firefox with webdriver-manager")
    print("=" * 60)
    
    service = FirefoxService(GeckoDriverManager().install())
    driver = webdriver.Firefox(service=service)
    
    try:
        driver.get("https://www.mozilla.org")
        print(f"✓ Firefox launched successfully!")
        print(f"  Browser: Firefox")
        print(f"  Page Title: {driver.title}")
        print(f"  URL: {driver.current_url}")
    finally:
        driver.quit()
    
    print("✓ Firefox demo completed\n")


def demo_edge_with_manager():
    """
    Microsoft Edge setup with webdriver-manager.
    Uses EdgeChromiumDriverManager for modern Edge.
    """
    print("=" * 60)
    print("DEMO: Edge with webdriver-manager")
    print("=" * 60)
    
    service = EdgeService(EdgeChromiumDriverManager().install())
    driver = webdriver.Edge(service=service)
    
    try:
        driver.get("https://www.microsoft.com")
        print(f"✓ Edge launched successfully!")
        print(f"  Browser: Microsoft Edge")
        print(f"  Page Title: {driver.title}")
        print(f"  URL: {driver.current_url}")
    finally:
        driver.quit()
    
    print("✓ Edge demo completed\n")


# =============================================================================
# PART 3: CROSS-BROWSER TESTING UTILITY
# =============================================================================
def get_driver(browser_name: str):
    """
    Factory function to get WebDriver for any supported browser.
    
    Args:
        browser_name: One of 'chrome', 'firefox', 'edge'
        
    Returns:
        WebDriver instance for the specified browser
        
    Raises:
        ValueError: If browser_name is not supported
    """
    browser_name = browser_name.lower()
    
    if browser_name == "chrome":
        service = ChromeService(ChromeDriverManager().install())
        return webdriver.Chrome(service=service)
    
    elif browser_name == "firefox":
        service = FirefoxService(GeckoDriverManager().install())
        return webdriver.Firefox(service=service)
    
    elif browser_name == "edge":
        service = EdgeService(EdgeChromiumDriverManager().install())
        return webdriver.Edge(service=service)
    
    else:
        raise ValueError(f"Unsupported browser: {browser_name}. "
                        f"Supported browsers: chrome, firefox, edge")


def demo_cross_browser_testing():
    """
    Demonstrate running the same test across multiple browsers.
    This pattern is essential for cross-browser compatibility testing.
    """
    print("=" * 60)
    print("DEMO: Cross-Browser Testing Pattern")
    print("=" * 60)
    
    # Same test runs on multiple browsers
    test_url = "https://www.selenium.dev/"
    browsers_to_test = ["chrome"]  # Add "firefox", "edge" if installed
    
    results = {}
    
    for browser in browsers_to_test:
        print(f"\n  Testing on {browser.upper()}...")
        
        try:
            driver = get_driver(browser)
            driver.get(test_url)
            
            results[browser] = {
                "status": "PASS",
                "title": driver.title,
                "url": driver.current_url
            }
            
            print(f"    ✓ {browser}: Page loaded successfully")
            
        except Exception as e:
            results[browser] = {
                "status": "FAIL",
                "error": str(e)
            }
            print(f"    ✗ {browser}: {e}")
            
        finally:
            if 'driver' in locals():
                driver.quit()
    
    # Summary
    print("\n  Cross-Browser Test Results:")
    print("  " + "-" * 40)
    for browser, result in results.items():
        status = result["status"]
        print(f"    {browser.capitalize()}: {status}")
    
    print("\n✓ Cross-browser demo completed")


# =============================================================================
# PART 4: DRIVER CACHING EXPLAINED
# =============================================================================
def demo_driver_caching():
    """
    Demonstrate that webdriver-manager caches drivers.
    Second run is much faster because driver is already downloaded.
    """
    print("\n" + "=" * 60)
    print("DEMO: Driver Caching")
    print("=" * 60)
    
    import time
    
    print("\n  First driver initialization (may download):")
    start = time.time()
    service1 = ChromeService(ChromeDriverManager().install())
    end = time.time()
    print(f"    Time: {end - start:.2f} seconds")
    
    print("\n  Second driver initialization (cached):")
    start = time.time()
    service2 = ChromeService(ChromeDriverManager().install())
    end = time.time()
    print(f"    Time: {end - start:.2f} seconds")
    
    print("\n  ℹ️  Drivers are cached in:")
    print("     ~/.wdm/drivers/ (Linux/Mac)")
    print("     C:\\Users\\<user>\\.wdm\\drivers\\ (Windows)")
    
    print("\n✓ Caching demo completed")


# =============================================================================
# MAIN EXECUTION
# =============================================================================
if __name__ == "__main__":
    print("\n🔧 DRIVER MANAGEMENT DEMO 🔧\n")
    print("This demo shows automated WebDriver management with webdriver-manager")
    print("-" * 60)
    
    # Run demos
    demo_chrome_with_manager()
    
    # Uncomment to test Firefox and Edge (if installed):
    # demo_firefox_with_manager()
    # demo_edge_with_manager()
    
    demo_cross_browser_testing()
    demo_driver_caching()
    
    print("\n" + "=" * 60)
    print("DRIVER MANAGEMENT DEMOS COMPLETED! 🎉")
    print("=" * 60)

