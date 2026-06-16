package com.revature.abstraction;

public interface Herbivore {

    default void eatPlants() {
        System.out.println("Eating plants, yum");
    }
    
} 
