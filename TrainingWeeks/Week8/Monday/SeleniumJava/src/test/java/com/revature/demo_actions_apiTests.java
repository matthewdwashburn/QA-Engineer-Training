package com.revature;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demo: Actions API for Complex Interactions
 *
 * 1. Actions class for advanced user interactions
 * 2. Mouse actions: hover, drag-drop, double-click, right-click
 * 3. Keyboard actions: key combos, press/release
 * 4. Action chains: build sequences and execute
 *
 * TEST SITE: https://the-internet.herokuapp.com
 */
@DisplayName("Actions API Demo")
public class demo_actions_apiTests {

    private WebDriver driver;
    private Actions actions;
    private static final String BASE_URL = "https://the-internet.herokuapp.com";

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        // Create Actions instance
        actions = new Actions(driver);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ==========================================================
    // SECTION 1: Mouse Hover (moveToElement)
    // ==========================================================

    @Test
    @DisplayName("moveToElement() - Mouse hover")
    void moveToElement_mouseHover() {
        /*
         * moveToElement() moves mouse to center of element
         * Triggers mouseover events (hover menus, tooltips)
         * Must call perform() to execute!
         */

        driver.get(BASE_URL + "/hovers");

        // Find all avatar images
        java.util.List<WebElement> avatars = driver.findElements(
                By.className("figure"));

        // Hover over first avatar
        WebElement firstAvatar = avatars.get(0);

        // Perform hover action
        actions.moveToElement(firstAvatar).perform();

        // Caption should now be visible
        WebElement caption = firstAvatar.findElement(By.className("figcaption"));
        assertTrue(caption.isDisplayed(), "Caption should be visible after hover");

        String captionText = caption.getText();
        System.out.println("Caption text: " + captionText);
        assertTrue(captionText.contains("user1"));

        // Hover over second avatar
        WebElement secondAvatar = avatars.get(1);
        actions.moveToElement(secondAvatar).perform();

        WebElement secondCaption = secondAvatar.findElement(By.className("figcaption"));
        System.out.println("Second caption: " + secondCaption.getText());
        assertTrue(secondCaption.getText().contains("user2"));
    }

    // ==========================================================
    // SECTION 2: Click Actions
    // ==========================================================

    @Test
    @DisplayName("click() via Actions class")
    void actionsClick_alternative() {
        /*
         * Actions.click() is an alternative to WebElement.click()
         * Useful when building action chains
         */

        driver.get(BASE_URL + "/add_remove_elements/");

        WebElement addButton = driver.findElement(
                By.xpath("//button[text()='Add Element']"));

        // Click using Actions
        actions.click(addButton).perform();

        // Verify element was added
        WebElement addedElement = driver.findElement(By.className("added-manually"));
        assertNotNull(addedElement);
    }

    @Test
    @DisplayName("doubleClick() - Double-click action")
    void doubleClick_action() {
        /*
         * doubleClick() simulates double-clicking
         * Some UI elements respond differently to double-click
         */

        // Using a test page with double-click functionality
        // The Internet doesn't have one, so we'll demo on a text element

        driver.get(BASE_URL + "/add_remove_elements/");

        WebElement button = driver.findElement(
                By.xpath("//button[text()='Add Element']"));

        // Double-click adds two elements
        actions.doubleClick(button).perform();

        // Verify two elements were added
        java.util.List<WebElement> addedElements = driver.findElements(
                By.className("added-manually"));

        System.out.println("Elements after double-click: " + addedElements.size());
        assertEquals(2, addedElements.size());
    }

    @Test
    @DisplayName("contextClick() - Right-click action")
    void contextClick_rightClick() {
        /*
         * contextClick() simulates right-click
         * Opens context menu (browser or custom)
         */

        driver.get(BASE_URL + "/context_menu");

        // Find the hot spot area
        WebElement hotSpot = driver.findElement(By.id("hot-spot"));

        // Right-click on it
        actions.contextClick(hotSpot).perform();

        // Alert should appear
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            System.out.println("Alert text: " + alertText);
            assertTrue(alertText.contains("You selected a context menu"));
            alert.accept();
        } catch (NoAlertPresentException e) {
            fail("Expected alert after right-click");
        }
    }

    // ==========================================================
    // SECTION 3: Drag and Drop
    // ==========================================================

    @Test
    @DisplayName("dragAndDrop() - Drag and drop action")
    void dragAndDrop_action() {
        /*
         * dragAndDrop() moves element from source to target
         * Simulates click-hold-move-release
         */

        driver.get(BASE_URL + "/drag_and_drop");

        WebElement source = driver.findElement(By.id("column-a"));
        WebElement target = driver.findElement(By.id("column-b"));

        // Get initial text
        String sourceTextBefore = source.getText();
        String targetTextBefore = target.getText();
        System.out.println("Before - Source: " + sourceTextBefore + ", Target: " + targetTextBefore);

        // Perform drag and drop
        actions.dragAndDrop(source, target).perform();

        // Note: This page has known issues with Selenium drag-drop
        // Alternative approach using JavaScript might be needed

        // Get text after
        String sourceTextAfter = source.getText();
        String targetTextAfter = target.getText();
        System.out.println("After - Source: " + sourceTextAfter + ", Target: " + targetTextAfter);
    }

    @Test
    @DisplayName("Drag and drop with click-hold-move-release")
    void dragAndDrop_clickHoldMoveRelease() {
        /*
         * Alternative to dragAndDrop() using separate actions
         * Sometimes more reliable for complex scenarios
         */

        driver.get(BASE_URL + "/drag_and_drop");

        WebElement source = driver.findElement(By.id("column-a"));
        WebElement target = driver.findElement(By.id("column-b"));

        // Build chain: click, hold, move, release
        actions.clickAndHold(source)
                .moveToElement(target)
                .release()
                .perform();

        System.out.println("Drag and drop completed");
    }

    // ==========================================================
    // SECTION 4: Keyboard Actions
    // ==========================================================

    @Test
    @DisplayName("sendKeys() via Actions")
    void actionsSendKeys_typeText() {
        /*
         * Actions.sendKeys() types to the focused element
         * Doesn't require finding the element first
         */

        driver.get(BASE_URL + "/login");

        WebElement usernameInput = driver.findElement(By.id("username"));

        // Click to focus, then type
        actions.click(usernameInput)
                .sendKeys("tomsmith")
                .perform();

        assertEquals("tomsmith", usernameInput.getAttribute("value"));
    }

    @Test
    @DisplayName("Key combinations (Ctrl+A, Ctrl+C)")
    void keyCombinations_ctrlCommands() {
        /*
         * Use keyDown/keyUp for modifier keys
         * Keys.chord() creates key combinations
         */

        driver.get(BASE_URL + "/login");

        WebElement usernameInput = driver.findElement(By.id("username"));
        usernameInput.sendKeys("select me");

        // Ctrl+A to select all
        actions.click(usernameInput)
                .keyDown(Keys.CONTROL)
                .sendKeys("a")
                .keyUp(Keys.CONTROL)
                .perform();

        System.out.println("Ctrl+A performed - text should be selected");

        // Alternative using Keys.chord()
        usernameInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        System.out.println("Alternative: Keys.chord() for combinations");
    }


    // ==========================================================
    // SECTION 5: Action Chains
    // ==========================================================

    @Test
    @DisplayName("Building action chains")
    void actionChains_buildSequence() {
        /*
         * Chain multiple actions together
         * Use build() to get the composite action
         * Use perform() to execute
         */

        driver.get(BASE_URL + "/login");

        WebElement usernameInput = driver.findElement(By.id("username"));
        // WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        // Build a chain of actions
        actions.click(usernameInput)
                .sendKeys("tomsmith")
                .sendKeys(Keys.TAB)  // Move to password
                .sendKeys("SuperSecretPassword!")
                .click(loginButton)
                .perform();

        // Verify login
        System.out.println("Current URL: " + driver.getCurrentUrl());
    }

    @Test
    @DisplayName("Pause between actions")
    void pauseBetweenActions() {
        /*
         * pause() adds delay between actions
         * Use sparingly - explicit waits are usually better
         */

        driver.get(BASE_URL + "/add_remove_elements/");

        WebElement addButton = driver.findElement(
                By.xpath("//button[text()='Add Element']"));

        // Click, pause, click again
        actions.click(addButton)
                .pause(java.time.Duration.ofMillis(500))
                .click(addButton)
                .pause(java.time.Duration.ofMillis(500))
                .click(addButton)
                .perform();

        java.util.List<WebElement> added = driver.findElements(
                By.className("added-manually"));
        System.out.println("Added elements: " + added.size());
        assertEquals(3, added.size());
    }

    // ==========================================================
    // SECTION 6: Scroll Actions
    // ==========================================================

    @Test
    @DisplayName("Scroll to element")
    void scrollToElement() {
        /*
         * scrollToElement() scrolls until element is in view
         * Selenium 4 feature
         */

        driver.get(BASE_URL + "/infinite_scroll");

        // Scroll down
        actions.scrollByAmount(0, 1000).perform();

        System.out.println("Scrolled down 1000 pixels");

        // Alternative: scroll to specific element
        // actions.scrollToElement(element).perform();
    }

    // ==========================================================
    // SECTION 7: Move by Offset
    // ==========================================================

    @Test
    @DisplayName("moveByOffset() - Move by pixels")
    void moveByOffset_pixelMovement() {
        /*
         * moveByOffset() moves cursor by x,y pixels from current position
         * Useful for canvas elements or precise positioning
         */

        driver.get(BASE_URL + "/hovers");

        WebElement avatar = driver.findElement(By.className("figure"));

        // Move to element center, then offset
        actions.moveToElement(avatar)
                .moveByOffset(10, 10)  // Move 10px right and down
                .perform();

        System.out.println("Moved to element and then by offset");
    }

    // ==========================================================
    // SECTION 8: Common Patterns
    // ==========================================================

    @Test
    @DisplayName("Tooltip hover pattern")
    void tooltipHover_pattern() {
        driver.get(BASE_URL + "/hovers");

        // Find element with tooltip
        WebElement avatar = driver.findElement(
                By.xpath("(//div[@class='figure'])[1]"));

        // Hover to show tooltip
        actions.moveToElement(avatar).perform();

        // Wait for tooltip and get text
        WebElement tooltip = avatar.findElement(By.className("figcaption"));

        assertTrue(tooltip.isDisplayed());
        System.out.println("Tooltip text: " + tooltip.getText());
    }

    @Test
    @DisplayName("Slider manipulation")
    void sliderManipulation_dragByOffset() {
        /*
         * For slider elements, use dragAndDropBy or moveByOffset
         */

        driver.get(BASE_URL + "/horizontal_slider");

        WebElement slider = driver.findElement(
                By.xpath("//input[@type='range']"));

        // Click and drag slider
        actions.clickAndHold(slider)
                .moveByOffset(50, 0)  // Move 50px right
                .release()
                .perform();

        String value = slider.getAttribute("value");
        System.out.println("Slider value: " + value);
    }
}
