package com.revature.abstraction;

public interface Herbivore {

    int number=12;

            //As of Java 8, we can have Concrete Method within an Interface.
    default void eatPlant(){
        System.out.println("Eats Plant. Yum");
    }
}
