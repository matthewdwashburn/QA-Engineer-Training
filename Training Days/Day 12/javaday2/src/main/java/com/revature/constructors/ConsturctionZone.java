package com.revature.constructors;

public class ConsturctionZone {
    public static void main(String[] args) {
        Building b1 = new Building();
        Building b2 = new Building(1000);
        
        System.out.println(b1.walls);
        System.out.println(b1.area);
        System.out.println(b1);
        System.out.println(b2);
    }
}
