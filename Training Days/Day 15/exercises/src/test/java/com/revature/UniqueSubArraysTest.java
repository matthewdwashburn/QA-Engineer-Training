package com.revature;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

public class UniqueSubArraysTest {

    @Test
    public void testSampleInput() throws Exception {
        String input = "6 3\n5 3 5 2 3 2\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            UniqueSubArrays.main(new String[]{});
        } finally {
            System.setIn(System.in);
            System.setOut(originalOut);
        }

        assertEquals("3", outContent.toString().trim());
    }
}
