package com.revature.encapsulation2;

import com.revature.encapsulation.Employee;

public class Playground2 {
    public static void main(String[] args) {
        DemoEmployee Alice = new DemoEmployee();

        Alice.setUsername("alice123");
        String alice_username = Alice.getUsername();
        System.out.println(alice_username);

        //setPassword is default access modifier so can't do below
        Alice.updatePassword("notsecure");


    }
}
