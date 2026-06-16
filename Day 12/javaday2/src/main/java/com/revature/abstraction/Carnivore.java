package com.revature.abstraction;

public interface Carnivore {

    default void eatMeat() {
        System.out.println("Eating meat, yum");
    }
}
