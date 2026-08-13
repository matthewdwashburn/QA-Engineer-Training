package com.day1java;
import java.util.Scanner;


public class Conditionals {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter a number: ");
        int num = sc.nextInt();
        /*
        Conditional is a block of code that will execute on the basis of some condition
        Relational Operators -> ==, !=, >, <. >=, <=
        Logical Operators -> !, &, &&, |, ||, ^ (&& is short curcuit and, || is short circuit or, ^ is xor)
        Condition is any expression that evaluates to true or false
        */

        if(num > 10) {
            System.out.println("Our number is greater than 10");
        } else if (num > 5) {
            System.out.println("Our number is greater than 5 but less than 10");
        } else {
            System.out.println("Our number is not greater than 5");
        }

        // Ternary Operators are an alternative to if else, shorter
        System.out.println("Our number is " + performTernary(num));

        //Switch-cases
        //Switch-cases are best used in scenarios where a value being assessed has exact valued matches.

        switch (num){
            case 0: {
                System.out.println("Our number is 0");
                break;
            }
            case 1: {
                System.out.println("Our number is 1");
                break;
            }
            case 2: {
                System.out.println("Our number is 2");
                break;
            }
            case 3: {
                System.out.println("Our number is 3");
                break;
            }
            default:{
                System.out.println("Our number is something else");
                break;
            }
        }
        sc.close();

    }
    
    private static String performTernary(int num) {
        String result = (num > 5) ? "greater than 5":"less than or equal to 5";
        return result;
    }
    
}

