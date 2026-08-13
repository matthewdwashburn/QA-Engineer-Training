package com.revature.unittest;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test Lifecycle Demo")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class demoLifecycleMethods {

    //shared across test (set in @BeforaAll)
    private static String sharedResource;
    private static int testCounter;

    //Fresh for each test (set in @BeforeEach)
    private Calculator calculator;
    private StringBuilder testLog;

    //@BeforaAll - One-Time Class Setp

    @BeforeAll
    static void setUpClass(){
        System.out.println("@BeforeAll: Setting up test class ONCE");

        //simulate expensive setup
        sharedResource = "Database connection";
        testCounter = 0;

        // you might
        // - start a mock server
        // - open a database connection
        // - load a large test data file
        // - initialize heavy resources


    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        testCounter++;
        System.out.println("@BeforeEach: Preparing test # "+ testCounter);

        // Create fresh instances for each test
        calculator = new Calculator();
        testLog = new StringBuilder();
        testLog.append("Test started |");

        //this is where you would
        // - Create fresh object instances
        // - reset mocks
        // - prepare test-specific data
        // - start a transaction

    }

    //actual tests
    @Order(1)
    @Test
    @DisplayName("First test - Calculator is fresh")
    void testOne(){
        System.out.println("running test one ....");
        testLog.append("test one executed");
        assertEquals(5,calculator.add(2,3));
        assertNotNull(sharedResource);
    }

    @Test
    @Order(2)
    @DisplayName("Second test - Calculator is fresh again")
    void testTwo(){
        System.out.println("running test two ....");
        testLog.append("test two executed");

        //Even if test one modified calculator, we get a fresh one
        assertEquals(8,calculator.add(5,3));

    }

    @Test
    @Order(3)
    @DisplayName("Third Test - Demonstrates Isolation")
    void testThree(){
        System.out.println("Running test three...");
        testLog.append("test three executed");

        //this test is completely independent
        assertEquals(6,calculator.multiply(2,3));

        //testLog is fresh - doesn't have entried from previous tests
        assertTrue(testLog.toString().contains("test three"));
        assertFalse(testLog.toString().contains("test one"));
    }

    //@AfterEach - per-test cleanup
    @AfterEach
    void tearDown(TestInfo testInfo){
        System.out.println("@AfterEach: cleaning up after: " +
                testInfo.getDisplayName());

        //Log final state
        testLog.append(" | Test completed");
        System.out.println("Log: "+ testLog.toString());

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
    static  void tearDownClass(){
        System.out.println("@AfterAll: cleaning up test class ONCE");
        System.out.println("Total tests run: "+ testCounter);

        //release shared resources
        sharedResource = null;

        //this is where you would
        // - stop mock servers
        // - close database connections
        // - clean up temp files
        // - release expensive resources
    }
}
