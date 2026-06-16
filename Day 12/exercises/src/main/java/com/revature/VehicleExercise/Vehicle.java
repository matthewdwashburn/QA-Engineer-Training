package com.revature.VehicleExercise;

public abstract class Vehicle {
    String make;
    int modelYear;
    double gasCostPerGallon = 5;

    public abstract double fuelCostPer100m();

}