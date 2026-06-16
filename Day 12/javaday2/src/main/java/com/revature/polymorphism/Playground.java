package com.revature.polymorphism;

public class Playground {
    public static void main(String[] args) {
        Parent p = new Parent();
        p.setJobTitle("Trainer"); 
        p.work();

        // Parent cannot play
        // p.play();

        Child c = new Child();

        c.setFavoriteGames("Minecraft");
        c.setFavoriteGames("Student");
        c.work();
        c.play();

        // Will have all the states and behaviors available in the parent class, in the implementation provided by the child class
        Parent pc = new Child();
        pc.work(); // Uses implementation of methods in the child class
        pc.setJobTitle("ParentChild");

        // no play() since only fields and methods of parent class available
        // pc.play();

    }
}
