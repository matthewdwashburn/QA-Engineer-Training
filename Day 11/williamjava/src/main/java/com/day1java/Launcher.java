package com.day1java;

import com.day1java.DemoClassesObjects.Student;

public class Launcher {
        public static void main(String[] args) {

        Student a = new Student("Ash");
        Student b = new Student("Ben");
        System.out.println(a);
        System.out.println(b);

        System.out.println("totalStudents (static):" + Student.getTotalStudents());
        Student c = new Student("Jake");
        System.out.println("Instance a==c?:" + a.equals(c));

    }

}
