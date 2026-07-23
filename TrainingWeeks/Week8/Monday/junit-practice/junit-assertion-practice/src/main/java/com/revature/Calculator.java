package com.revature;

public class Calculator {

    /**
     * Adds two integers.
     * 
     * @param a First operand
     * @param b Second operand
     * @return Sum of a and b
     */
    public int add(int a, int b) {
        return a + b;
    }

    /**
     * Subtracts second integer from first.
     * 
     * @param a First operand
     * @param b Second operand
     * @return Difference (a - b)
     */
    public int subtract(int a, int b) {
        return a - b;
    }

    /**
     * Multiplies two integers.
     * 
     * @param a First operand
     * @param b Second operand
     * @return Product of a and b
     */
    public int multiply(int a, int b) {
        return a * b;
    }

    /**
     * Divides first integer by second.
     * 
     * @param a Dividend
     * @param b Divisor
     * @return Quotient (a / b)
     * @throws ArithmeticException if divisor is zero
     */
    public int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return a / b;
    }

    /**
     * Calculates the remainder of division.
     * 
     * @param a Dividend
     * @param b Divisor
     * @return Remainder of a / b
     * @throws ArithmeticException if divisor is zero
     */
    public int modulo(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Cannot calculate modulo with zero divisor");
        }
        return a % b;
    }

    /**
     * Calculates power of a number.
     * 
     * @param base     The base number
     * @param exponent The exponent
     * @return base raised to exponent power
     * @throws IllegalArgumentException if exponent is negative
     */
    public long power(int base, int exponent) {
        if (exponent < 0) {
            throw new IllegalArgumentException("Exponent cannot be negative for integer power");
        }
        long result = 1;
        for (int i = 0; i < exponent; i++) {
            result *= base;
        }
        return result;
    }

    /**
     * Calculates the absolute value.
     * 
     * @param value Input value
     * @return Absolute value (always non-negative)
     */
    public int absoluteValue(int value) {
        return value < 0 ? -value : value;
    }

    /**
     * Checks if a number is even.
     * 
     * @param number The number to check
     * @return true if even, false if odd
     */
    public boolean isEven(int number) {
        return number % 2 == 0;
    }

    /**
     * Checks if a number is positive.
     * 
     * @param number The number to check
     * @return true if positive (greater than zero)
     */
    public boolean isPositive(int number) {
        return number > 0;
    }

    /**
     * Returns the maximum of two numbers.
     * 
     * @param a First number
     * @param b Second number
     * @return The larger of the two numbers
     */
    public int max(int a, int b) {
        return a > b ? a : b;
    }

    /**
     * Returns the minimum of two numbers.
     * 
     * @param a First number
     * @param b Second number
     * @return The smaller of the two numbers
     */
    public int min(int a, int b) {
        return a < b ? a : b;
    }
}
