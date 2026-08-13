package cuc.steps;

import org.openqa.selenium.WebDriver;

/**
 * Shared state for a single scenario, injected by PicoContainer into every
 * step definition class that declares it as a constructor parameter. One
 * instance is created per scenario, which is what lets the cross-role
 * scenario drive a single browser session across both the employee and
 * manager step classes.
 */
public class TestContext {

    public static final String WELCOME_URL = resolveBaseUrl();

    private WebDriver driver;

    private static String resolveBaseUrl() {
        String override = System.getProperty("e2e.base.url");
        if (override == null || override.isBlank()) {
            return "http://127.0.0.1:5173/";
        }
        // Callers append paths directly, so the trailing slash is not optional.
        return override.endsWith("/") ? override : override + "/";
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }
}
