package com.revature.unittest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Junit Assertions Comprehensive Demo")
public class demo_assertions_comprehensive {
    @Test
    @DisplayName("assertEquals - Comparing Values")
    void demonstrateEquals(){
        //Primative comparison
        assertEquals(4, 2+2);
        assertEquals(4, 2+2, "Basic math should work");

        //String comparison
        String expected = "hello world";
        String actual = "hello " + "world";
        assertEquals(expected, actual);

        //Object comparison
        Integer num1 = Integer.valueOf(100);
        Integer num2 = Integer.valueOf(100);
        assertEquals(num1, num2);

    }

    @Test
    @DisplayName("assertEquals with delta - Floating point comparison")
    void demonstrateEqualsFloatingPointComparison(){
        double result = 0.1 + 0.2; // This is not exactly 0.3 due to IEEE 754!

        //Bad: this might fail due to floating point precision
        //assertEquals(0.3,result) //Don't do this

        //Good: Use delta (tolerance) for floating-point precision
        assertEquals(0.3, result, 0.00001, "Should be approximately 0.3");

        //PI Comparison should use delta
        assertEquals(3.14159, Math.PI, 0.00001);
    }

    @Test
    @DisplayName("assertNotEquals - Values should differ")
    void demonstrateNotEquals(){
        assertNotEquals("hello", "world");
        assertNotEquals(1, 2);
        assertNotEquals(null, "something");
    }

    // Boolean Assertions

    @Test
    @DisplayName("assertTrue/assertFalse - Boolean Conditions")
    void demonstrateBooleanAssertions(){
        //assertTrue
        assertTrue(5>3, "5 should be greater than 3");
        assertTrue("Hello".startsWith("H"), "Hello should start with H");
        assertTrue(List.of(1,2,3).contains(2));

        // assertFalse
        assertFalse(5 < 3, "3 should be less than 5");
        assertFalse("Hello".isEmpty(), "Hello should not be empty");
        assertFalse(List.of(1, 2, 3).contains(99));
    }

    @Test
    @DisplayName("Why specific assertions beat assertTrue")
    void demonstrateSpecificVsGeneric(){
        int result = 7;
        // LESS INFORMATIVE: "expected: <true> but was <false>"
        // assertTrue(result==7)

        // MORE INFORMATIVE
        assertEquals(7, result, "Specific assertions give better messages");
    }

    // Null assertions
    @Test
    @DisplayName("assertNull/AssertNotNull = null checking")
    void demonstrateNullAssertions(){
        String nullValue = null;
        String nonNullValue = "exists";

        assertNull(nullValue, "Should be null");
        assertNotNull(nonNullValue, "Should not be null");

        // Real-world example
        //assertNull(repository.findById(-1), "Non-existing ID reutrns null");
        //assertNotNull(repository.findById(1), "Existing ID returns object");
    }

    //Reference assertions
    @Test
    @DisplayName("assertSame/assertNotSame = Reference comparison")
    void demonstrateReferenceAssertions(){
        String str1 = "hello";
        String str2 = str1; // same reference
        String str3 = new String("hello"); // Different object, same value

        //same reference
        assertSame(str1, str2, "Should be the same object reference");

        //Different references, equal content
        assertEquals(str1, str3); //passes - same content
        assertNotSame(str1, str3); //passes - different objects

        //use assertSame w/ singletons, caching, object identity
        //use assertEquals with testing value equality

    }

    @Test
        @DisplayName("assertArrayEquals - Array comparison")
        void demonstrateArrayAssertions() {
            int[] expected = {1, 2, 3, 4, 5};
            int[] actual = {1, 2, 3, 4, 5};

            assertArrayEquals(expected, actual);

            // With floating point
            double[] expectedDoubles = {1.0, 2.0, 3.0};
            double[] actualDoubles = {1.001, 1.999, 3.002};
            assertArrayEquals(expectedDoubles, actualDoubles, 0.01,
                    "Each element should be within 0.01");
        }

        @Test
        @DisplayName("assertIterableEquals - Collection comparison")
        void demonstrateIterableAssertions() {
            List<String> expected = Arrays.asList("apple", "banana", "cherry");

            List<String> actual = new ArrayList<>();
            actual.add("apple");
            actual.add("banana");
            actual.add("cherry");

            assertIterableEquals(expected, actual);
        }

        // ==========================================================
        // assertAll - GROUPED ASSERTIONS (Key Feature!)
        // ==========================================================

        @Test
        @DisplayName("assertAll - Run all assertions, report all failures")
        void demonstrateAssertAll() {
            // Simulating a User object
            String firstName = "John";
            String lastName = "Doe";
            int age = 30;
            String email = "john@example.com";

            // WITHOUT assertAll - stops at first failure
            // assertEquals("John", firstName);
            // assertEquals("Doe", lastName);    // Never runs if above fails
            // assertEquals(30, age);            // Never runs if above fails

            // WITH assertAll - runs all, reports all failures
            assertAll("User validation",
                    () -> assertEquals("John", firstName, "First name check"),
                    () -> assertEquals("Doe", lastName, "Last name check"),
                    () -> assertEquals(30, age, "Age check"),
                    () -> assertTrue(email.contains("@"), "Email format check"),
                    () -> assertNotNull(email, "Email not null check")
            );
        }

        @Test
        @DisplayName("assertAll with intentional failures - See multiple errors")
        void demonstrateAssertAllMultipleFailures() {
            // Uncomment to show multiple failure output
        /*
        String data = "test";
        assertAll("Multiple failures demo",
            () -> assertEquals("wrong", data),      // Fails
            () -> assertTrue(data.length() > 10),   // Fails
            () -> assertNull(data)                  // Fails
        );
        */

            // Output shows ALL three failures, not just the first one!
        }

        // ==========================================================
        // Timeout Assertions
        // ==========================================================

        @Test
        @DisplayName("assertTimeout - Performance constraints")
        void demonstrateTimeout() {
            // Operation must complete within time limit
            String result = assertTimeout(Duration.ofMillis(500), () -> {
                // Simulate quick operation
                Thread.sleep(100);
                return "completed";
            });

            assertEquals("completed", result);
        }

        @Test
        @DisplayName("assertTimeoutPreemptively - Abort if too slow")
        void demonstrateTimeoutPreemptive() {
            // This version INTERRUPTS the operation if it exceeds time
            assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
                // Quick operation
                return 42;
            });

             // - assertTimeout: Waits for completion, then fails
            // - assertTimeoutPreemptively: Interrupts immediately (different thread!)
        }
}
