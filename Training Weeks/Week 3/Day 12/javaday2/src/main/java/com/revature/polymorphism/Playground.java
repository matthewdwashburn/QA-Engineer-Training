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
        pc.setJobTitle("ParentChild Job");

        // Bad since only fields and methods of parent class available
        // pc.play();

        // Cast parent class to child object
        Parent p0 = (Parent) c; // This is ok because the child's variable has everything needed to make a Parent, liskov substitution
        p0.work();

        // Child c0 = (Child) p; // Not ok because parent variable doesn't have everythin needed to make a child

        // Casted parent child as child
        Child c1 = (Child) pc;
        c1.work();
        c1.play();

        System.out.println(c1.jobTitle);



    }
}
