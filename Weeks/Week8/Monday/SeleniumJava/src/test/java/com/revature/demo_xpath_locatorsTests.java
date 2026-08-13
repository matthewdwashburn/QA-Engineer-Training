package com.revature;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demo: XPath Locators in Selenium
 *
 * 1. XPath is the most flexible locator strategy
 * 2. Prefer relative XPATH over absolute
 * 3. XPath functions enable complex element finding
 * 4. XPath axes navigate the DOM tree
 *
 * TEST site: https://the-internet.herokuapp.com
 */

@DisplayName("XPath Locators Demo")
public class demo_xpath_locatorsTests {


    private WebDriver driver;
    private static final String BASE_URL = "https://the-internet.herokuapp.com";

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Absolue XPath - starts from root")
    void absoluteXpath_startsFromRoot() {
        /*
        Absolute XPath starts with single slash (/)
        Starts from document root

        Problems:
        - Very brittle -breaks id DOM structure changes
        - Hard to read and maintain
        - AVOID in production code
         */

        driver.get(BASE_URL);

        //Absolute XPath - BAD PRACTICE (but shows the concept)
        // Example: /html/body/div[2]/div/h1
        // this would break if any parent element changes

        //let's find the heading using absolute path
        try {
            WebElement heading = driver.findElement(
                    By.xpath("/html/body/div[2]/div/h1")
            );
            System.out.println("Found heading: " + heading.getText());
        } catch (NoSuchElementException e) {
            System.out.println("Absolute XPath is brittle - element not found");
        }
    }

    @Test
    @DisplayName("relative XPath - starts with //")
    void relativeXpath_startsAnywhere() {
        /*
        Relative XPath starts with double slash (//)
        Searches entire document for matching elements

        MUCH BETTER:
        -more maintainable
        -survives DOM restructuring
        -preferred in production
         */

        driver.get(BASE_URL);

        //Relative XPath - GOOD PRACTICE
        WebElement heading = driver.findElement(
                By.xpath("//h1[@class='heading']")
        );
        System.out.println("Heading text: " + heading.getText());
        assertNotNull(heading.getText());
    }

    //2. XPath with Attributes

    @Test
    @DisplayName("XPath by ID attribute")
    void xpathById_findElement() {
        driver.get(BASE_URL + "/login");

        //Find by id attribute
        WebElement usernameInput = driver.findElement(
                By.xpath("//input[@id='username']")
        );
        assertTrue(usernameInput.isDisplayed());
        System.out.println("Found username input by id");
    }

    @Test
    @DisplayName("XPath by name attribute")
    void xpathByName_findElement() {
        driver.get(BASE_URL + "/login");

        //Find by id attribute
        WebElement passwordInput = driver.findElement(
                By.xpath("//input[@name='password']")
        );
        assertTrue(passwordInput.isDisplayed());
        System.out.println("Found password input by name");
    }

    @Test
    @DisplayName("XPath by multiple attributes")
    void xpathByMultipleAttributes_moreSpecific() {
        driver.get(BASE_URL + "/login");
        //combine multiple attributes for more specific matching

        WebElement loginButton = driver.findElement(
                By.xpath("//button[@type='submit' and @class='radius']")
        );

        assertTrue(loginButton.isDisplayed());
        System.out.println("Found login button: " + loginButton.getText());
    }

    @Test
    @DisplayName("contains() - Partial text match")
    void xpathContains_partialMatch() {
        /*
        contains() matches if attributes/text CONTAINS the value
        Great for dynamic IDS or partial class names
         */

        driver.get(BASE_URL);

        //Find link containing "Form" in text
        WebElement formLink = driver.findElement(
                By.xpath("//a[contains(text(),'Form')]")
        );

        System.out.println("Found Link: " + formLink.getText());
        assertTrue(formLink.getText().contains("Form"));

        //Find element with class containing 'heading'
        WebElement heading = driver.findElement(By.xpath("//*[contains(@class,'heading')]"));

        System.out.println("Heading: " + heading.getText());
    }

    @Test
    @DisplayName("text() - Exact text content")
    void xpathText_exactMatch() {
        /*
         * text() matches the exact text content of an element
         * Case-sensitive!
         */

        driver.get(BASE_URL);

        // Find link with exact text
        WebElement checkboxLink = driver.findElement(
                By.xpath("//a[text()='Checkboxes']"));

        System.out.println("Found: " + checkboxLink.getText());
        assertEquals("Checkboxes", checkboxLink.getText());
    }

    @Test
    @DisplayName("normalize-space() - Handle whitespace")
    void xpathNormalizeSpace_handleWhitespace() {
        /*
         * normalize-space() strips leading/trailing whitespace
         * and collapses multiple spaces into one
         *
         * Useful when text has inconsistent spacing
         */

        driver.get(BASE_URL);

        // This handles text with extra whitespace
        WebElement element = driver.findElement(
                By.xpath("//a[normalize-space(text())='Checkboxes']"));

        assertNotNull(element);
        System.out.println("Found element despite whitespace variations");
    }


    //4. XPath Axes

    @Test
    @DisplayName("parent axis - Navigate up")
    void xpathParent_navigateUp() {
        /*
        parent:: moves up one level in DOM
        useful when find child but need parent
         */

        driver.get(BASE_URL + "/tables");

        //Find a cell , then get its parent row
        WebElement cell = driver.findElement(By.xpath(
                "//td[text()='jsmith@gmail.com']"
        ));
        WebElement parentRow = cell.findElement(By.xpath("./parent::tr"));

        System.out.println("Parent row text: " + parentRow.getText());
        assertTrue(parentRow.getText().contains("Smith"));
    }

    @Test
    @DisplayName("child axis - Navigate Down")
    void xpathChild_navigateDown() {
        /*
        child:: selects direct children
        Default axis, so child::div is same as just div
         */

        driver.get(BASE_URL);


        //find all direct child links of the content div
        List<WebElement> links = driver.findElements(
                By.xpath("//div[@id='content']//a")

        );
        System.out.println("Found " + links.size() + " links in content area");
        assertTrue(links.size() > 0);
    }

    // ==========================================================
    // SECTION 5: Complex XPath Expressions
    // ==========================================================

    @Test
    @DisplayName("Combining conditions with and/or")
    void xpathAndOr_combineConditions() {
        driver.get(BASE_URL + "/login");

        // Using 'and'
        WebElement button = driver.findElement(
                By.xpath("//button[@type='submit' and contains(@class, 'radius')]"));

        System.out.println("Button found: " + button.getText());

        // Using 'or'
        List<WebElement> inputs = driver.findElements(
                By.xpath("//input[@id='username' or @id='password']"));

        System.out.println("Found " + inputs.size() + " inputs");
        assertEquals(2, inputs.size());
    }

    @Test
    @DisplayName("Indexed selection with [n]")
    void xpathIndex_selectByPosition() {
        /*
         * XPath indexes start at 1, not 0!
         * Use [1] for first, [last()] for last
         */

        driver.get(BASE_URL);

        // Get first link
        WebElement firstLink = driver.findElement(
                By.xpath("(//a)[1]"));
        System.out.println("First link: " + firstLink.getText());

        // Get last link
        WebElement lastLink = driver.findElement(
                By.xpath("(//a)[last()]"));
        System.out.println("Last link: " + lastLink.getText());

        // Get specific link by position
        WebElement thirdLink = driver.findElement(
                By.xpath("(//div[@id='content']//a)[3]"));
        System.out.println("Third link: " + thirdLink.getText());
    }

    @Test
    @DisplayName("XPath with not() function")
    void xpathNot_excludeElements() {
        /*
         * not() excludes elements matching a condition
         */

        driver.get(BASE_URL + "/checkboxes");

        // Find checkbox that is NOT checked
        List<WebElement> unchecked = driver.findElements(
                By.xpath("//input[@type='checkbox' and not(@checked)]"));

        System.out.println("Unchecked checkboxes: " + unchecked.size());

        // Find checkbox that IS checked
        List<WebElement> checked = driver.findElements(
                By.xpath("//input[@type='checkbox' and @checked]"));

        System.out.println("Checked checkboxes: " + checked.size());
    }
}


