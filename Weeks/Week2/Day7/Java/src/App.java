public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        String firstName = args[0];
        String lastName = args[1];
        System.out.println("Hello " + firstName + " " + lastName);
        
        int num1 = Integer.parseInt(args[2]);
        int num2 = Integer.parseInt(args[3]);

        int sum = num1 + num2;

        System.out.println(num1 + " + " + num2 + " = " + sum);
    }
}

// Single Comment

/*
Multi line comment
*/

/**
 * Documentation comment
 */