package com.revature.abstraction;

public abstract class Animal {
    String species;

    public void eat() {
        System.out.println("The animal is eating something");
    }

    public abstract void makeSound();

}
