package cuc.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ManagerReviewLifecycle {

    // Through the frontend's reverse proxy, so the suite needs one URL, not three.
    private static final String EMPLOYEE_API =
            TestContext.WELCOME_URL.replaceAll("/$", "") + "/api/employee";

    private final TestContext context;

    public ManagerReviewLifecycle(TestContext context) {
        this.context = context;
    }

    private WebDriver driver() {
        return context.getDriver();
    }

    private WebElement waitForRow(String description) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[td[text()='" + description + "']]")));
    }

    // ---------------background Steps---------------------

    @When("I log in as a manager with username {string} and password {string}")
    public void i_log_in_as_a_manager_with_username_and_password(String username, String password) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
        // Wait rather than findElement — this step is reached both from a fresh page
        // load and from a post-logout navigation, where React may not have rendered yet.
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='manager-login-btn']"))).click();

        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='username-text-field']")));
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='password-text-field']")));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);

        driver().findElement(By.cssSelector("[data-testid='login-btn']")).click();
    }

    @Then("I should be on the manager dashboard")
    public void i_should_be_on_the_manager_dashboard() {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlToBe(TestContext.WELCOME_URL + "manager/dashboard"));
    }

    @When("I navigate directly to the manager dashboard")
    public void i_navigate_directly_to_the_manager_dashboard() {
        driver().get(TestContext.WELCOME_URL + "manager/dashboard");
    }

    // ---------------pending queue / review Steps---------------------

    @Given("an employee has a pending expense {string} for {string} in category {string}")
    public void an_employee_has_a_pending_expense_for_in_category(String description, String amount, String category)
            throws IOException, InterruptedException {
        // Seeded directly through the employee API rather than the UI — this scenario
        // is only testing manager behavior, and the manager's browser session (already
        // logged in per Background) shouldn't be disturbed by a second login.
        HttpClient client = HttpClient.newBuilder().cookieHandler(new CookieManager()).build();

        HttpRequest login = HttpRequest.newBuilder()
                .uri(URI.create(EMPLOYEE_API + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "{\"username\":\"brian\",\"password\":\"password\"}"))
                .build();
        client.send(login, HttpResponse.BodyHandlers.discarding());

        String categoryKey = category.toUpperCase().replace(" ", "_");
        String submitBody = String.format(
                "{\"description\":\"%s\",\"amount\":\"%s\",\"category\":\"%s\",\"expense_date\":\"2026-07-28\"}",
                description, amount, categoryKey);
        HttpRequest submit = HttpRequest.newBuilder()
                .uri(URI.create(EMPLOYEE_API + "/expenses/submit"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(submitBody))
                .build();
        client.send(submit, HttpResponse.BodyHandlers.discarding());
    }

    @When("I view the pending expense queue")
    public void i_view_the_pending_expense_queue() {
        driver().get(TestContext.WELCOME_URL + "manager/expense-review");
    }

    @Then("I should see the expense {string} with amount {string} in the pending queue")
    public void i_should_see_the_expense_with_amount_in_the_pending_queue(String description, String amount) {
        WebElement row = waitForRow(description);
        List<WebElement> cells = row.findElements(By.tagName("td"));
        // Compare numerically — the manager backend doesn't format to a fixed 2
        // decimal places the way the employee backend does, so "450.00" vs "450"
        // is not a real mismatch.
        assertEquals(Double.parseDouble(amount), Double.parseDouble(cells.get(2).getText()), 0.001); // employeeId, description, amount, category, date
    }

    @When("I review the expense {string} as {string} with comment {string}")
    public void i_review_the_expense_as_with_comment(String description, String decision, String comment) {
        WebElement row = waitForRow(description);
        WebElement reviewButton = row.findElement(By.cssSelector("[data-testid^='review-btn-']"));
        reviewButton.click();

        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='review-status-input']"))).click();

        String decisionLabel = decision.substring(0, 1).toUpperCase() + decision.substring(1);
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(@class,'dropdown-item') and normalize-space()='" + decisionLabel + "']"))).click();

        driver().findElement(By.cssSelector("[data-testid='review-comment-input']")).sendKeys(comment);
        driver().findElement(By.cssSelector("[data-testid='confirm-review-btn']")).click();
    }

    @Then("I should see a confirmation that the review was submitted")
    public void i_should_see_a_confirmation_that_the_review_was_submitted() {
        By locator = By.cssSelector("[data-testid='review-response-label']");
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, "Expense reviewed successfully"));
        assertEquals("Expense reviewed successfully", driver().findElement(locator).getText());
    }

    @Then("the expense {string} should no longer appear in the pending queue")
    public void the_expense_should_no_longer_appear_in_the_pending_queue(String description) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
        wait.withMessage("row for \"" + description + "\" to disappear from the pending queue");
        wait.until(d -> d.findElements(By.xpath("//tr[td[text()='" + description + "']]")).isEmpty());
    }

}
