package cuc.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class EmployeeExpenseLifecycle {

    private final TestContext context;

    public EmployeeExpenseLifecycle(TestContext context) {
        this.context = context;
    }

    private WebDriver driver() {
        return context.getDriver();
    }

    private void waitForNoRow(String description) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
        wait.withMessage("row for \"" + description + "\" to disappear from the table");
        wait.until(d -> d.findElements(By.xpath("//tr[td[text()='" + description + "']]")).isEmpty());
    }

    private WebElement waitForRow(String description) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[td[text()='" + description + "']]")));
    }

    // ---------------background Steps---------------------

    @Given("I am on the welcome page")
    public void i_am_on_the_welcome_page() {
        driver().get(TestContext.WELCOME_URL);
    }

    @When("I log in as an employee with username {string} and password {string}")
    public void i_log_in_as_an_employee_with_username_and_password(String username, String password) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
        // Wait rather than findElement — this step is reached both from a fresh page
        // load and from a post-logout navigation, where React may not have rendered yet.
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='employee-login-btn']"))).click();

        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='username-text-field']")));
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='password-text-field']")));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);

        driver().findElement(By.cssSelector("[data-testid='login-btn']")).click();

    }

    @Then("I should be on the employee dashboard")
    public void i_should_be_on_the_employee_dashboard() {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlToBe(TestContext.WELCOME_URL + "employee/dashboard"));
    }

    @When("I log out")
    public void i_log_out() {
        // Not clicking the logout-btn here on purpose: this step gets reused from
        // multiple pages (cross-role scenario logs out from Pending Expenses and
        // from Manager Expense Review, neither of which has that button — only the
        // dashboards do). Replicating what LogOut() actually does — clear the
        // session, go to "/" — works regardless of which page we're currently on.
        driver().manage().deleteAllCookies();
        ((JavascriptExecutor) driver()).executeScript("window.localStorage.clear();");
        driver().get(TestContext.WELCOME_URL);
    }

    // ---------------auth / route protection Steps---------------------

    @When("I navigate directly to the employee dashboard")
    public void i_navigate_directly_to_the_employee_dashboard() {
        driver().get(TestContext.WELCOME_URL + "employee/dashboard");
    }

    @Then("I should be redirected to the welcome page")
    public void i_should_be_redirected_to_the_welcome_page() {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlToBe(TestContext.WELCOME_URL));
    }

    @Then("I should see a login error {string}")
    public void i_should_see_a_login_error(String errorMessage) {
        By locator = By.cssSelector("[data-testid='login-error-label']");
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, errorMessage));
        assertEquals(errorMessage, driver().findElement(locator).getText());
    }

    @Then("I should still be on the login page")
    public void i_should_still_be_on_the_login_page() {
        assertTrue(driver().getCurrentUrl().contains("/login"));
    }

    // ---------------submit / pending expense Steps---------------------

    @When("I submit an expense with description {string}, amount {string}, category {string}, and date {string}")
    public void i_submit_an_expense_with_description_amount_category_and_date(String description, String amount,
            String category, String date) {
        driver().findElement(By.cssSelector("[data-testid='submit-expense-btn']")).click();
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='description-input']"))).sendKeys(description);
        driver().findElement(By.cssSelector("[data-testid='amount-input']")).sendKeys(amount);

        driver().findElement(By.cssSelector("[data-testid='category-input']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class,'dropdown-item') and text()='" + category + "']"))).click();

        driver().findElement(By.cssSelector("[data-testid='date-input']")).sendKeys(date);
        driver().findElement(By.cssSelector("[data-testid='submit-expense-confirm-btn']")).click();
    }

    @Then("I should see a confirmation that the expense was submitted")
    public void i_should_see_a_confirmation_that_the_expense_was_submitted() {
        String expected = "Expense submitted successfully and is now pending manager review. Submit an additional expense or return to dashboard.";
        By locator = By.cssSelector("[data-testid='response-label']");
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, expected));
        assertEquals(expected, driver().findElement(locator).getText());
    }

    @Then("I should see an expense error {string}")
    public void i_should_see_an_expense_error(String errorMessage) {
        By locator = By.cssSelector("[data-testid='response-label']");
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, errorMessage));
        assertEquals(errorMessage, driver().findElement(locator).getText());
    }

    @When("I return to the dashboard")
    public void i_return_to_the_dashboard() {
        driver().get(TestContext.WELCOME_URL + "employee/dashboard");
    }

    // Navigate by URL rather than click-chaining through "Back to Dashboard" —
    // these steps get reused from several different starting pages, and not every
    // page has the same nav buttons.
    @When("I view my pending expenses")
    public void i_view_my_pending_expenses() {
        driver().get(TestContext.WELCOME_URL + "employee/pending-expenses");
    }

    @Then("I should see the expense {string} with status {string} in my pending expenses")
    public void i_should_see_the_expense_with_status_in_my_pending_expenses(String description, String status) {
        WebElement row = waitForRow(description);
        List<WebElement> cells = row.findElements(By.tagName("td"));
        assertEquals(status, cells.get(3).getText()); // description, amount, category, status, date
    }

    @When("I edit the expense {string} to description {string} and amount {string}")
    public void i_edit_the_expense_to_description_and_amount(String originalDescription, String newDescription,
            String newAmount) {
        WebElement row = waitForRow(originalDescription);
        WebElement editButton = row.findElement(By.cssSelector("[data-testid^='edit-btn-']"));
        editButton.click();
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
        WebElement editDescriptionField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='edit-description-field']")));
        WebElement editAmountField = driver().findElement(By.cssSelector("[data-testid='edit-amount-field']"));

        // The modal pre-fills both fields with the row's current values, so these
        // must be cleared first — sendKeys alone would append to what's there.
        editDescriptionField.clear();
        editDescriptionField.sendKeys(newDescription);
        editAmountField.clear();
        editAmountField.sendKeys(newAmount);
        driver().findElement(By.cssSelector("[data-testid='confirm-edit-btn']")).click();
    }

    @Then("I should see the expense {string} with amount {string} in my pending expenses")
    public void i_should_see_the_expense_with_amount_in_my_pending_expenses(String description, String amount) {
        WebElement row = waitForRow(description);
        List<WebElement> cells = row.findElements(By.tagName("td"));
        assertEquals(amount, cells.get(1).getText()); // description, amount, category, status, date
    }

    @When("I delete the expense {string}")
    public void i_delete_the_expense(String description) {
        WebElement row = waitForRow(description);
        WebElement deleteButton = row.findElement(By.cssSelector("[data-testid^='delete-btn-']"));
        deleteButton.click();
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='confirm-delete-btn']"))).click();
    }

    @Then("the expense {string} should no longer appear in my pending expenses")
    public void the_expense_should_no_longer_appear_in_my_pending_expenses(String description) {
        // Assert against the pending page specifically — the cross-role scenario
        // reaches this step from the expense-history page, where the reviewed
        // expense legitimately still appears.
        driver().get(TestContext.WELCOME_URL + "employee/pending-expenses");
        waitForNoRow(description);
    }

    // ---------------expense history / ledger Steps---------------------

    @When("I view my expense history")
    public void i_view_my_expense_history() {
        driver().get(TestContext.WELCOME_URL + "employee/expense-history");
    }

    @Then("I should not see the expense {string} in my expense history")
    public void i_should_not_see_the_expense_in_my_expense_history(String description) {
        waitForNoRow(description);
    }

    @Then("I should see the expense {string} with status {string} and comment {string} in my expense history")
    public void i_should_see_the_expense_with_status_and_comment_in_my_expense_history(String description,
            String status, String comment) {
        WebElement row = waitForRow(description);
        List<WebElement> cells = row.findElements(By.tagName("td"));
        assertEquals(status, cells.get(3).getText());
        assertEquals(comment, cells.get(4).getText());
    }

}
