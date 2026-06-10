package com.revature.constructors;

public class PersonManager {
    public static void main(String[] args) {
        Person person = new Person("Jake", 15);

        System.err.println("Person Name: " + person.getName());
        System.err.println("Person Age: " + person.getAge());

    }
}
