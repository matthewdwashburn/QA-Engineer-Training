package com.revature;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.junit.jupiter.api.*;
import org.openqa.selenium.support.ui.UnexpectedTagNameException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demo: Working with Select Dropdowns
 *
 * 1. Select class wraps HTML <select> elements
 * 2. Three ways to select: byValue, byIndex, byVisibleText
 * 3. Multi-select dropdowns have additional methods
 * 4. Select class only works with <select> tags, not custom dropdowns
 *
 * TEST SITE: https://the-internet.herokuapp.com/dropdown
 */
@DisplayName("Select Dropdowns Demo")
public class demo_select_dropdownsTests {
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

    // ==========================================================
    // SECTION 1: Creating Select Object
    // ==========================================================

    @Test
    @DisplayName("Create Select object from dropdown")
    void createSelect_fromDropdown() {
        /*
         * Select class constructor takes a WebElement
         * The element must be a <select> tag!
         */

        driver.get(BASE_URL + "/dropdown");

        // Find the select element
        WebElement dropdownElement = driver.findElement(By.id("dropdown"));

        // Wrap in Select object
        Select dropdown = new Select(dropdownElement);

        // Verify it's not a multi-select
        boolean isMultiple = dropdown.isMultiple();
        System.out.println("Is multi-select: " + isMultiple);
        assertFalse(isMultiple);
    }

    // ==========================================================
    // SECTION 2: Select by Visible Text
    // ==========================================================

    @Test
    @DisplayName("selectByVisibleText() - Select by displayed text")
    void selectByVisibleText_selectOption() {
        /*
         * selectByVisibleText() matches the text shown to users
         * Most intuitive method - use when text is stable
         */

        driver.get(BASE_URL + "/dropdown");

        WebElement dropdownElement = driver.findElement(By.id("dropdown"));
        Select dropdown = new Select(dropdownElement);

        // Select "Option 1" by visible text
        dropdown.selectByVisibleText("Option 1");

        // Verify selection
        WebElement selectedOption = dropdown.getFirstSelectedOption();
        assertEquals("Option 1", selectedOption.getText());

        System.out.println("Selected: " + selectedOption.getText());

        // Select "Option 2"
        dropdown.selectByVisibleText("Option 2");
        assertEquals("Option 2", dropdown.getFirstSelectedOption().getText());
    }

    // ==========================================================
    // SECTION 3: Select by Value
    // ==========================================================

    @Test
    @DisplayName("selectByValue() - Select by value attribute")
    void selectByValue_selectOption() {
        /*
         * selectByValue() matches the "value" attribute of <option>
         * Use when you know the underlying values
         *
         * HTML: <option value="1">Option 1</option>
         */

        driver.get(BASE_URL + "/dropdown");

        WebElement dropdownElement = driver.findElement(By.id("dropdown"));
        Select dropdown = new Select(dropdownElement);

        // Select option with value="1"
        dropdown.selectByValue("1");

        // Verify selection
        WebElement selected = dropdown.getFirstSelectedOption();
        assertEquals("1", selected.getAttribute("value"));
        assertEquals("Option 1", selected.getText());

        System.out.println("Selected value: " + selected.getAttribute("value"));
        System.out.println("Selected text: " + selected.getText());

        // Select option with value="2"
        dropdown.selectByValue("2");
        assertEquals("2", dropdown.getFirstSelectedOption().getAttribute("value"));
    }

    // ==========================================================
    // SECTION 4: Select by Index
    // ==========================================================

    @Test
    @DisplayName("selectByIndex() - Select by position")
    void selectByIndex_selectOption() {
        /*
         * selectByIndex() uses 0-based indexing
         * Index 0 is the first option
         * Use when order matters or other methods don't work
         */

        driver.get(BASE_URL + "/dropdown");

        WebElement dropdownElement = driver.findElement(By.id("dropdown"));
        Select dropdown = new Select(dropdownElement);

        // Get all options to see what we're working with
        List<WebElement> options = dropdown.getOptions();
        System.out.println("Total options: " + options.size());
        for (int i = 0; i < options.size(); i++) {
            System.out.println("  Index " + i + ": " + options.get(i).getText());
        }

        // Select by index (0 = first option, usually placeholder)
        dropdown.selectByIndex(1); // Second option
        assertEquals("Option 1", dropdown.getFirstSelectedOption().getText());

        dropdown.selectByIndex(2); // Third option
        assertEquals("Option 2", dropdown.getFirstSelectedOption().getText());
    }

    // ==========================================================
    // SECTION 5: Getting Selected Options
    // ==========================================================

    @Test
    @DisplayName("Get selected option information")
    void getSelectedOption_information() {
        driver.get(BASE_URL + "/dropdown");

        WebElement dropdownElement = driver.findElement(By.id("dropdown"));
        Select dropdown = new Select(dropdownElement);

        // Select an option first
        dropdown.selectByVisibleText("Option 2");

        // Get the selected option
        WebElement selected = dropdown.getFirstSelectedOption();

        // Get various properties
        String text = selected.getText();
        String value = selected.getAttribute("value");
        boolean isSelected = selected.isSelected();

        System.out.println("Text: " + text);
        System.out.println("Value: " + value);
        System.out.println("Is Selected: " + isSelected);

        assertEquals("Option 2", text);
        assertEquals("2", value);
        assertTrue(isSelected);
    }

    // ==========================================================
    // SECTION 6: Getting All Options
    // ==========================================================

    @Test
    @DisplayName("getOptions() - List all available options")
    void getOptions_listAllOptions() {
        driver.get(BASE_URL + "/dropdown");

        WebElement dropdownElement = driver.findElement(By.id("dropdown"));
        Select dropdown = new Select(dropdownElement);

        // Get all options
        List<WebElement> allOptions = dropdown.getOptions();

        System.out.println("All dropdown options:");
        for (WebElement option : allOptions) {
            String text = option.getText();
            String value = option.getAttribute("value");
            boolean isEnabled = option.isEnabled();

            System.out.println("  '" + text + "' (value='" + value + "', enabled=" + isEnabled + ")");
        }

        // Verify expected number of options
        assertEquals(3, allOptions.size()); // Including placeholder
    }

    // ==========================================================
    // SECTION 7: Multi-Select Dropdowns
    // ==========================================================

    @Test
    @DisplayName("Multi-select dropdown operations")
    void multiSelect_operations() {
        /*
         * Multi-select has additional methods:
         * - isMultiple()
         * - deselectAll()
         * - deselectByValue(), deselectByIndex(), deselectByVisibleText()
         * - getAllSelectedOptions()
         *
         * Note: The Internet doesn't have multi-select, so we'll use DemoQA
         */

        // Navigate to a page with multi-select (using example HTML)
        // For demo purposes, we'll create our own simple HTML

        String html = "data:text/html,<html><body>" +
                "<select id='multi' multiple>" +
                "<option value='1'>Red</option>" +
                "<option value='2'>Green</option>" +
                "<option value='3'>Blue</option>" +
                "<option value='4'>Yellow</option>" +
                "</select></body></html>";

        driver.get(html);

        WebElement multiSelect = driver.findElement(By.id("multi"));
        Select select = new Select(multiSelect);

        // Verify it's a multi-select
        assertTrue(select.isMultiple(), "Should be multi-select");

        // Select multiple options
        select.selectByVisibleText("Red");
        select.selectByVisibleText("Blue");
        select.selectByValue("4"); // Yellow

        // Get all selected options
        List<WebElement> selected = select.getAllSelectedOptions();
        System.out.println("Selected options: " + selected.size());
        for (WebElement option : selected) {
            System.out.println("  - " + option.getText());
        }

        assertEquals(3, selected.size());

        // Deselect one
        select.deselectByVisibleText("Blue");
        assertEquals(2, select.getAllSelectedOptions().size());

        // Deselect all
        select.deselectAll();
        assertEquals(0, select.getAllSelectedOptions().size());
    }

    // ==========================================================
    // SECTION 8: Handling Non-Standard Dropdowns
    // ==========================================================

    @Test
    @DisplayName("Non-standard dropdown (custom/div-based)")
    void nonStandardDropdown_customHandling() {
        /*
         * Many modern UIs use custom dropdowns (div/ul/li based)
         * Select class won't work on these!
         * Must handle manually with clicks
         */

        driver.get(BASE_URL + "/dropdown");

        // For demonstration - this shows the concept
        // Real custom dropdowns need different approach:

        System.out.println("For custom dropdowns (not <select>):");
        System.out.println("1. Click the dropdown trigger element");
        System.out.println("2. Wait for options to appear");
        System.out.println("3. Find and click the desired option");
        System.out.println();
        System.out.println("Example:");
        System.out.println("  driver.findElement(By.cssSelector('.dropdown-trigger')).click();");
        System.out.println("  driver.findElement(By.xpath(\"//li[text()='Option 1']\")).click();");

        // The Select class ONLY works with <select> tags
        try {
            // This would fail if element is not a <select>
            WebElement notASelect = driver.findElement(By.tagName("h3"));
            Select willFail = new Select(notASelect);
            fail("Should have thrown exception");
        } catch (UnexpectedTagNameException e) {
            System.out.println("\nSelect class throws UnexpectedTagNameException for non-select elements");
        }
    }

    // ==========================================================
    // SECTION 9: Complete Dropdown Workflow
    // ==========================================================

    @Test
    @DisplayName("Complete dropdown selection workflow")
    void completeDropdownWorkflow() {
        driver.get(BASE_URL + "/dropdown");

        // 1. Find the dropdown element
        WebElement dropdownElement = driver.findElement(By.id("dropdown"));

        // 2. Create Select object
        Select dropdown = new Select(dropdownElement);

        // 3. Check initial state
        WebElement initialSelected = dropdown.getFirstSelectedOption();
        System.out.println("Initial selection: " + initialSelected.getText());

        // 4. Get all available options
        List<WebElement> options = dropdown.getOptions();
        System.out.println("Available options: " + options.size());

        // 5. Select desired option
        dropdown.selectByVisibleText("Option 1");

        // 6. Verify selection changed
        WebElement currentSelected = dropdown.getFirstSelectedOption();
        assertEquals("Option 1", currentSelected.getText());

        // 7. Get the value for potential form submission
        String selectedValue = currentSelected.getAttribute("value");
        System.out.println("Selected value for form: " + selectedValue);

        System.out.println("\nWorkflow completed successfully!");
    }
}
