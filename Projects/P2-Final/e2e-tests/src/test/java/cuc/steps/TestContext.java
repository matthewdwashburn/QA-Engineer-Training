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

    /** Defaults to the compose frontend; -De2e.base.url points it elsewhere. */
    public static final String WELCOME_URL =
            System.getProperty("e2e.base.url", "http://127.0.0.1:8080/");

    private WebDriver driver;

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }
}
