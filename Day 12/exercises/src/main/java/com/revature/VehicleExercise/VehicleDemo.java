package com.revature.VehicleExercise;

import java.util.ArrayList;

public class VehicleDemo {
    public static void main(String[] args) {

        ArrayList<Vehicle> vehicles = new ArrayList<>();

        ElectricCar e1 = new ElectricCar("Tesla 1", 2026);
        vehicles.add(e1);
        ElectricCar e2 = new ElectricCar("Tesla 2", 2026);
        vehicles.add(e2);
        ElectricCar e3 = new ElectricCar("Tesla 3", 2026);
        vehicles.add(e3);

        GasCar g1 = new GasCar("Ford 1", 2010);
        vehicles.add(g1);
        GasCar g2 = new GasCar("Ford 2", 2010);
        vehicles.add(g2);
        GasCar g3 = new GasCar("Ford 3", 2010);
        vehicles.add(g3);

        for(Vehicle v : vehicles) {
            System.out.println("Cost per 100m: " + v.fuelCostPer100m());
            System.out.println(v);
        }


    }
}