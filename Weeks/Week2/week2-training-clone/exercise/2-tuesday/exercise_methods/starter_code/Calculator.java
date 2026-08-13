/**
 * Week 2 Exercise — Calculator with static methods and overloads.
 *
 * Division by zero strategy (TODO — choose and implement):
 *   Option A: print error message and return Double.NaN
 *   Option B: return 0.0 and document why (not ideal for production)
 *
 * Compile: javac Calculator.java
 * Run:     java Calculator
 */
public class Calculator {

    public static double add(double a, double b) {
        // TODO
        throw new UnsupportedOperationException("Implement add(a,b)");
    }

    /** Sum of three doubles — overloads add(a,b). */
    public static double add(double a, double b, double c) {
        // TODO: should call add twice or sum directly
        throw new UnsupportedOperationException("Implement add(a,b,c)");
    }

    public static double subtract(double a, double b) {
        throw new UnsupportedOperationException("Implement subtract");
    }

    public static double multiply(double a, double b) {
        throw new UnsupportedOperationException("Implement multiply");
    }

    public static double divide(double a, double b) {
        throw new UnsupportedOperationException("Implement divide with zero check");
    }

    public static void main(String[] args) {
        // TODO: demonstrate all methods including overload and divide-by-zero
        System.out.println("Implement main");
    }
}
