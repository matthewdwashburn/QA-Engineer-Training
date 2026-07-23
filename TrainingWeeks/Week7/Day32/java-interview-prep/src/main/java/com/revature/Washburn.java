package com.revature;

public abstract class Washburn {
    public static int familyMemberCount;
    public Washburn() {
        familyMemberCount++;
    }

    public static int getFamilyMemberCount() {
        return familyMemberCount;
    }


    public abstract void sleep();

}
