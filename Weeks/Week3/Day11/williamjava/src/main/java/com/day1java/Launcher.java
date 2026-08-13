package com.day1java;

import java.util.stream.IntStream;

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

        System.out.println("Fibbonaci Example");
        int n = 6;
        int[] arr = IntStream.rangeClosed(1, n).toArray();
        for(int number: arr) {
            System.out.println(MethodRecursion.fib(number));
        }

    }

}
