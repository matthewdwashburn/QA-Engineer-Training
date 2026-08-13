package com.revature.domain;

//Mock vs Spy - understanding the difference

//Mock: completely fake - all methods return defaults until stubbed
//Spy: Real object wrapper - real methods execute unless stubbed
//Use Mock for: complete isolation, testing interactions
//Use Spy for: Partial mocking, legacy code, need some real behavior

//Caution: Overusing spies often indicates design problems!

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Mock vs Spy Demo")
public class Demo_Mock_Vs_Spy {

    //Spy Behavior

    @Spy
    private List<String> spyList = new ArrayList<>();

    @Test
    @DisplayName("Spy: real methods execute")
    void demonstrateSpyBehavior(){
        //Spy wraps a real ArrayList - methods actually work!

        spyList.add("item1");
        spyList.add("item2");

        //Real behavior - items were actually added
        assertEquals(2,spyList.size(),"Spy actuall has items");
        assertEquals("item1",spyList.get(0));
        assertEquals("item2",spyList.get(1));


    }

    @Test
    @DisplayName("Spy: can selectively override methods")
    void demonstrateSpySelectiveStubbing(){
        //Create fresh spy
        List<String> freshSpy = spy(new ArrayList<>());

        //Add real items
        freshSpy.add("real1");
        freshSpy.add("real2");

        assertEquals(2,freshSpy.size(), "Real size before stubbing");

        //Now stub just the size() method
        when(freshSpy.size()).thenReturn(100);

        //Size is stubbed, but data is still real
        assertEquals(100,freshSpy.size(),"Size is stubbed");
        assertEquals("real1",freshSpy.get(0),"Data is still real");
        assertEquals("real2",freshSpy.get(1),"Data is still real");

    }

    //side by side comparison
    @Test
    @DisplayName("Comparison: Mock vs Spy behavior")
    @SuppressWarnings("unchecked")
    void compareMockAndSpy(){
        //Create both types
        List<String> mock = mock(ArrayList.class);
        List<String> spy = spy(new ArrayList<>());

        //add to both
        mock.add("item");
        spy.add("item");

        //compare sizes
        assertEquals(0,mock.size(),"Mock didn't actually add");
        assertEquals(1,spy.size(),"Spy actually added");

        //Compare get behavior
        assertNull(mock.get(0),"Mock returns null");
        assertEquals("item",spy.get(0),"Spy returns real item");
    }

    // ==========================================================
    // When to Use Spy - Real-World Scenario
    // ==========================================================

    @Test
    @DisplayName("Spy use case: Partial mocking")
    void demonstrateSpyUseCase() {
        // Imagine a service where most methods work fine,
        // but one method makes an external call

        RealCalculator realCalc = spy(new RealCalculator());

        // Real methods work
        assertEquals(5, realCalc.add(2, 3), "add() works normally");
        assertEquals(6, realCalc.multiply(2, 3), "multiply() works normally");

        // But let's say expensiveOperation() calls an external service
        // We want to stub just that method
        doReturn(999).when(realCalc).expensiveOperation();

        // Now expensiveOperation is stubbed, others are real
        assertEquals(5, realCalc.add(2, 3), "add() still works");
        assertEquals(999, realCalc.expensiveOperation(), "Stubbed method");
    }

    // Helper class for spy demonstration
    static class RealCalculator {
        public int add(int a, int b) { return a + b; }
        public int multiply(int a, int b) { return a * b; }
        public int expensiveOperation() {
            // Imagine this calls external API
            throw new UnsupportedOperationException("Would call external service");
        }
    }

    // ==========================================================
    // Spy Gotcha - doReturn vs when().thenReturn()
    // ==========================================================

    @Test
    @DisplayName("Spy gotcha: Use doReturn() for stubbing")
    void demonstrateSpyGotcha() {
        List<String> spy = spy(new ArrayList<>());

        // PROBLEM with when().thenReturn() on spy:
        // The real method gets called DURING stubbing!
        // when(spy.get(0)).thenReturn("stubbed");  // IndexOutOfBoundsException!

        // SOLUTION: Use doReturn() for spies
        doReturn("stubbed").when(spy).get(0);

        assertEquals("stubbed", spy.get(0));

    }

}
