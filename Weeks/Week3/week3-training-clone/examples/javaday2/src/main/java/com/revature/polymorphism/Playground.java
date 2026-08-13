package com.revature.polymorphism;

public class Playground {

    public static void main(String[] args) {


        Parent p = new Parent();
        p.jobtitle = "Trainer";
        p.work();

        //not available method to parent
        //p.play();

        Child c = new Child();

        c.favoritegames = "Minecraft";
        c.jobtitle = "Student";
        c.work();
        c.play();



        //will have all of the states(fields) and behvaiors(methods) available in the Parent Class.
        //will have the implementations provided by the Child class;
        Parent pc = new Child();
        pc.work();
        pc.jobtitle = "something";

        //no play() since only fields and methods of Parent class available
        //pc.play();

        Parent p0 = (Parent) c; //this os okay because the child variable 'c' has everything needed to make a Parent Object

//        Child c0 = (Child) p; //this is not okay because the Parent variable 'p' does not have everything needed to make a Child Object

        Child c1 = (Child) pc;

        c1.work();
        c1.play();
        System.out.println(c1.jobtitle);










    }

}
