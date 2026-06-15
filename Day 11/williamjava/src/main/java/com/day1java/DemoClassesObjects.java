package com.day1java;

import java.util.Objects;
import java.util.Arrays;

class DemoClassesObjects {

    static class Student {
        private static int nextId = 1;
        private static int totalStudents = 0;

        private final int id;
        private String name;

        static {
            System.out.println(" [static block] Student class loaded");
        }

        Student(String name) {
            this.id = nextId++;
            this.name = name;
            totalStudents++;
        }

        public static int getTotalStudents() {
            return totalStudents;
        }

        public static void setTotalStudents(int totalStudents) {
            Student.totalStudents = totalStudents;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass())
                return false;
            Student student = (Student) o;
            return id == student.id && Objects.equals(name, student.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }

        public static void main(String[] args) {
            int[] scores = {72, 31, 67, 23, 88};

            for(int i=0; i<scores.length; i++) {
                System.out.println("index " + i  + " => " + scores[i]);
            }
        

        int[][] grid = {
            {1, 2},
            {4, 5, 6},
            {6, 7, 8, 9}
        }; 

        for(int r = 0; r < grid.length; r++) {
            System.out.println("Row " + r + ": " + Arrays.toString(grid[r]));
        }

        int[] copy = Arrays.copyOf(scores, scores.length);

        Arrays.sort(copy);

        System.out.println("sorted: " + Arrays.toString(copy));
        System.out.println("binarysearch 88: " + Arrays.binarySearch(copy, 88));

        }

    }
}
