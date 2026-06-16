package com.revature.abstraction;

public class Bear extends Animal implements Carnivore, Herbivore {

    @Override
    public void makeSound() {
        System.out.println("Grrrr");
    }

    @Override
    public void eatMeat() {
        System.out.println("Eat Fish");
    }
    
    @Override
    public void eatPlants() {
        Herbivore.super.eatPlants();
        System.out.println("Eating Berries");
    }
}
