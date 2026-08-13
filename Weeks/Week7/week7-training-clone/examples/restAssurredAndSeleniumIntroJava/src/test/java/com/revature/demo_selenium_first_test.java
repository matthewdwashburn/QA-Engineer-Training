package com.revature;

//These are the simplest Selenium Tests
// WebDriver is the main interface for the browser interaction
// Always close the driver when done (finally block @AfterEach)
//get() navigates to URL, getTitle() gets the page title...


import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("First Selenium WebDriver Tests")
public class demo_selenium_first_test {

    private WebDriver driver;

    @BeforeEach
    void setUp() {

        //for now, we'll use manual driver setup
        //tomorrow we will see WebDriverManager for automatic setup

        //ChromeDriver requires:
        //Chrome browser installed
        //ChromeDriver executable (matching Chrome version)
        //System property set OR ChomeDriver in PATH

        //WebDriverManager.chromedriver.setup()
        //could need above

        driver = new ChromeDriver();

        //Maximize browser window()
        driver.manage().window().maximize();
    }

    @AfterEach
    void tearDown() {
        //alwaus close the driver!
        //quit() closes all windows and ends the session
        //close() only closes current window()
        //forgetting to quit() leaves browser processes running
        if(driver != null){
            driver.quit();
        }
    }

    //Basic Navigation
    @Test
    @DisplayName("Navigate to website and verify title")
    void navigateToWebsite_verifyTitle(){
        //1. open a URL
        //2. get the page title
        //3. assert it matches expected

        //navigate to the website
        driver.get("https://www.selenium.dev/");

        // get page title
        String title = driver.getTitle();
        System.out.println("Page Title: " + title);

        //Verify title
        assertTrue(title.contains("Selenium"),
                "Title shoudl contain 'Selenium'");
    }

    @Test
    @DisplayName("Get current URL after navigation")
    void navigateToWebsite_verifyUrl(){
        //getCurrentUrl() returns the current page URL
        //Useful for verifyng redirects or navigation

        driver.get("https://www.selenium.dev/documentation");

        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL: " + currentUrl);

        assertTrue(currentUrl.contains("documentation"),
                "URL should contain 'documentation'");
    }

    @Test
    @DisplayName("Get page source")
    void getPageSource_containsExpectedContent() {
        /*
         * getPageSource() returns raw HTML
         * Useful for debugging or content verification
         */

        driver.get("https://www.selenium.dev/");

        String pageSource = driver.getPageSource();

        // Verify page contains expected content
        assertTrue(pageSource.contains("Selenium"),
                "Page source should contain 'Selenium'");

        // Print first 500 characters for demo
        System.out.println("Page source (first 500 chars):");
        System.out.println(pageSource.substring(0, Math.min(500, pageSource.length())));
    }

    // ==========================================================
    // SECTION 2: Basic WebDriver Properties
    // ==========================================================

    @Test
    @DisplayName("WebDriver window handle")
    void getWindowHandle_returnsUniqueId() {
        /*
         * INSTRUCTOR NOTE:
         * Window handle is a unique ID for each browser window
         * Used for switching between multiple windows/tabs
         */

        driver.get("https://www.selenium.dev/");

        String windowHandle = driver.getWindowHandle();
        System.out.println("Window handle: " + windowHandle);

        assertNotNull(windowHandle, "Window handle should not be null");
        assertFalse(windowHandle.isEmpty(), "Window handle should not be empty");
    }

    // ==========================================================
    // SECTION 3: Test Lifecycle Demo
    // ==========================================================

    @Test
    @DisplayName("Multiple navigations in single test")
    void multipleNavigations_workCorrectly() {
        /*
         * Show that we can navigate to multiple pages in one test
         * Each test gets a fresh browser instance (@BeforeEach)
         */

        // First navigation
        driver.get("https://www.selenium.dev/");
        assertEquals("Selenium", driver.getTitle().split(" ")[0]);

        // Second navigation
        driver.get("https://www.google.com");
        assertTrue(driver.getTitle().toLowerCase().contains("google"));

        // Third navigation
        driver.get("https://www.selenium.dev/documentation/");
        assertTrue(driver.getCurrentUrl().contains("documentation"));
    }

    // ==========================================================
    // SECTION 4: Common Mistakes Demo
    // ==========================================================

    @Test
    @DisplayName("Demo: What happens without driver.quit()")
    @Disabled("Intentionally disabled - demonstrates bad practice")
    void withoutQuit_browserStaysOpen() {
        /*
         * If we don't call quit(), the browser stays open!
         * This leads to:
         * - Memory leaks
         * - Port exhaustion
         * - Zombie browser processes
         *
         * NEVER do this in real tests!
         */

        WebDriver tempDriver = new ChromeDriver();
        tempDriver.get("https://www.selenium.dev/");

        // Forgetting to close - BAD!
        // tempDriver.quit();
    }

    // ==========================================================
    // SECTION 5: Using Practice Website
    // ==========================================================

    @Test
    @DisplayName("Navigate to practice website - The Internet")
    void practiceWebsite_theInternet() {
        /*
         * The Internet (Heroku) is excellent for Selenium practice
         * It has examples of many UI patterns
         */

        driver.get("https://the-internet.herokuapp.com/");

        String title = driver.getTitle();
        System.out.println("Practice site title: " + title);

        assertTrue(title.contains("Internet"),
                "Should be on The Internet practice site");
    }

    @Test
    @DisplayName("Navigate to practice website - DemoQA")
    void practiceWebsite_demoQA() {
        /*
         * DemoQA has forms, widgets, and interactive elements
         */

        driver.get("https://demoqa.com/");

        String currentUrl = driver.getCurrentUrl();
        System.out.println("DemoQA URL: " + currentUrl);

        assertTrue(currentUrl.contains("demoqa"),
                "Should be on DemoQA practice site");
    }

}
