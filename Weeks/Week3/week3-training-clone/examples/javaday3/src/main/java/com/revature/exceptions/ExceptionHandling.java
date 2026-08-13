package com.revature.exceptions;

import java.util.Scanner;

public class ExceptionHandling {

    public static void main(String[] args) {
        printDivision();
    }

    private static void printDivision(){

        Scanner scan = new Scanner(System.in);

        int x = scan.nextInt();
        int y = scan.nextInt();

        try {
            int result = x/y;
            System.out.println(result);

        } catch (ArithmeticException e) {
            System.out.println("Oops! Something went wrong!");
            e.printStackTrace();

        } catch (RuntimeException r) {
            System.out.println("run time exception");
            r.printStackTrace();


        } finally {
            System.out.println( "In finally block");
            scan.close();
        }

        System.out.println("End of program");
    }
}
