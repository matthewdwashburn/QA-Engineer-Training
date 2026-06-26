package com.revature.methods;

public class DemoMethods {
    public static void main(String[] args) {
        System.out.println("Sum = " + sum(2,3));
        System.out.println("Sum = " + DemoMethods.sum(2, 3));
        System.out.println(DemoMethods.sayHello("Jake"));


        // DemoMethods demoMethods = new DemoMethods();
        

    }


    // var args
    public static int sum(int ...numbers) {
        int total = 0;
        for(int num:numbers){
            total+=num;
        }
        return total;
    }

    public static String sayHello(String name) {
        return "Hello, " + name + "!";
    }

}
