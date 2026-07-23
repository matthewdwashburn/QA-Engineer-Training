package com.revature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class parameterizedTests {

    Calculator calculator = new Calculator();
    @ParameterizedTest
    @ValueSource(ints = {2, 4, 6, 8, 10})
    void isEven_evenNumbers_returnsTru(int number) {
        assertTrue(calculator.isEven(number));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 7, 9})
    void isEven_oddNumbers_returnsFalse(int number) {
        assertFalse(calculator.isEven(number));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void isPositive_positiveNumbers_returnsTrue(int number) {
        assertTrue(calculator.isPositive(number));
    }

    @ParameterizedTest
    @CsvSource({
        "1, 2, 3",
        "4, 5, 9",
        "8, 9, 17",
        "0, 5, 5"
    })
    void add_variousInputs_returnsCorrectResult(int a, int b, int expected) {
        assertEquals(expected, calculator.add(a, b));

    }

    @ParameterizedTest
    @CsvSource({
        "6, 8, -2",
        "1, 2, -1",
        "6, 4, 2",
        "1000, 99, 901",
    })
    void subtract_variousInputs_returnsCorrectResult(int a, int b, int expected) {
        assertEquals(expected, calculator.subtract(a, b));
    }

    @ParameterizedTest
    @MethodSource("provideDivisionTestCases")
    void divide_variousCases_correctQuotientResult(int a, int b, int expected) {
        assertEquals(expected, calculator.divide(a, b));
    }

    static Stream<Arguments> provideDivisionTestCases() {
        return Stream.of(
            Arguments.of(10, 2, 5),
            Arguments.of(15, 3, 5),
            Arguments.of(16, 4, 4),
            Arguments.of(18, 9, 2)
        );
    }

    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({"1, 2, 3", "4, 5, 9", "10, 20, 30"})
    void add_customeDisplayName(int a, int b, int expected) {
        assertEquals(expected, calculator.add(a, b));
    }
}
