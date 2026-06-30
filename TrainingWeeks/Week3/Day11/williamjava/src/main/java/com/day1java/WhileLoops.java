package com.day1java;
import java.util.Scanner;

public class WhileLoops {
    public static void main(String[] args) {
    int count = 1;
    while(count < 100) {
        System.out.println(count); // print the value
        count++; // update the value
    }

    boolean isSunny = true;
    
    while(isSunny) {
        System.out.println("I'm glad its sunny outside");
        isSunny = !isSunny;
    }

    // While loops are best used in situations where the number of iterations is unknown
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter a number:");
    String input = sc.nextLine();
    int number = Integer.parseInt(input);

    while(number != 5) {
        System.out.println("You did not enter #5. Please try again.");
        input = sc.nextLine();
        number = Integer.parseInt(input);
    }

    System.out.println("You finally did it! You entered 5!");
    //Do-While Loop, even if the condition is false, you will at least get 1 iteration, runs first checks later
    count = 100;
    do {
        System.out.println("Something to print");
    } while(count <= 10);

    // Base class Object from which all classes inherit
    Object obj = new Object();
    System.out.println(obj.toString());
    sc.close();
    }

}
