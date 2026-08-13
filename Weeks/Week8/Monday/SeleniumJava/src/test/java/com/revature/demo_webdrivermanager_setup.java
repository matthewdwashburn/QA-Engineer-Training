package com.revature;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demo: WebDriverManager - Automated Driver Setup
 *
 * 1. No more manual driver downloads!
 * 2. Automatic browser version detection
 * 3. Automatic driver caching
 * 4. Supports all major browsers
 *
 * DEPENDENCY (pom.xml):
 * <dependency>
 *     <groupId>io.github.bonigarcia</groupId>
 *     <artifactId>webdrivermanager</artifactId>
 *     <version>6.3.4</version>
 * </dependency>
 */
@DisplayName("WebDriverManager Setup Demo")
public class demo_webdrivermanager_setup {

    private WebDriver driver;

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    // ==========================================================
    // SECTION 1: Basic Setup
    // ==========================================================

    @Test
    @DisplayName("Chrome - One line setup")
    void chromeSetup_oneLine() {
        /*

         * Compare to manual setup:
         * - No downloading drivers
         * - No version matching
         * - No system properties
         */

        // One line does everything!
        WebDriverManager.chromedriver().setup();

        driver = new ChromeDriver();
        driver.get("https://www.selenium.dev");

        assertTrue(driver.getTitle().contains("Selenium"));
        System.out.println("Chrome setup with WebDriverManager - SUCCESS!");
    }

//    @Test
//    @DisplayName("Firefox - One line setup")
//    void firefoxSetup_oneLine() {
//        WebDriverManager.firefoxdriver().setup();
//
//        driver = new FirefoxDriver();
//        driver.get("https://www.selenium.dev");
//
//        assertTrue(driver.getTitle().contains("Selenium"));
//        System.out.println("Firefox setup - SUCCESS!");
//    }

    @Test
    @DisplayName("Edge - One line setup")
    void edgeSetup_oneLine() {
        WebDriverManager.edgedriver().setup();

        driver = new EdgeDriver();
        driver.get("https://www.selenium.dev");

        assertTrue(driver.getTitle().contains("Selenium"));
        System.out.println("Edge setup - SUCCESS!");
    }

    // ==========================================================
    // SECTION 2: Advanced Configuration
    // ==========================================================

    @Test
    @DisplayName("Specific driver version")
    void specificVersion() {
        /*
         * Sometimes you need a specific driver version
         */

        WebDriverManager.chromedriver()
                .driverVersion("147.0.7727.138")  // Specific version
                .setup();

        driver = new ChromeDriver();
        driver.get("https://www.selenium.dev");

        System.out.println("Using specific driver version");
    }

    @Test
    @DisplayName("Cache configuration")
    void cacheConfiguration() {
        /*
         * WebDriverManager caches drivers by default
         * Can configure cache location and duration
         */

        WebDriverManager.chromedriver()
                .cachePath("./drivers-cache")  // Custom cache location
                .setup();

        driver = new ChromeDriver();
        driver.get("https://www.selenium.dev");

        System.out.println("Custom cache path configured");
    }

    // ==========================================================
    // SECTION 3: Cross-Browser Pattern
    // ==========================================================

    @Test
    @DisplayName("Cross-browser with WebDriverManager")
    void crossBrowser() {
        String browser = System.getProperty("browser", "chrome");

        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
        }

        driver.get("https://www.selenium.dev");
        System.out.println("Running on: " + browser);
    }

    // ==========================================================
    // SECTION 4: Selenium 4+ Native Manager
    // ==========================================================

    @Test
    @DisplayName("Selenium 4.6+ built-in driver management")
    void selenium4BuiltIn() {
        /*
         * Selenium 4.6+ has built-in driver management!
         * Just create driver - it downloads automatically
         */

        // Selenium 4.6+ - just create the driver!
        driver = new ChromeDriver();

        driver.get("https://www.selenium.dev");
        assertTrue(driver.getTitle().contains("Selenium"));

        System.out.println("Selenium 4.6+ native driver management!");
    }
}


