package com.revature.VehicleExercise;

public interface AutonomousCapable {
    default boolean supportsSelfDrive() {
        return true;
    }
}
