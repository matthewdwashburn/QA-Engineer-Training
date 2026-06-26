package com.revature;

import java.util.function.Function;

public class Student {
    private final String firstName;
    private final String lastName;
    public final String studentNumber;

    public Student(String firstName, String lastName, String studentNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentNumber = studentNumber;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getCommaName() {
        return lastName + ", " + firstName;
    }

    public static Function<Student, Boolean> f = f -> f.getFullName().equals( "John Smith") && f.studentNumber.equals("js123") ;
    
}
