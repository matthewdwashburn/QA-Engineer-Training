package com.revature;
import java.util.Objects;

/**
 * Lab 2 — Student. Replace UnsupportedOperationException bodies with real logic.
 * See ../README.md
 */
public class Student {
    private final int id; 
    private static int nextId = 1;
    private static int totalStudents = 0;
    private String name; 
    private String program;

    public Student(String name, String program) {
        this.name = name;
        this.program = program;
        this.id = nextId++;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProgram() {
        return program;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public static int getEnrollmentCount() {
        return totalStudents;
    }

    @Override
    public String toString() {
        return "Student [id=" + id + ", name=" + name + ", program=" + program + "]";
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        Student student = (Student) o;
        return student.id == id && Objects.equals(name, student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
