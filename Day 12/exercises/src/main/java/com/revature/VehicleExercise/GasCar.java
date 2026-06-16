package com.revature.VehicleExercise;

public class GasCar extends Vehicle implements AutonomousCapable {
    int milesPerGallon = 20;

    public GasCar(String make, int modelYear) {
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
        return false;
    }

    @Override
    public String toString() {
        return "GasCar [make=" + make + ", modelYear=" + modelYear + ", milesPerGallon=" + milesPerGallon
                + ", gasCostPerGallon=" + gasCostPerGallon + ", fuelCostPer100m()=" + fuelCostPer100m()
                + ", supportsSelfDrive()=" + supportsSelfDrive() + "]";
    }

}
