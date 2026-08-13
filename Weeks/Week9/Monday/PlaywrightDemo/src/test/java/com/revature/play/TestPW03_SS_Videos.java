package com.revature.play;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.ScreenshotType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.nio.file.Path;

public class TestPW03_SS_Videos {
    // =========================================================================
    // PART 1: Screenshots
    // =========================================================================

    @DisplayName("Playwright Screenshots")
    @Test
    void demoScreenshots() {
        System.out.println("1. SCREENSHOTS");
        System.out.println("─".repeat(40));

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            page.navigate("https://playwright.dev/");

            // Basic screenshot
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("screenshots/basic.png")));
            System.out.println("   ✓ Basic screenshot: screenshots/basic.png");

            // Full page screenshot
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("screenshots/fullpage.png"))
                    .setFullPage(true));
            System.out.println("   ✓ Full page screenshot: screenshots/fullpage.png");

            // Specific viewport size
            page.setViewportSize(1280, 720);
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("screenshots/viewport-1280x720.png")));
            System.out.println("   ✓ Viewport screenshot: screenshots/viewport-1280x720.png");

            // Element screenshot
            Locator header = page.locator("header");
            header.screenshot(new Locator.ScreenshotOptions()
                    .setPath(Paths.get("screenshots/element-header.png")));
            System.out.println("   ✓ Element screenshot: screenshots/element-header.png");

            // Screenshot with mask (hide elements)
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("screenshots/masked.png"))
                    .setMask(java.util.Arrays.asList(
                            page.locator("header")  // Hide header
                    )));
            System.out.println("   ✓ Masked screenshot: screenshots/masked.png");

            // Screenshot as bytes (for attaching to reports)
            byte[] screenshotBytes = page.screenshot();
            System.out.println("   ✓ Screenshot as bytes: " + screenshotBytes.length + " bytes");

            // JPEG format with quality
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("screenshots/quality.jpg"))
                    .setType(ScreenshotType.JPEG)
                    .setQuality(80));
            System.out.println("   ✓ JPEG screenshot: screenshots/quality.jpg");

            browser.close();
        }

        System.out.println();
    }

    // =========================================================================
    // PART 2: Video Recording
    // =========================================================================

    @DisplayName("Playwright Video Recording")
    @Test
    void demoVideoRecording() {
        System.out.println("2. VIDEO RECORDING");
        System.out.println("─".repeat(40));

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true)
            );

            // Create context with video recording enabled
            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setRecordVideoDir(Paths.get("videos/"))
                            .setRecordVideoSize(1280, 720)
            );

            Page page = context.newPage();
            System.out.println("   Recording started...");

            // Perform some actions
            page.navigate("https://the-internet.herokuapp.com/");
            System.out.println("   Navigated to homepage");

            page.locator("a:has-text('Form Authentication')").click();
            System.out.println("   Clicked Form Authentication");

            page.locator("#username").fill("tomsmith");
            page.locator("#password").fill("SuperSecretPassword!");
            System.out.println("   Filled login form");

            page.locator("button[type='submit']").click();
            System.out.println("   Submitted form");

            page.waitForTimeout(1000);  // Wait for video to capture result

            // Get video path (available after page/context close)
            Path videoPath = page.video().path();
            System.out.println("   Video will be saved to: " + videoPath);

            // Close context to finalize video
            context.close();
            System.out.println("   ✓ Recording complete!");

            browser.close();
        }

        System.out.println();
    }
}
