package com.revature;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.PriorityQueue;
import java.util.Queue;

class Student implements Comparable<Student> {
    private int id;
    private String name;
    private double cgpa;

    public Student(int id, String name, double cgpa) {
        this.id = id;
        this.name = name;
        this.cgpa = cgpa;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getCGPA() {
        return cgpa;
    }

    /*
     * Highest number is highest priority, instead of normal
     * highest priority = 1, put this object second for cgpa, but first
     * for name and id, the lower value wins
     */
    @Override
    public int compareTo(Student s) {
        if (s.getCGPA() != this.getCGPA()) {
            // Descending
            return Double.compare(s.getCGPA(), this.getCGPA());
        } else if (!s.getName().equals(this.getName())) {
            // Ascending
            return this.getName().compareTo(s.getName());
        } else {
            // Ascending
            return Integer.compare(this.getID(), s.getID());
        }
    }

    @Override
    public String toString() {
        return "id: " + id + ", name: " + name + ", cgpa: " + cgpa;
    }
}

class Priorities {
    public List<Student> getStudents(List<String> events) {
        Queue<Student> students = new PriorityQueue<>();
        for (int i = 0; i < events.size(); i++) {
            String[] splitEvent = events.get(i).split(" ");
            String event = splitEvent[0];
            if (event.equals("ENTER")) {
                String name = splitEvent[1];
                Double cgpa = Double.parseDouble(splitEvent[2]);
                int id = Integer.parseInt(splitEvent[3]);
                Student currStudent = new Student(id, name, cgpa);
                students.add(currStudent);
            } else {
                students.poll();
            }
        }
        List<Student> studentsList = new ArrayList<>();
        while (!students.isEmpty()) {
            studentsList.add(students.poll());
        }

        return studentsList;
    }
}

public class EventOrganizer {
    private final static Scanner scan = new Scanner(System.in);
    private final static Priorities priorities = new Priorities();

    public static void main(String[] args) {
        int totalEvents = Integer.parseInt(scan.nextLine());
        List<String> events = new ArrayList<>();

        while (totalEvents-- != 0) {
            String event = scan.nextLine();
            events.add(event);
        }

        List<Student> students = priorities.getStudents(events);

        if (students.isEmpty()) {
            System.out.println("EMPTY");
        } else {
            for (Student st : students) {
                System.out.println(st.getName());
            }
        }
    }
}


/*
 * Sample Input 0
 * 
 * 12
 * ENTER John 3.75 50
 * ENTER Mark 3.8 24
 * ENTER Shafaet 3.7 35
 * SERVED
 * SERVED
 * ENTER Samiha 3.85 36
 * SERVED
 * ENTER Ashley 3.9 42
 * ENTER Maria 3.6 46
 * ENTER Anik 3.95 49
 * ENTER Dan 3.95 50
 * SERVED
 * 
 * Sample Output 0
 * 
 * Dan
 * Ashley
 * Shafaet
 * Maria
 * 
 */