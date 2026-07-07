package com.revature.unittest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

public class parameterizedAndExceptionTests {
    // Parameterized Tests - Data-Driven Testing
    //@ParameterizedTest replaces @Test for data-driven tests
    //Various sources: @ValueSource, @CsvSource, @MethodSource, @EnumSource
    //Write logig once, run with many inputs
    //required dependency: junit-jupiter-params

    // Create a new single instance of the calcaulator object
    private final Calculator calculator = new Calculator();

    @Nested
    @DisplayName("@ValueSource Examples")
    class ValueSourceExamples{
        @ParameterizedTest
        @ValueSource(ints = {2, 4, 6, 8, 0, -2, 100})
        @DisplayName("Even numbers should be identified correctly")
        void isEven_evenNumbers_returnsTru(int number) {
            assertTrue(calculator.isEven(number), number + " Should be even");
        }

        @ParameterizedTest
        @ValueSource(strings = {"hello", "world", "JUnit6", "Testing"})
        @DisplayName("Strings can be parameterized too")
        void stringLength_variousStrings_calculated(String input){
            assertTrue(input.length()>0);
        }
    }

    @Nested
    @DisplayName("Null and Empty Source Examples")
    class NullEmptyExamples {
        @ParameterizedTest
        @NullSource
        @DisplayName("Null Input Handling")
        void handleNull_nullInput_handled(String input){
            assertNull(input);
        }

        @ParameterizedTest
        @EmptySource
        @DisplayName("Empty String Handling")
        void handleNull_emptyInput_handled(String input) {
            assertTrue(input.isEmpty());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", "\n"})
        @DisplayName(("Blank Strings Should Be Rejected"))
        void validateInput_blandInputs_rejected(String input){
            //combine source for comprehensive blank checking
            assertTrue(input==null || input.trim().isEmpty());
        }
    }
    @Nested
    @DisplayName("@CsvSource Examples")
    class CsvSourceExamples{
        @ParameterizedTest
        @CsvSource({
            "1, 2, 3",
            "0, 0, 0",
            "-1, 1, 0",
            "100, 200, 300",
            "-5, -10, -15"
        })
        @DisplayName("Addition with various inputs")
        void add_variousInputs_correctResult(int a, int b, int expected){
            assertEquals(expected, calculator.add(a,b));
        }

        @ParameterizedTest(name = "{0}+{1}={2}") //Custom Display Name
        @CsvSource({
            "1, 1, 2",
            "2, 3, 5",
            "10, 20, 30"
        })
        @DisplayName("Addition with Custom Display Names")
        void add_withCustomDisplayName(int a, int b, int expected){
            assertEquals(expected, calculator.add(a, b));
        }

        @ParameterizedTest
        @CsvSource(value = {
            "hello | 5",
            "world | 5",
            "JUnit | 5",
            "testing | 7"
        }, delimiter = '|') //custom delimiter
        @DisplayName("String length with pipe delimeter")
        void stringLenth_customDelimiter(String input, int expectedLength){
            assertEquals(expectedLength, input.length());
        }

        @Nested
        @DisplayName("@MethodSource Examples")
        class MethodSourceExamples{
            @ParameterizedTest
            @MethodSource("provideNumbersForAbsoluteValue")
            @DisplayName("Absolute Value Calculation")
            void absoluteValue_variousNumbers_correctResult(int input, int expected){
                assertEquals(expected, calculator.absoluteValue(input));
            }

            //Provider method must be static and return Stream<Arguments>
            static Stream<Arguments> provideNumbersForAbsoluteValue(){
                return Stream.of(
                    Arguments.of(5,5),
                    Arguments.of(-5,5),
                    Arguments.of(0,0),
                    Arguments.of(-100,100),
                    Arguments.of(Integer.MIN_VALUE+1,Integer.MAX_VALUE) // Edge case
                );
            }

            @ParameterizedTest
            @MethodSource("provideMinMaxTestCases")
            @DisplayName("Min/Max operations")
            void minMax_variousCases_correctResult(int a, int b, int expectedMin, int expectedMax){
                assertAll(
                    ()->assertEquals(expectedMin, calculator.min(a,b)),
                    ()->assertEquals(expectedMax, calculator.max(a,b))
                );

            }

            // Provider method must be static and return Stream<Arguments>
            static Stream<Arguments> provideMinMaxTestCases() {
                return Stream.of(
                        Arguments.of(1, 5,1,5),
                        Arguments.of(5, 1,1,5),
                        Arguments.of(-5, 5,-5,5),
                        Arguments.of(0, 0, 0, 0),
                        Arguments.of(-10, -5, -10, -5)
                );
            }

            // First, define an enum for testing
            enum Operation {
                ADD, SUBTRACT, MULTIPLY
            }

            @Nested
            @DisplayName("@EnumSource Examples")
            class EnumSourceExamples{
                @ParameterizedTest
                @EnumSource(Operation.class)
                @DisplayName("All Operations Should be Valid")
                void operation_allValues_valid(Operation op){
                    assertNotNull(op);
                    assertNotNull(op.name());
                }
            }

            @Nested
            @DisplayName("Exception Testing Demo")
            class Exceptions{

                //assertThrows is the primary tool - returns the exception for inspection
                //Always verify exception type
                //verify exception MESSAGE when it contains useful info
                //assertDoesNotThrow explicityly documents what should not be thrown

                Calculator calculator = new Calculator();

                @Test
                @DisplayName("Division by zero throws ArithmeticException")
                void divide_byZero_throwsArithmeticException(){
                    //Basic usage - just verify exception type
                    assertThrows((ArithmeticException.class), ()->{
                        calculator.divide(10,0);
                    });
                }

                @Test
                @DisplayName("Capture exception and verify message")
                void divide_byZero_exceptionHasCorrectMessage(){
                    ArithmeticException exception = assertThrows(
                        ArithmeticException.class, ()->calculator.divide(10,0)
                    );

                    //now verify the message
                    assertEquals("Cannot divide by zero", exception.getMessage());
                }

                @Test
                @DisplayName("Valid Division does not throw exception")
                void divide_validInputs_noException(){
                    //Explicitly verify no exception is thrown
                    assertDoesNotThrow(()->{
                        calculator.divide(10, 2);
                    });
                }
            }
        }
    }
}
