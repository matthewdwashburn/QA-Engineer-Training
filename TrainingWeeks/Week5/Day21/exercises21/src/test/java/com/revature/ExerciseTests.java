package com.revature;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExerciseTests {

        // Land perimeter
    @Test
    void testBasic() {
        assertEquals("Total land perimeter: 60",
                LandPerimiter.landPerimeter(new String[] { "OXOOOX", "OXOXOO", "XXOOOX", "OXXXOO", "OOXOOX", "OXOOOO",
                        "OOXOOX", "OOXOOO", "OXOOOO", "OXOOXX" }));
        assertEquals("Total land perimeter: 52", LandPerimiter.landPerimeter(new String[] { "OXOOO", "OOXXX", "OXXOO",
                "XOOOO", "XOOOO", "XXXOO", "XOXOO", "OOOXO", "OXOOX", "XOOOO", "OOOXO" }));
        assertEquals("Total land perimeter: 40", LandPerimiter
                .landPerimeter(new String[] { "XXXXXOOO", "OOXOOOOO", "OOOOOOXO", "XXXOOOXO", "OXOXXOOX" }));
        assertEquals("Total land perimeter: 54", LandPerimiter.landPerimeter(
                new String[] { "XOOOXOO", "OXOOOOO", "XOXOXOO", "OXOXXOO", "OOOOOXX", "OOOXOXX", "XXXXOXO" }));
        assertEquals("Total land perimeter: 40", LandPerimiter
                .landPerimeter(new String[] { "OOOOXO", "XOXOOX", "XXOXOX", "XOXOOO", "OOOOOO", "OOOXOO", "OOXXOO" }));
        assertEquals("Total land perimeter: 4", LandPerimiter.landPerimeter(new String[] { "X" }));
    }

    private void doTest(long expected, String start, String end) {
            long actual = CountIPAddresses.ipsBetween(start, end);
            String message = String.format("for start = \"%s\" and end = \"%s\"", start, end);
            assertEquals(expected, actual, message);
    }

        // IP addresses
    @Test
    public void ipAddressTest() {
            doTest(50, "10.0.0.0", "10.0.0.50");
            doTest(246, "20.0.0.10", "20.0.1.0");
            doTest((1l << 32l) - 1l, "0.0.0.0", "255.255.255.255");
            doTest(1, "150.0.0.0", "150.0.0.1");
            doTest(50, "10.0.0.0", "10.0.0.50");
            doTest(246, "20.0.0.10", "20.0.1.0");
    }

        // Outliers
    @Test
    void outlierTests() {
            assertEquals(3, FindOutlier.find(new int[] { 2, 6, 8, -10, 3 }));
            assertEquals(206847684, FindOutlier
                            .find(new int[] { 206847684, 1056521, 7, 17, 1901, 21104421, 7, 1, 35521, 1, 7781 }));
            assertEquals(0, FindOutlier.find(new int[] { Integer.MAX_VALUE, 0, 1 }));
    }




}
