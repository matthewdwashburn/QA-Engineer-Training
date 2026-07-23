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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
public class demo_xpath_locatorsTesting {


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

    @Test
    @DisplayName("Absolue XPath - starts from root")
    void absoluteXpath_startsFromRoot(){
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
        try{
            WebElement heading = driver.findElement(
                    By.xpath("/html/body/div[2]/div/h1")
            );
            System.out.println("Found heading: " + heading.getText());
        } catch (NoSuchElementException e ){
            System.out.println("Absolute XPath is brittle - element not found");
        }
    }

    @Test
    @DisplayName("relative XPath - starts with //")
    void relativeXpath_startsAnywhere(){
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
    void xpathById_findElement(){
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
    void xpathByName_findElement(){
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
    void xpathByMultipleAttributes_moreSpecific(){
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
    void xpathContains_partialMatch(){
        /*
        contains() matches if attributes/text CONTAINS the value
        Great for dynamic IDS or partial class names
         */

        driver.get(BASE_URL);

        //Find link containing "Form" in text
        WebElement formLink = driver.findElement(
                By.xpath("//a[contains(text(),'Form')]")
                );

        System.out.println("Found Link: " +formLink.getText());
        assertTrue(formLink.getText().contains("Form"));

        //Find element with class containing 'heading'
        WebElement heading = driver.findElement(By.xpath("//*[contains(@class,'heading')]"));

        System.out.println("Heading: " + heading.getText());
    }

    //4. XPath Axes

    @Test
    @DisplayName("parent axis - Navigate up")
    void xpathParent_navigateUp(){
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
    void xpathChild_navigateDown(){
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
        assertTrue(links.size()>0);
    }
}
