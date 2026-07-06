package com.revature.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;

@DisplayName("Test Lifecycle Demo")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class demo_lifecycle_methods {
    // Shared across test (set in @BeforeAll)
    private static String sharedResource;
    private static int testCounter;

    //Fresh for each test (set in @BeforeEach)
    private Calculator calculator;
    private StringBuilder testLog;

    //@BeforeAll - One-Time Class Setup

    @BeforeAll
    static void setUpClass(){
        System.out.println("@BeforeAll: Setting up test class ONCE");

        //simulate expenseive setup
        sharedResource = "Database connection";
        testCounter = 0;

        // you might 
        // - start a mock server
        // - open a db connection
        // - load a large test data file
        // - initialize heavy resources
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        testCounter++;
        System.out.println("@BeforeEach: Preparing test # " + testCounter);

        // Create fresh instance for each test
        calculator = new Calculator();
        testLog = new StringBuilder();
        testLog.append("Test started |");

        // this is where you would
        // - Create a fresh object instance
        // - resest mocks
        // - prepare test-specific data
        // - start a transaction

    }

    //Actual tests
    @Test
    @Order(3)
    @DisplayName("First test - Calculator is fresh")
    void testOne(){
        System.out.println("Running test one.....");
        testLog.append("Test one executed");
        assertEquals(5, calculator.add(2,3));
        assertNotNull(sharedResource);
    }

    @Test
    @Order(1)
    @DisplayName("Second test - Calculator is fresh again")
    void testTwo() {
        System.out.println("Running test two.....");
        testLog.append("Test two executed");

        //Even if test one modified calculator, we get a fresh one
        assertEquals(8, calculator.add(5, 3));
    }

    @Test
    @Order(2)
    @DisplayName("Third Test - Demonstrates Isolation")
    void testThree(){
        System.out.println("Running test three.....");
        testLog.append("test three executed");

        //This test is completely independent
        assertEquals(6, calculator.multiply(2, 3));

        //testLog is fresh - doesn't have entried from previous tests
        assertTrue(testLog.toString().contains("test three"));
        assertFalse(testLog.toString().contains("test one"));
    }


    //@AfterEach - per-test cleanup
    @AfterEach
    void tearDown(TestInfo testInfo){
        System.out.println("@AfterEach: cleaning up after: " +
            testInfo.getDisplayName());
        
        // Log final state
        testLog.append("| Test completed");
        System.out.println("Log: " + testLog.toString());

        //clean up per-test resources
        calculator=null;
        testLog=null;

        // - roll back transactions
        // - delete test data
        // - close file handlers
        // - reset any modified state
    }

    //AfterAll
    @AfterAll
    static void tearDownClass(){
        System.out.println("@AfterAll: cleaning up test class ONCE");
        System.out.println("Total tests run: " + testCounter);

        //release shared resources
        sharedResource = null;

        //this is where you would
        // - stop mock servers
        // - close database connections
        // - clean up temp files
        // - release expensive resources
    }
}
