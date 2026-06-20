/**
 * Week 2 Exercise — Calculator with static methods and overloads.
 *
 */
public class Calculator {

    public static double add(double a, double b) {
        return a + b;
    }

    /** Sum of three doubles — overloads add(a,b). */
    public static double add(double a, double b, double c) {
       return a + b + c;
    }

    public static double subtract(double a, double b) {
        return a - b;
    }

    public static double multiply(double a, double b) {
        return a * b;
    }

    public static double divide(double a, double b) {
        if(b == 0) {
            System.err.println("Error. Cannot divide by zero.");
            return Double.NaN;
        }
        return a / b;
    }

    public static void main(String[] args) {
        System.out.println("12 + 6 = " + Calculator.add(12, 6));
        System.out.println("12 + 6 + 7 = " + Calculator.add(12, 6, 7));
        System.out.println("12 - 6 = " + Calculator.subtract(12, 6));
        System.out.println("12 * 6 = " + Calculator.multiply(12, 6));
        System.out.println("12 / 6 = " + Calculator.divide(12, 6));
        System.out.println("12 / 0 = " + Calculator.divide(12, 0));
    }
}