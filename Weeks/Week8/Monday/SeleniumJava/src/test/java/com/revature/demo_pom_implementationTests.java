package com.revature;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Demo: Page Object Model Implementation
 *
 * 1. Page Objects encapsulate page structure
 * 2. Each page is a separate class
 * 3. Locators are private, methods are public
 * 4. Tests use page methods, not raw locators
 */
@DisplayName("Page Object Model Demo")
public class demo_pom_implementationTests {

    private WebDriver driver;
    private static final String BASE_URL = "https://the-internet.herokuapp.com";

    // ==========================================================
    // PAGE OBJECTS (normally in separate files)
    // ==========================================================

    /**
     * Page Object for Login Page
     */
    class LoginPage {
        private WebDriver driver;
        private WebDriverWait wait;

        // Locators - private
        private By usernameField = By.id("username");
        private By passwordField = By.id("password");
        private By loginButton = By.xpath("//button[@type='submit']");
        private By flashMessage = By.id("flash");

        // Constructor
        public LoginPage(WebDriver driver) {
            this.driver = driver;
            this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        }

        // Action methods - public
        public LoginPage enterUsername(String username) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
            driver.findElement(usernameField).clear();
            driver.findElement(usernameField).sendKeys(username);
            return this; // Fluent pattern
        }

        public LoginPage enterPassword(String password) {
            driver.findElement(passwordField).clear();
            driver.findElement(passwordField).sendKeys(password);
            return this;
        }

        public SecurePage clickLogin() {
            driver.findElement(loginButton).click();
            return new SecurePage(driver);
        }

        public LoginPage clickLoginExpectingError() {
            driver.findElement(loginButton).click();
            return this;
        }

        // Verification methods
        public String getErrorMessage() {
            wait.until(ExpectedConditions.visibilityOfElementLocated(flashMessage));
            return driver.findElement(flashMessage).getText();
        }

        public boolean isUsernameFieldDisplayed() {
            return driver.findElement(usernameField).isDisplayed();
        }

        // Compound action
        public SecurePage loginAs(String username, String password) {
            return enterUsername(username)
                    .enterPassword(password)
                    .clickLogin();
        }
    }

    /**
     * Page Object for Secure Area (after login)
     */
    class SecurePage {
        private WebDriver driver;
        private WebDriverWait wait;

        private By flashMessage = By.id("flash");
        private By logoutButton = By.xpath("//a[contains(@href, 'logout')]");
        private By heading = By.tagName("h2");

        public SecurePage(WebDriver driver) {
            this.driver = driver;
            this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        }

        public String getFlashMessage() {
            wait.until(ExpectedConditions.visibilityOfElementLocated(flashMessage));
            return driver.findElement(flashMessage).getText();
        }

        public String getHeading() {
            return driver.findElement(heading).getText();
        }

        public LoginPage clickLogout() {
            driver.findElement(logoutButton).click();
            return new LoginPage(driver);
        }

        public boolean isSecureAreaDisplayed() {
            try {
                return driver.findElement(heading).getText().contains("Secure Area");
            } catch (NoSuchElementException e) {
                return false;
            }
        }
    }

    // ==========================================================
    // TEST SETUP
    // ==========================================================

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(BASE_URL + "/login");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    // ==========================================================
    // TESTS USING PAGE OBJECTS
    // ==========================================================

    @Test
    @DisplayName("Valid login using POM")
    void validLogin_usingPOM() {
        LoginPage loginPage = new LoginPage(driver);

        // Fluent style usage
        SecurePage securePage = loginPage
                .enterUsername("tomsmith")
                .enterPassword("SuperSecretPassword!")
                .clickLogin();

        assertTrue(securePage.isSecureAreaDisplayed());
        assertTrue(securePage.getFlashMessage().contains("logged into"));
    }

    @Test
    @DisplayName("Valid login using compound method")
    void validLogin_compoundMethod() {
        LoginPage loginPage = new LoginPage(driver);

        // Single method call
        SecurePage securePage = loginPage.loginAs("tomsmith", "SuperSecretPassword!");

        assertTrue(securePage.isSecureAreaDisplayed());
    }

    @Test
    @DisplayName("Invalid login shows error")
    void invalidLogin_showsError() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage
                .enterUsername("invalid")
                .enterPassword("invalid")
                .clickLoginExpectingError();

        String errorMessage = loginPage.getErrorMessage();
        assertTrue(errorMessage.contains("invalid"));
    }

    @Test
    @DisplayName("Login and logout flow")
    void loginLogout_flow() {
        LoginPage loginPage = new LoginPage(driver);

        // Login
        SecurePage securePage = loginPage.loginAs("tomsmith", "SuperSecretPassword!");
        assertTrue(securePage.isSecureAreaDisplayed());

        // Logout
        LoginPage loginPageAfterLogout = securePage.clickLogout();
        assertTrue(loginPageAfterLogout.isUsernameFieldDisplayed());
    }

    @Test
    @DisplayName("Demonstrate POM benefits")
    void demonstratePOMBenefits() {
        /*
         * Notice how clean the test is:
         * - No locators visible
         * - No WebDriver calls
         * - Intent is clear
         *
         * If locator changes, update ONE place (page object)
         */

        LoginPage loginPage = new LoginPage(driver);

        // Test reads like documentation
        SecurePage securePage = loginPage.loginAs("tomsmith", "SuperSecretPassword!");

        assertEquals("Secure Area", securePage.getHeading());
        assertTrue(securePage.getFlashMessage().contains("secure area"));

        System.out.println("POM makes tests maintainable and readable!");
    }
}


