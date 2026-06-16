package com.revature.encapsulation2;

import com.revature.encapsulation.Employee;

public class Playground2 {

    public static void main(String[] args) {
        Employee Bob = new Employee();

        Bob.setUsername("Bobby123");
        String bob_username = Bob.getUsername();
        System.out.println(bob_username);

        // Set password is protected access modifier, doesn't work
        // Bob.setPassword(bob_username);

        // Protected can be invoked by a child class
        DemoEmployee Alice = new DemoEmployee();
        Alice.updatePassword("Alice123");
        

    }


    
}
