package com.revature;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Demo: Allure JUnit5 Setup - First Allure-Annotated Tests
 *
 *
 * 1. Allure integrates with JUnit5 via allure-junit5 dependency
 * 2. Basic annotations: @Description, @Severity, @Link
 * 3. Hierarchy: @Epic, @Feature, @Story
 * 4. Run: mvn clean test && allure serve target/allure-results
 *
 * REQUIRED DEPENDENCIES:
 * - allure-junit5
 * - aspectjweaver (for @Step to work)
 */
@Epic("Week 6: Unit Testing")
@Feature("Allure Reporting")
@DisplayName("Allure JUnit5 Setup Demo")
public class demo_allure_setupTest {

    // ==========================================================
    // SECTION 1: Basic Allure Annotations
    // ==========================================================

    @Test
    @Story("Basic Annotations")
    @Severity(SeverityLevel.NORMAL)
    @Description("This test demonstrates basic Allure annotations with JUnit5")
    @DisplayName("First Allure Test")
    void testBasicAnnotations() {
        // This test will appear in Allure report with all metadata
        int result = 2 + 2;
        assertEquals(4, result);
    }

    @Test
    @Story("Basic Annotations")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Critical tests affect release decisions")
    @DisplayName("Critical Severity Test")
    void testCriticalSeverity() {
        // Critical tests are highlighted in red in the report
        assertTrue(true, "This critical test passes");
    }

    @Test
    @Story("Basic Annotations")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Blocker tests indicate system cannot be released")
    @DisplayName("Blocker Severity Test")
    void testBlockerSeverity() {
        // Blocker is the highest severity
        assertNotNull("value");
    }

    // ==========================================================
    // SECTION 2: Linking to External Systems
    // ==========================================================

    @Test
    @Story("External Links")
    @Link(name = "Documentation", url = "https://docs.example.com/login")
    @Link(name = "Requirements", url = "https://jira.example.com/REQ-123")
    @DisplayName("Test with Documentation Links")
    void testWithLinks() {
        // Links appear as clickable icons in the report
        assertTrue(true);
    }

    @Test
    @Story("External Links")
    @Issue("BUG-456")
    @TmsLink("TC-789")
    @DisplayName("Test with Issue and TMS Links")
    void testWithIssueAndTms() {
        // @Issue links to bug tracker
        // @TmsLink links to test management system
        assertTrue(true);
    }

    // ==========================================================
    // SECTION 3: Test Owner and Labels
    // ==========================================================

    @Test
    @Story("Metadata")
    @Owner("qa-team")
    @DisplayName("Test with Owner")
    void testWithOwner() {
        // Owner appears in report for accountability
        assertTrue(true);
    }

    @Test
    @Story("Metadata")
    @Owner("john.doe")
    @Severity(SeverityLevel.MINOR)
    @Description("Minor issue - UI text typo")
    @DisplayName("Minor Test Example")
    void testMinorSeverity() {
        assertTrue(true);
    }

    // ==========================================================
    // SECTION 4: Nested Tests with Allure
    // ==========================================================

    @Nested
    @Story("Calculator Operations")
    @DisplayName("Calculator Tests")
    class CalculatorTests {

        @Test
        @Severity(SeverityLevel.NORMAL)
        @Description("Verify addition operation")
        @DisplayName("Addition Test")
        void testAddition() {
            assertEquals(5, 2 + 3);
        }

        @Test
        @Severity(SeverityLevel.NORMAL)
        @Description("Verify subtraction operation")
        @DisplayName("Subtraction Test")
        void testSubtraction() {
            assertEquals(7, 10 - 3);
        }
    }

    // ==========================================================
    // SECTION 5: Failing Test Example
    // ==========================================================

    // Uncomment to see how failures appear in Allure
    /*
    @Test
    @Story("Failure Examples")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test intentionally fails to show Allure failure reporting")
    @DisplayName("Intentional Failure")
    void testIntentionalFailure() {
        assertEquals(5, 2 + 2, "This will fail and show in report");
    }
    */

    // ==========================================================
    // SECTION 6: Parameterized Test with Allure
    // ==========================================================

    @ParameterizedTest(name = "Test with value {0}")
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @Story("Parameterized Tests")
    @Severity(SeverityLevel.NORMAL)
    @Description("Parameterized tests show each parameter in the report")
    void testParameterizedWithAllure(int value) {
        assertTrue(value > 0, "Value should be positive");
    }
}
