package com.revature.cuc.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginValidInvalidDataSteps {

    private WebDriver driver;
    private static final String LOGIN_URL = "https://the-internet.herokuapp.com/login";

    //---------Hooks----------------

    @Before
    public void setUp(){
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @After
    public void tearDown() {
        if(driver!= null){
            driver.quit();
        }
    }

    //---------------background Steps---------------------

    @Given("the application is running")
    public void the_application_is_running() {

        driver.get(LOGIN_URL);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement usernameField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("username"))
        );

        assertTrue(usernameField.isDisplayed());
    }
    @And("the test database is seeded with users")
    public void the_test_database_is_seeded_with_users() {
        // No-op for demo application
        // Exists for readability and future extensibility
    }

    //---------------------Scenario Steps --------------------------
    @Given("the user is on the login page")
    public void the_user_is_on_the_login_page() {
        driver.get(LOGIN_URL);
    }
    @When("the user enters username {string}")
    public void the_user_enters_username(String username) {
        // Write code here that turns the phrase above into concrete actions
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys(username);
    }
    @And("the user enters password {string}")
    public void the_user_enters_password(String password) {
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(password);
    }
    @And("the user clicks the login button")
    public void the_user_clicks_the_login_button() {
        // Write code here that turns the phrase above into concrete actions
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    //---------------------------Outcome Validation-------------------------
    @Then("the {string} should be displayed")
    public void the_should_be_displayed(String expectedResult) {

        String flashMessage =
                driver.findElement(By.id("flash")).getText();

        switch (expectedResult.toLowerCase()) {
            case "success message":
                assertTrue(
                        flashMessage.contains("You logged into a secure area")
                );
                break;
            case "error message":
                assertTrue(flashMessage.contains("Your username is invalid")
                        || flashMessage.contains("Your password is invalid"));
                break;
            default:
                throw new IllegalArgumentException(
                        "Unknown expected_result: " + expectedResult
                );
        }
    }
    @And("the user should be on the {string} page")
    public void the_user_should_be_on_the_page(String expectedPage) {
        switch (expectedPage.toLowerCase()){
            case "secure":
                assertTrue(driver.getCurrentUrl().contains("/secure"));
                break;
            case "login":
                assertTrue(driver.getCurrentUrl().contains("/login"));
                break;
            default:
                throw new IllegalArgumentException("Uknown expected_page: " + expectedPage);
        }
    }

}
