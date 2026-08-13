package com.revature.abstraction;

public abstract class Animal {

    String species;

    //Concrete method because it has an implementation
    public void eat() {
        System.out.println("The animal is eating something.");
    }

    //Abstract method -> anythni that inherits Animal will be required to implement this method.
    public abstract void makeSound();

}
