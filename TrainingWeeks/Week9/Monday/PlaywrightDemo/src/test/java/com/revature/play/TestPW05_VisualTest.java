package com.revature.play;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

@DisplayName("Visual Tests")
public class TestPW05_VisualTest {

    @DisplayName("Visual Comparison")
    @Test
    void demoVisualComparison() {
        System.out.println("1. VISUAL COMPARISON");
        System.out.println("─".repeat(40));

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            // Set consistent viewport for visual tests
            page.setViewportSize(1280, 720);

            // Navigate to page
            page.navigate("https://the-internet.herokuapp.com/login");

            // Wait for page to be fully loaded
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Take baseline screenshot
//            Path baselinePath = Paths.get("target/visual-tests/baseline/login-page.png");
//            page.screenshot(new Page.ScreenshotOptions()
//                    .setPath(baselinePath));
//            System.out.println("   ✓ Baseline captured: " + baselinePath);

            // In a real test, you would:
            // 1. First run: Create baseline
            // 2. Subsequent runs: Compare against baseline

            // Take current screenshot
            Path currentPath = Paths.get("target/visual-tests/current/login-page.png");
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(currentPath));
            System.out.println("   ✓ Current captured: " + currentPath);

            // Compare (using external tool or Playwright Test in Node.js)
            System.out.println("   ℹ️ Compare images using image diff tool");

            browser.close();
        }

        System.out.println();
    }
}
