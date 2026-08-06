package com.revature;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Demo: Page Factory Pattern
 *
  * 1. @FindBy annotations declare elements
 * 2. PageFactory.initElements() initializes them
 * 3. @CacheLookup caches element for reuse
 * 4. Cleaner syntax than traditional POM
 */
@DisplayName("Page Factory Demo")
public class demo_page_factoryTests {

    private WebDriver driver;
    private static final String BASE_URL = "https://the-internet.herokuapp.com";

    // ==========================================================
    // PAGE OBJECTS WITH PAGE FACTORY
    // ==========================================================

    /**
     * Login Page using Page Factory
     */
    class LoginPageFactory {
        private WebDriver driver;

        // @FindBy declares element locators
        @FindBy(id = "username")
        private WebElement usernameField;

        @FindBy(id = "password")
        private WebElement passwordField;

        @FindBy(xpath = "//button[@type='submit']")
        @CacheLookup  // Cache element after first find
        private WebElement loginButton;

        @FindBy(id = "flash")
        private WebElement flashMessage;

        @FindBy(css = ".subheader")
        private WebElement subheader;

        // Multiple element find
        @FindBy(tagName = "input")
        private java.util.List<WebElement> allInputs;

        // Constructor must call initElements
        public LoginPageFactory(WebDriver driver) {
            this.driver = driver;
            PageFactory.initElements(driver, this);
        }

        // Action methods use elements directly
        public LoginPageFactory enterUsername(String username) {
            usernameField.clear();
            usernameField.sendKeys(username);
            return this;
        }

        public LoginPageFactory enterPassword(String password) {
            passwordField.clear();
            passwordField.sendKeys(password);
            return this;
        }

        public SecurePageFactory clickLogin() {
            loginButton.click();
            return new SecurePageFactory(driver);
        }

        public String getFlashMessage() {
            return flashMessage.getText();
        }

        public String getSubheader() {
            return subheader.getText();
        }

        public int getInputCount() {
            return allInputs.size();
        }

        public SecurePageFactory loginAs(String username, String password) {
            return enterUsername(username)
                    .enterPassword(password)
                    .clickLogin();
        }
    }

    /**
     * Secure Page using Page Factory
     */
    class SecurePageFactory {
        private WebDriver driver;

        @FindBy(id = "flash")
        private WebElement flashMessage;

        @FindBy(css = "a[href*='logout']")
        @CacheLookup
        private WebElement logoutButton;

        @FindBy(tagName = "h2")
        private WebElement heading;

        public SecurePageFactory(WebDriver driver) {
            this.driver = driver;
            PageFactory.initElements(driver, this);
        }

        public String getFlashMessage() {
            return flashMessage.getText();
        }

        public String getHeading() {
            return heading.getText();
        }

        public boolean isSecureAreaDisplayed() {
            try {
                return heading.getText().contains("Secure Area");
            } catch (NoSuchElementException e) {
                return false;
            }
        }

        public LoginPageFactory clickLogout() {
            logoutButton.click();
            return new LoginPageFactory(driver);
        }
    }

    // ==========================================================
    // DIFFERENT @FindBy LOCATOR TYPES
    // ==========================================================

    class AllLocatorTypesExample {

        @FindBy(id = "elementId")
        private WebElement byId;

        @FindBy(name = "elementName")
        private WebElement byName;

        @FindBy(className = "elementClass")
        private WebElement byClassName;

        @FindBy(css = "#id .class tag")
        private WebElement byCssSelector;

        @FindBy(xpath = "//div[@id='test']")
        private WebElement byXpath;

        @FindBy(linkText = "Click Here")
        private WebElement byLinkText;

        @FindBy(partialLinkText = "Click")
        private WebElement byPartialLinkText;

        @FindBy(tagName = "input")
        private java.util.List<WebElement> byTagName;

        // Using @FindBys (AND condition)
        @FindBys({
                @FindBy(className = "form"),
                @FindBy(tagName = "input")
        })
        private WebElement byChainedCondition;

        // Using @FindAll (OR condition)
        @FindAll({
                @FindBy(id = "username"),
                @FindBy(id = "password")
        })
        private java.util.List<WebElement> byOrCondition;

        public AllLocatorTypesExample(WebDriver driver) {
            PageFactory.initElements(driver, this);
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
    // TESTS USING PAGE FACTORY
    // ==========================================================

    @Test
    @DisplayName("Valid login using Page Factory")
    void validLogin_pageFactory() {
        LoginPageFactory loginPage = new LoginPageFactory(driver);

        SecurePageFactory securePage = loginPage
                .enterUsername("tomsmith")
                .enterPassword("SuperSecretPassword!")
                .clickLogin();

        assertTrue(securePage.isSecureAreaDisplayed());
        assertTrue(securePage.getFlashMessage().contains("logged into"));
    }

    @Test
    @DisplayName("Page Factory vs Traditional POM")
    void pageFactoryVsTraditional() {
        /*
         * INSTRUCTOR NOTE:
         *
         * Traditional POM:
         * private By username = By.id("username");
         * driver.findElement(username).sendKeys(text);
         *
         * Page Factory:
         * @FindBy(id = "username")
         * private WebElement username;
         * username.sendKeys(text);  // Direct use!
         */

        LoginPageFactory loginPage = new LoginPageFactory(driver);

        // Elements used directly without findElement calls
        loginPage.enterUsername("tomsmith")
                .enterPassword("SuperSecretPassword!");

        System.out.println("Page Factory is cleaner!");
    }

    @Test
    @DisplayName("Using @CacheLookup benefit")
    void cacheLookupBenefit() {
        /*
         * INSTRUCTOR NOTE:
         * @CacheLookup caches element after first lookup
         * Good for static elements that don't change
         * Bad for dynamic elements (can cause StaleElementReferenceException)
         */

        LoginPageFactory loginPage = new LoginPageFactory(driver);

        // loginButton is cached after first use
        // Subsequent accesses don't re-query DOM
        loginPage.enterUsername("tomsmith")
                .enterPassword("SuperSecretPassword!")
                .clickLogin();

        System.out.println("@CacheLookup improves performance for static elements");
    }

    @Test
    @DisplayName("Finding multiple elements")
    void findingMultipleElements() {
        LoginPageFactory loginPage = new LoginPageFactory(driver);

        int inputCount = loginPage.getInputCount();
        System.out.println("Input fields found: " + inputCount);

        assertTrue(inputCount >= 2); // username and password
    }

    @Test
    @DisplayName("Complete login flow with Page Factory")
    void completeLoginFlow() {
        LoginPageFactory loginPage = new LoginPageFactory(driver);

        // Verify page loaded
//        assertTrue(loginPage.getSubheader().contains("login"));
        assertTrue(loginPage.getSubheader().contains("log into"));

        // Login
        SecurePageFactory securePage = loginPage.loginAs("tomsmith", "SuperSecretPassword!");

        // Verify secure area
        assertEquals("Secure Area", securePage.getHeading());

        // Logout
        LoginPageFactory loginAgain = securePage.clickLogout();

        // Verify back on login page
        assertTrue(loginAgain.getSubheader().contains("login"));

        System.out.println("Complete flow using Page Factory!");
    }
}