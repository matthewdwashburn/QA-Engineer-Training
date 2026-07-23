package com.revature;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Demo: Navigation Methods in Selenium
 *
 * 1. navigate() provides browser navigation controls
 * 2. back(), forward(), refresh() like browser buttons
 * 3. navigate().to() vs get() differences
 * 4. URL verification after navigation
 */
@DisplayName("Navigation Methods Demo")
public class demo_navigation_methodsTests {

    private WebDriver driver;
    private static final String BASE_URL = "https://the-internet.herokuapp.com";

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    // ==========================================================
    // SECTION 1: Basic Navigation
    // ==========================================================

    @Test
    @DisplayName("navigate().to() - Navigate to URL")
    void navigateTo() {
        // navigate().to() is equivalent to get()
        driver.navigate().to(BASE_URL);
        assertTrue(driver.getCurrentUrl().contains("the-internet"));

        driver.navigate().to(BASE_URL + "/login");
        assertTrue(driver.getCurrentUrl().contains("login"));
    }

    @Test
    @DisplayName("get() vs navigate().to()")
    void getVsNavigateTo() {
        /*
         * get() and navigate().to() are nearly identical
         * get() blocks until page loads
         * navigate().to() returns immediately (browser handles loading)
         * In practice, both wait for page load
         */

        // Using get()
        driver.get(BASE_URL);
        String url1 = driver.getCurrentUrl();

        // Using navigate().to()
        driver.navigate().to(BASE_URL + "/login");
        String url2 = driver.getCurrentUrl();

        assertNotEquals(url1, url2);
        System.out.println("Both methods navigate successfully");
    }

    // ==========================================================
    // SECTION 2: Browser History Navigation
    // ==========================================================

    @Test
    @DisplayName("back() and forward()")
    void backAndForward() {
        // Navigate to first page
        driver.get(BASE_URL);
        String page1 = driver.getCurrentUrl();
        System.out.println("Page 1: " + page1);

        // Navigate to second page
        driver.get(BASE_URL + "/login");
        String page2 = driver.getCurrentUrl();
        System.out.println("Page 2: " + page2);

        // Navigate to third page
        driver.get(BASE_URL + "/checkboxes");
        String page3 = driver.getCurrentUrl();
        System.out.println("Page 3: " + page3);

        // Go back
        driver.navigate().back();
        assertEquals(page2, driver.getCurrentUrl());
        System.out.println("After back: " + driver.getCurrentUrl());

        // Go back again
        driver.navigate().back();
        assertEquals(page1, driver.getCurrentUrl());
        System.out.println("After back again: " + driver.getCurrentUrl());

        // Go forward
        driver.navigate().forward();
        assertEquals(page2, driver.getCurrentUrl());
        System.out.println("After forward: " + driver.getCurrentUrl());
    }

    @Test
    @DisplayName("refresh() - Reload page")
    void refresh() {
        driver.get(BASE_URL + "/dynamic_content");

        String contentBefore = driver.getPageSource();

        // Refresh the page
        driver.navigate().refresh();

        String contentAfter = driver.getPageSource();

        // Dynamic content page changes on refresh
        System.out.println("Page refreshed!");
    }

    // ==========================================================
    // SECTION 3: URL Verification
    // ==========================================================

    @Test
    @DisplayName("getCurrentUrl() - Verify navigation")
    void getCurrentUrl() {
        driver.get(BASE_URL + "/login");

        String currentUrl = driver.getCurrentUrl();

        assertTrue(currentUrl.startsWith("https://"));
        assertTrue(currentUrl.contains("the-internet"));
        assertTrue(currentUrl.endsWith("/login"));

        System.out.println("URL verified: " + currentUrl);
    }

}


