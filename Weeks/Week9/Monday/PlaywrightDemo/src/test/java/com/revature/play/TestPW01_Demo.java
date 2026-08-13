package com.revature.play;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPW01_Demo {

    @DisplayName("Basic PlayWright Setup")
    @Test
    void basicTest() {
        System.out.println("🎭 PLAYWRIGHT SETUP DEMO");
        System.out.println("========================\n");

        // =================================================================
        // PART 1: Basic Playwright Usage
        // =================================================================
        System.out.println("1. Creating Playwright instance...");

        // Playwright.create() initializes the Playwright library
        // Use try-with-resources for automatic cleanup
        try (Playwright playwright = Playwright.create()) {

            System.out.println("2. Launching browser...");

            // Launch Chromium browser
            // Default is headless=true; set to false to see browser
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)  // Show browser for demo
                            .setSlowMo(500)      // Slow down for visibility
            );

            System.out.println("3. Creating page...");

            // Create a new page (tab)
            Page page = browser.newPage();

            System.out.println("4. Navigating to website...\n");

            // Navigate to a URL
            page.navigate("https://playwright.dev/");

            // Get and print the title
            String title = page.title();
            System.out.println("   Page Title: " + title);
            System.out.println("   URL: " + page.url());

            // =============================================================
            // PART 2: Simple Interaction
            // =============================================================
            System.out.println("\n5. Interacting with page...");

            // Click the "Get started" link
            // Note: NO EXPLICIT WAIT NEEDED - Playwright auto-waits!
            page.locator("a:has-text('Get started')").click();

            // Verify navigation (auto-retrying assertion)
            assertThat(page).hasURL(java.util.regex.Pattern.compile(".*intro"));

            System.out.println("   Navigated to: " + page.url());

            // =============================================================
            // PART 3: Screenshot
            // =============================================================
            System.out.println("\n6. Taking screenshot...");

            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(java.nio.file.Paths.get("screenshot.png")));

            System.out.println("   Screenshot saved: screenshot.png");

            // =============================================================
            // Cleanup
            // =============================================================
            System.out.println("\n7. Closing browser...");
            browser.close();

        } // Playwright automatically closed here

        System.out.println("\n✓ Demo completed successfully!");
    }


}
