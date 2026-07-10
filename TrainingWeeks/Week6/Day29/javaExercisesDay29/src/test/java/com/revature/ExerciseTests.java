package com.revature;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExerciseTests {
    @Test
    public void tests() {
        assertEquals(9, Chairs.lastChair(10));
    }

    @Test
    public void test() {
        assertEquals(3, EvenIndex.findEvenIndex(new int[] { 1, 2, 3, 4, 3, 2, 1 }));
        assertEquals(1, EvenIndex.findEvenIndex(new int[] { 1, 100, 50, -51, 1, 1 }));
        assertEquals(-1, EvenIndex.findEvenIndex(new int[] { 1, 2, 3, 4, 5, 6 }));
        assertEquals(3, EvenIndex.findEvenIndex(new int[] { 20, 10, 30, 10, 10, 15, 35 }));
        assertEquals(-1, EvenIndex.findEvenIndex(new int[] { -8505, -5130, 1926, -9026 }));
        assertEquals(1, EvenIndex.findEvenIndex(new int[] { 2824, 1774, -1490, -9084, -9696, 23094 }));
        assertEquals(6, EvenIndex.findEvenIndex(new int[] { 4, 5, 6, 7, 8, 9, 10, 9, 8, 7, 6, 5, 4 }));
    }
}
