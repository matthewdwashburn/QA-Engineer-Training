package com.revature.play;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

public abstract class BaseTest {
    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeAll
    static void globalSetup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(System.getenv("CI") != null)
        );
    }

    @AfterAll
    static void globalTeardown() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    void setup() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
        );
        page = context.newPage();
    }

    @AfterEach
    void teardown(TestInfo testInfo) {
        // Screenshot on failure
        if (testInfo.getTags().contains("screenshot-on-failure")) {
            // Capture logic here
        }
        context.close();
    }

    protected void navigateTo(String path) {
        String baseUrl = System.getenv().getOrDefault("BASE_URL", "https://the-internet.herokuapp.com");
        page.navigate(baseUrl + path);
    }
}
