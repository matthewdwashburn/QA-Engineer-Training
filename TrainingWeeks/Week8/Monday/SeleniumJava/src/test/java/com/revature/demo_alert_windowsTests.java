package com.revature;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import java.time.Duration;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Demo: Alerts, Windows, and Frame Handling
 *
 * 1. JavaScript alerts require special handling
 * 2. Window handles identify browser windows/tabs
 * 3. iFrames are embedded documents requiring context switch
 * 4. Always return to main content after frame operations
 */
@DisplayName("Alerts and Windows Demo")
public class demo_alert_windowsTests {

    private WebDriver driver;
    private static final String BASE_URL = "https://the-internet.herokuapp.com";

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    // ==========================================================
    // SECTION 1: JavaScript Alerts
    // ==========================================================

    @Test
    @DisplayName("Simple alert - accept()")
    void simpleAlert_accept() {
        driver.get(BASE_URL + "/javascript_alerts");

        // Click button that triggers alert
        driver.findElement(By.xpath("//button[text()='Click for JS Alert']")).click();

        // Switch to alert
        Alert alert = driver.switchTo().alert();

        // Get alert text
        String alertText = alert.getText();
        System.out.println("Alert text: " + alertText);

        // Accept (click OK)
        alert.accept();

        // Verify result
        WebElement result = driver.findElement(By.id("result"));
        assertTrue(result.getText().contains("successfully"));
    }

    @Test
    @DisplayName("Confirm alert - dismiss()")
    void confirmAlert_dismiss() {
        driver.get(BASE_URL + "/javascript_alerts");

        // Click confirm button
        driver.findElement(By.xpath("//button[text()='Click for JS Confirm']")).click();

        Alert alert = driver.switchTo().alert();
        System.out.println("Confirm text: " + alert.getText());

        // Dismiss (click Cancel)
        alert.dismiss();

        WebElement result = driver.findElement(By.id("result"));
        assertTrue(result.getText().contains("Cancel"));
    }

    @Test
    @DisplayName("Prompt alert - sendKeys()")
    void promptAlert_sendKeys() {
        driver.get(BASE_URL + "/javascript_alerts");

        // Click prompt button
        driver.findElement(By.xpath("//button[text()='Click for JS Prompt']")).click();

        Alert alert = driver.switchTo().alert();

        // Enter text in prompt
        alert.sendKeys("Hello Selenium!");

        // Accept
        alert.accept();

        WebElement result = driver.findElement(By.id("result"));
        assertTrue(result.getText().contains("Hello Selenium!"));
    }

    @Test
    @DisplayName("Wait for alert")
    void waitForAlert() {
        driver.get(BASE_URL + "/javascript_alerts");

        driver.findElement(By.xpath("//button[text()='Click for JS Alert']")).click();

        // Wait for alert to be present
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        alert.accept();
        System.out.println("Alert handled with wait");
    }

    // ==========================================================
    // SECTION 2: Multiple Windows/Tabs
    // ==========================================================

    @Test
    @DisplayName("Handle new window")
    void handleNewWindow() {
        driver.get(BASE_URL + "/windows");

        // Store original window handle
        String originalWindow = driver.getWindowHandle();
        System.out.println("Original window: " + originalWindow);

        // Click to open new window
        driver.findElement(By.linkText("Click Here")).click();

        // Wait for new window
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        // Get all window handles
        Set<String> allWindows = driver.getWindowHandles();
        System.out.println("Total windows: " + allWindows.size());

        // Switch to new window
        for (String windowHandle : allWindows) {
            if (!windowHandle.equals(originalWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }

        // Now in new window
        System.out.println("New window title: " + driver.getTitle());
        assertTrue(driver.getTitle().contains("New Window"));

        // Close new window and switch back
        driver.close();
        driver.switchTo().window(originalWindow);

        System.out.println("Back to original: " + driver.getTitle());
    }

    @Test
    @DisplayName("Create new tab (Selenium 4)")
    void createNewTab() {
        driver.get(BASE_URL);
        String originalWindow = driver.getWindowHandle();

        // Selenium 4 way to create new tab
        driver.switchTo().newWindow(WindowType.TAB);

        // Navigate in new tab
        driver.get(BASE_URL + "/login");
        System.out.println("New tab URL: " + driver.getCurrentUrl());

        // Switch back to original
        driver.switchTo().window(originalWindow);
        System.out.println("Original URL: " + driver.getCurrentUrl());
    }

    // ==========================================================
    // SECTION 3: iFrames
    // ==========================================================



    @Test
    @DisplayName("Switch to iframe by WebElement")
    void switchToIframeByElement() {
        driver.get(BASE_URL + "/iframe");

        WebElement iframeElement = driver.findElement(By.tagName("iframe"));
        driver.switchTo().frame(iframeElement);

        // In iframe
        WebElement body = driver.findElement(By.id("tinymce"));
        System.out.println("iFrame content: " + body.getText());

        // Return to parent frame
        driver.switchTo().parentFrame();
    }

    @Test
    @DisplayName("Nested iframes")
    void nestedIframes() {
        driver.get(BASE_URL + "/nested_frames");

        // Switch to top frame
        driver.switchTo().frame("frame-top");

        // Switch to middle frame within top
        driver.switchTo().frame("frame-middle");

        // Get content
        WebElement content = driver.findElement(By.id("content"));
        System.out.println("Middle frame content: " + content.getText());

        // Go back to main document
        driver.switchTo().defaultContent();
    }
}

