package com.revature.abstraction;

public class Bear extends Animal implements Carnivore,Herbivore{
    @Override
    public void makeSound() {
        System.out.println("Grrrr");

    }

    @Override
    public void eatMeat() {
        System.out.println("Eats a fish");

    }

    @Override
    public void eatPlant() {
        Herbivore.super.eatPlant();
        System.out.println("Eating berries.");
    }
}
