package com.revature.exceptions;
import java.util.Scanner;

public class ExceptionHandling {
    public static void main(String[] args) {
        printDivision();
    }

    public static void printDivision() {
        Scanner scan = new Scanner(System.in);

        System.out.println("Give first number");
        int x = scan.nextInt();
        System.out.println("Give second number");
        int y = scan.nextInt();

        try {
            int result = x/y;
            System.out.println(result);
        } catch (ArithmeticException e) {
            System.out.println("Arithmatic exception!");
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.out.println("Oops! Something went wrong!");
        } finally {
            System.out.println("Finally...");
            scan.close();
        }
        System.out.println("End of program");
    }
}