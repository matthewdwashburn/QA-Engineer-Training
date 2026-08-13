package com.revature;

/** Lab 2 driver — run after Student is implemented. */
public class StudentDemo {
    public static void main(String[] args) {

        Student s1 = new Student("Bob", "CS");
        Student s2 = new Student("Joe", "Premed");
        Student s3 = new Student("Billy", "Art");

        // Different
        System.out.println(s1.equals(s2));
        System.out.println(s1 == s2);

        // Same
        System.out.println(s1.equals(s1));

        System.out.println(s3);
        System.out.println(Student.getEnrollmentCount());
    }
}
