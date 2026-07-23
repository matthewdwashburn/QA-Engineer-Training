package com.revature;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demo: Element Interactions in Selenium
 * 1. WebElement represents any HTML element
 * 2. Basic interactions: click, sendKeys, clear
 * 3. Information getters: getText, getAttribute, getCssValue
 * 4. State checks: isDisplayed, isEnabled, isSelected
 *
 * TEST site: https://the-internet.herokuapp.com
 */

@DisplayName("Element Interactions Demo")
public class demo_element_interactionsTests {

    private WebDriver driver;
    private static final String BASE_URL = "https://the-internet.herokuapp.com";

    @BeforeEach
    void setUp(){
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterEach
    void tearDown() {
        if(driver != null){
            driver.quit();
        }
    }

    //1. Basic Click Operations
    @Test
    @DisplayName(("click() - Basic button click"))
    void click_basicButton() {
        /* click() simulates a mouse click on the element
         * works on buttons, links, checkboxes, etc.
         */
        driver.get(BASE_URL + "/add_remove_elements/");

        //Find and click the "Add Element" button
        WebElement addButton = driver.findElement(
                By.xpath("//button[text()='Add Element']")
        );

        //Before clicking
        int elementsBefore = driver.findElements(By.className("added-manually")).size();
        System.out.println("Elements before click: " +elementsBefore);

        //click the button
        addButton.click();

        //After clicking
        int elementsAfter = driver.findElements(
                By.className("added-manually")).size();
        System.out.println("Elements after click: " + elementsAfter);

        assertEquals(elementsBefore +1, elementsAfter);
    }

    @Test
    @DisplayName("click() - Link navigation")
    void click_linkNavigation(){
        driver.get(BASE_URL);

        //Find and click a link
        WebElement link =driver.findElement(
                By.linkText("Form Authentication")
        );
        link.click();

        //verify navigation occurred
        assertTrue(driver.getCurrentUrl().contains("login"));
    }

    //2: Text Input Operations
    @Test
    @DisplayName("sendKeys() - Type text into input")
    void sendKeys_typeText(){
        /*
        *sendKeys() types text into input fields
        * works with text fields, textareas, etc.
         */

        driver.get(BASE_URL + "/login");

        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));

        //clear the field first
        usernameInput.clear();

        //Type into fields
        usernameInput.sendKeys("tomsmith");
        passwordInput.sendKeys("SuperSecretPassword!");

        //verify text was entered
        assertEquals("tomsmith", usernameInput.getAttribute("value"));
        assertEquals("SuperSecretPassword!", passwordInput.getAttribute("value"));

        System.out.println("Text Entered successfully!");
    }

    @Test
    @DisplayName("sendKeys() - Special Keys")
    void snedKeys_specialKeys() {

        //Keys enum provides special keys like ENTER, TAB, etc.

        driver.get(BASE_URL + "/login");

        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));

        //type and press TAB to move to next field
        usernameInput.sendKeys("tomsmith");
        usernameInput.sendKeys(Keys.TAB);

        //Type password and press ENTER to submit
        passwordInput.sendKeys("SuperSecretPassword!");
        passwordInput.sendKeys(Keys.ENTER);

        //Verify the login occurred
        System.out.println("Current URL after login: "+driver.getCurrentUrl());
    }

    //Section 3: getting element Information
    @Test
    @DisplayName("getText() - Get visible text")
    void getText_getVisibleText(){
    /*
    getText() returns the visible text of an element
    does not include hidden text or attribute values
     */

    driver.get(BASE_URL + "/login");

    WebElement heading = driver.findElement(By.tagName("h2"));
    String headingText = heading.getText();

    System.out.println("Heading Text: "+ headingText);
    assertEquals("Login Page",headingText);

    //Get text from Paragraph
    WebElement subheading = driver.findElement(By.tagName("h4"));
    System.out.println("Subheading: " +subheading.getText());

    }

    @Test
    @DisplayName("getAttribute() - Get attribute values")
    void getAttribute_getAttributeValues(){
        //getAttribute() retrieves HTML attribute values
        //Common attributes: id, class, name, href, value, placeholder

        driver.get(BASE_URL + "/login");

        WebElement usernameInput = driver.findElement(By.id("username"));

        //get various attributes
        String id = usernameInput.getAttribute("id");
        String type = usernameInput.getAttribute("type");
        String name = usernameInput.getAttribute("name");

        System.out.println("ID: " + id);
        System.out.println("Type: " + type);
        System.out.println("Name: " + name);

        assertEquals("username", id);
        assertEquals("text",type);

        //Get href from a link
        WebElement link = driver.findElement(By.xpath("//a[contains(text(),'Elemental')]"));
        String href = link.getAttribute("href");
        System.out.println("Link href: " +href);
    }
    @Test
    @DisplayName("getCssValue() - Get CSS properties")
    void getCssValue_getCssProperties() {
        /*
         * getCssValue() retrieves computed CSS property values
         * Useful for verifying styling
         */

        driver.get(BASE_URL + "/login");

        WebElement button = driver.findElement(
                By.xpath("//button[@type='submit']"));

        // Get CSS properties
        String backgroundColor = button.getCssValue("background-color");
        String fontSize = button.getCssValue("font-size");
        String display = button.getCssValue("display");

        System.out.println("Background color: " + backgroundColor);
        System.out.println("Font size: " + fontSize);
        System.out.println("Display: " + display);

        // Note: Colors often returned as rgba(...)
        assertNotNull(backgroundColor);
    }

    // ==========================================================
    // SECTION 4: Element State Checks
    // ==========================================================

    @Test
    @DisplayName("isDisplayed() - Check visibility")
    void isDisplayed_checkVisibility() {
        /*
         * isDisplayed() returns true if element is visible
         * Checks CSS display, visibility, and dimensions
         */

        driver.get(BASE_URL + "/login");

        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement button = driver.findElement(
                By.xpath("//button[@type='submit']"));

        // These should be visible
        assertTrue(usernameInput.isDisplayed(),
                "Username input should be displayed");
        assertTrue(button.isDisplayed(),
                "Submit button should be displayed");

        System.out.println("Username displayed: " + usernameInput.isDisplayed());
        System.out.println("Button displayed: " + button.isDisplayed());
    }

    @Test
    @DisplayName("isEnabled() - Check if interactable")
    void isEnabled_checkEnabled() {
        /*
         * isEnabled() returns true if element can be interacted with
         * Disabled elements return false
         */

        driver.get(BASE_URL + "/dynamic_controls");

        WebElement textInput = driver.findElement(
                By.xpath("//input[@type='text']"));

        // Initially disabled
        boolean initiallyEnabled = textInput.isEnabled();
        System.out.println("Initially enabled: " + initiallyEnabled);

        // This particular input starts disabled on this page
        // In a real test, you'd enable it and check again
    }

    @Test
    @DisplayName("isSelected() - Check checkbox/radio state")
    void isSelected_checkSelectedState() {
        /*
         * isSelected() returns true for checked checkboxes/radios
         * Also works for selected options in dropdowns
         */

        driver.get(BASE_URL + "/checkboxes");

        // Find both checkboxes
        java.util.List<WebElement> checkboxes = driver.findElements(
                By.xpath("//input[@type='checkbox']"));

        System.out.println("Number of checkboxes: " + checkboxes.size());

        for (int i = 0; i < checkboxes.size(); i++) {
            WebElement checkbox = checkboxes.get(i);
            boolean isSelected = checkbox.isSelected();
            System.out.println("Checkbox " + (i + 1) + " selected: " + isSelected);
        }

        // Click first checkbox to change its state
        WebElement firstCheckbox = checkboxes.get(0);
        boolean beforeClick = firstCheckbox.isSelected();
        firstCheckbox.click();
        boolean afterClick = firstCheckbox.isSelected();

        System.out.println("Before click: " + beforeClick);
        System.out.println("After click: " + afterClick);
        assertNotEquals(beforeClick, afterClick);
    }

    // ==========================================================
    // SECTION 5: Complete Form Interaction
    // ==========================================================

    @Test
    @DisplayName("Complete login form interaction")
    void completeForm_loginFlow() {
        /*
         * Combine all interactions for a complete form test
         */

        driver.get(BASE_URL + "/login");

        // Find elements
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(
                By.xpath("//button[@type='submit']"));

        // Verify elements are displayed and enabled
        assertTrue(usernameInput.isDisplayed());
        assertTrue(usernameInput.isEnabled());
        assertTrue(passwordInput.isDisplayed());
        assertTrue(passwordInput.isEnabled());
        assertTrue(loginButton.isDisplayed());
        assertTrue(loginButton.isEnabled());

        // Clear and enter credentials
        usernameInput.clear();
        usernameInput.sendKeys("tomsmith");

        passwordInput.clear();
        passwordInput.sendKeys("SuperSecretPassword!");

        // Verify input values
        assertEquals("tomsmith", usernameInput.getAttribute("value"));
        assertEquals("SuperSecretPassword!", passwordInput.getAttribute("value"));

        // Click login
        loginButton.click();

        // Verify success (check for success message or URL)
        WebElement flash = driver.findElement(By.id("flash"));
        String flashText = flash.getText();
        System.out.println("Flash message: " + flashText);

        assertTrue(flashText.contains("You logged into a secure area!") ||
                driver.getCurrentUrl().contains("secure"));
    }

    // ==========================================================
    // SECTION 6: Element Dimensions and Location
    // ==========================================================

    @Test
    @DisplayName("Get element size and location")
    void getElementDimensions_sizeAndLocation() {
        /*
         * getSize() returns element dimensions
         * getLocation() returns element position on page
         * Useful for visual testing or layout verification
         */

        driver.get(BASE_URL + "/login");

        WebElement button = driver.findElement(
                By.xpath("//button[@type='submit']"));

        // Get size
        Dimension size = button.getSize();
        System.out.println("Button width: " + size.getWidth());
        System.out.println("Button height: " + size.getHeight());

        // Get location
        Point location = button.getLocation();
        System.out.println("Button X: " + location.getX());
        System.out.println("Button Y: " + location.getY());

        // Get rectangle (combines size and location)
        Rectangle rect = button.getRect();
        System.out.println("Rectangle: " + rect);

        assertTrue(size.getWidth() > 0);
        assertTrue(size.getHeight() > 0);
    }


}
