package com.revature;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.revautre.Greeter;

public class GreeterTest {
    @Test
    void testGreet() {
        Greeter greeter = new Greeter();

        String expectedResult = "Hello MATTHEW";

        String actualResult = greeter.hello("Matthew");

        Assertions.assertEquals(expectedResult, actualResult);
    }
}
