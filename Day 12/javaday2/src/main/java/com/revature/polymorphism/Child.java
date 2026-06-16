package com.revature.polymorphism;

public class Child extends Parent {
    String favoriteGames;

    @Override
    public void work() {
        System.out.println("Did homework for school");
    }

    public void play() {
        System.out.println("Playing on the playground");
    }

    @Override
    public Child someMethod(){
        return new Child();
    }
}
