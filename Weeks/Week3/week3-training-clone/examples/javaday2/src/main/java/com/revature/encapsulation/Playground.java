package com.revature.encapsulation;


public class Playground {

    public static void main(String[] args) {
        Employee Bob = new Employee();

        Bob.setUsername("bobby123");
        String bob_username = Bob.getUsername();
        System.out.println(bob_username);

        Bob.setPassword("notsecure");
        String bob_password = Bob.getPassword();
        System.out.println(bob_password);

    }


}
