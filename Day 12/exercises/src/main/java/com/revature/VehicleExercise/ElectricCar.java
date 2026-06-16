package com.revature.VehicleExercise;

public class ElectricCar extends Vehicle implements AutonomousCapable {

    int milesPerGallon = 40;

    public ElectricCar(String make, int modelYear) {
        this.make = make;
        this.modelYear = modelYear;
    }

    @Override
    public double fuelCostPer100m() {
        double result = gasCostPerGallon * (100.0 / milesPerGallon);
        return result;
    }

    @Override
    public boolean supportsSelfDrive() {
        return true;
    }

    @Override
    public String toString() {
        return "ElectricCar [make=" + make + ", modelYear=" + modelYear + ", gasCostPerGallon=" + gasCostPerGallon
                + ", milesPerGallon=" + milesPerGallon + ", fuelCostPer100m()=" + fuelCostPer100m()
                + ", supportsSelfDrive()=" + supportsSelfDrive() + "]";
    }

}
