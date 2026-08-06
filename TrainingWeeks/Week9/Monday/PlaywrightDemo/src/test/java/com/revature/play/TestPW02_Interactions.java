package com.revature.play;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Playwright Interactions")
public class TestPW02_Interactions extends BaseTest{

    // PART 1: Auto-Wait Demonstration
    @DisplayName("Demo Auto Wait")
    @Test
    public void demoAutowait(){
        System.out.println("1. AUTO-WAIT DEMONSTRATION");
        System.out.println("─".repeat(40));

        navigateTo("/dynamic_loading/1");

        // In Selenium, we'd need explicit waits here
        // In Playwright, click auto-waits for element to be actionable

        System.out.println("   Clicking 'Start' button...");
        page.locator("#start button").click();

        // This auto-waits for element to appear AND be visible
        System.out.println("   Waiting for result (auto-wait)...");
        String result = page.locator("#finish h4").textContent();

        System.out.println("   Result: " + result);
        System.out.println("   ✓ No explicit waits needed!\n");
    }
    // =========================================================================
    // PART 2: Locator Strategies
    // =========================================================================

    /***
     * In Playwright, ARIA (Accessible Rich Internet Applications)
     * refers to a set of features and tools designed to test and ensure the accessibility of web applications.
     */
    @DisplayName("Locator Strategies")
    @Test
    void demoLocators() {
        System.out.println("2. LOCATOR STRATEGIES");
        System.out.println("─".repeat(40));

        navigateTo("/login");

        // By CSS selector
        Locator byId = page.locator("#username");
        System.out.println("   By ID: #username");

        // By text (exact)
        Locator byText = page.locator("text=Login");
        System.out.println("   By text: 'Login'");

        // By text (contains)
        Locator byTextContains = page.locator("text=Super");
        System.out.println("   By text contains: 'Super'");

        // By role (accessibility)
        Locator byRole = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Login"));
        System.out.println("   By role: button named 'Login'");

        // By placeholder
        Locator byPlaceholder = page.getByPlaceholder("Username");
        System.out.println("   By placeholder: 'Username'");

        // By label
        Locator byLabel = page.getByLabel("Username");
        System.out.println("   By label: 'Username'");

        // By test ID (data-testid attribute)
        // page.getByTestId("login-button");

        // Chained locators
        Locator chained = page.locator("form").locator("button");
        System.out.println("   Chained: form > button");

        // Filter locators
        Locator filtered = page.locator("input")
                .filter(new Locator.FilterOptions().setHasText(""));

        System.out.println("   ✓ Multiple locator strategies available\n");
    }


    // =========================================================================
    // PART 3: Form Interactions
    // =========================================================================
    @DisplayName("Form Interactions")
    @Test
     void demoFormInteractions() {
        System.out.println("3. FORM INTERACTIONS");
        System.out.println("─".repeat(40));

        navigateTo("/login");

        // Fill text input (clears first, then types)
        page.locator("#username").fill("tomsmith");
        System.out.println("   Filled username");

        // Password input
        page.locator("#password").fill("SuperSecretPassword!");
        System.out.println("   Filled password");

        // Click button
        page.locator("button[type='submit']").click();
        System.out.println("   Clicked submit button");

        // Verify success
        assertThat(page.locator("#flash")).containsText("secure area");
        System.out.println("   ✓ Login successful\n");

        // Go back for more demos
        navigateTo("");

        // Navigate to checkboxes
        page.locator("a:has-text('Checkboxes')").click();

        // Checkbox interactions
        Locator checkbox1 = page.locator("input[type='checkbox']").first();
        Locator checkbox2 = page.locator("input[type='checkbox']").last();

        // Check if not already checked
        if (!checkbox1.isChecked()) {
            checkbox1.check();
            System.out.println("   Checked first checkbox");
        }

        // Uncheck if checked
        if (checkbox2.isChecked()) {
            checkbox2.uncheck();
            System.out.println("   Unchecked second checkbox");
        }

        // Navigate to dropdown
        navigateTo("/dropdown");

        // Select dropdown option
        page.locator("#dropdown").selectOption("1");
        System.out.println("   Selected dropdown option 1");

        // Select by label
        page.locator("#dropdown").selectOption(
                new SelectOption().setLabel("Option 2")
        );
        System.out.println("   Selected 'Option 2' by label\n");
    }

    // =========================================================================
    // PART 4: Web-First Assertions
    // =========================================================================

    /**
     * Playwright's web-first assertions are a set of asynchronous assertion methods that automatically wait and retry
     * until the expected condition related to a web element or page is met or a timeout is reached (default is 5 seconds).
     */
    @DisplayName("Web-First Assertions")
    @Test
    void demoAssertions() {
        System.out.println("4. WEB-FIRST ASSERTIONS");
        System.out.println("─".repeat(40));

        navigateTo("");

        // Page assertions
        assertThat(page).hasTitle("The Internet");
        System.out.println("   ✓ Page has expected title");

        assertThat(page).hasURL(Pattern.compile(".*herokuapp.*"));
        System.out.println("   ✓ URL matches pattern");

        // Locator assertions
        Locator heading = page.locator("h1.heading");

        assertThat(heading).isVisible();
        System.out.println("   ✓ Heading is visible");

        assertThat(heading).hasText("Welcome to the-internet");
        System.out.println("   ✓ Heading has correct text");

        assertThat(heading).hasAttribute("class", "heading");
        System.out.println("   ✓ Heading has class attribute");

        // Element count
        Locator links = page.locator("ul li a");
        assertThat(links).hasCount(44);  // Verify count
        System.out.println("   ✓ Found expected number of links");

        // Negative assertions
        assertThat(page.locator(".non-existent")).not().isVisible();
        System.out.println("   ✓ Non-existent element is not visible");

        System.out.println("\n   💡 Assertions auto-retry until timeout!");
        System.out.println("   No need for explicit waits before assertions.\n");
    }
}
