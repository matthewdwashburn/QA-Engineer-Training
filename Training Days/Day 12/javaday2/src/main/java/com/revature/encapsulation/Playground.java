package com.revature.encapsulation;

public class Playground {

    public static void main(String[] args) {
        Employee Bob = new Employee();

        Bob.setUsername("Bobby123");
        String bob_username = Bob.getUsername();
        System.out.println(bob_username);
    }


    
}
