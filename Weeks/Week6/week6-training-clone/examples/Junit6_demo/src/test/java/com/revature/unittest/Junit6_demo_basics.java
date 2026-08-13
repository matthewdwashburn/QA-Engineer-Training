package com.revature.unittest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//@DisplayName provides human-readable test names in reports
@DisplayName("Calculator Basic Tests - JUnit6 Fundamentals")
class Junit6_demo_basics {
    // The System Under Test (SUT)
    private final Calculator calculator = new Calculator();

    //Basic Test Structure

    @Test
    @DisplayName("Adding two positive numbers returns their sum")
    void add_twoPositiveNumbers_returnsSum(){
        //ARRANGE - Set up the test data
        int a = 5;
        int b = 3;

        //ACT - Execute the method under test
        int result = calculator.add(a,b);

        //ASSERT - Verify the outcome
        assertEquals(8, result, "5+3 should equal 8");
    }

    @Test
    @DisplayName("Subtracting returns the difference")
    void subtract_twoNumbers_returnsDifference(){
        //Simple one-liner when logic is straightforward
        assertEquals(7,calculator.subtract(10,3));
    }

    //Testing Edge Cases
    @Test
    @DisplayName("Adding zero doesn't change the number")
    void add_withZero_returnsOriginalNumber(){
        assertEquals(42,calculator.add(42,0),"adding zero should return original");
        assertEquals(42,calculator.add(0,42),"Zero plus number should return number"        );
    }

    @Test
    @DisplayName("Multipling by Zero returns zero")
    void multiply_byZero_returnsZero(){
        assertEquals(0,calculator.multiply(100,0));
        assertEquals(0,calculator.multiply(0,100));

    }

    @Test
    @DisplayName("Negative numbers are handled correctly")
    void add_negativeNumbers_handledCorrectly(){
        assertEquals(-8,calculator.add(-5,-3),"Two Negatives");
        assertEquals(2,calculator.add(5,-3),"Positive and negative");
        assertEquals(-2,calculator.add(-5,3),"Negative and positive");
    }

    //Boolean Assertions
    @Test
    @DisplayName("Even number detection works correctly")
    void isEven_variousNumbers_correctlyIdentified(){
        assertTrue(calculator.isEven(2),"2 should be even");
        assertTrue(calculator.isEven(0),"0 should be even");
        assertTrue(calculator.isEven(-4),"-4 should be even");

        assertFalse(calculator.isEven(1),"1 should be odd");
        assertFalse(calculator.isEven(7),"7 should be odd");
        assertFalse(calculator.isEven(-3),"-3 should be odd");
    }

    @Test
    @DisplayName("Positive number detection works correctly")
    void isPositive_variousNumbers_correctlyIdentified(){
        assertTrue(calculator.isPositive(1),"One is positive");
        assertTrue(calculator.isPositive(100),"One-Hundred is positive");

        assertFalse(calculator.isPositive(0), "zero is not positive");
        assertFalse(calculator.isPositive(-1),"Negative is not positive");
    }

    //methodName_scenario_expectedBehavior
    @Test
    void max_firstLarger_returnsFirst(){
        assertEquals(10,calculator.max(10,5));
    }

    @Test
    void max_secondLarger_returnsFirst(){
        assertEquals(10,calculator.max(5,10));
    }

    @Test
    void max_equalNumbers_returnsEither(){
        assertEquals(7,calculator.max(7,7));
    }

    //other common patterns naming conventions
    //should_expectedBehavior_when_scenario
    //given_precondition_when_action_then_result
}
