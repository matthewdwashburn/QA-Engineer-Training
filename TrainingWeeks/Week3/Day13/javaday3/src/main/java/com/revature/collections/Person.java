package com.revature.collections;

public class Person {
    private String name;
    private int height;
    private int age;

    public Person() {
    }

    public Person(String name, int height, int age) {
        this.name = name;
        this.height = height;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person [name=" + name + ", height=" + height + ", age=" + age + "]";
    }

    
    
}
