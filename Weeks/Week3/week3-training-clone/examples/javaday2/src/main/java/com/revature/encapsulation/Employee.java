package com.revature.encapsulation;

import java.util.Objects;

public class Employee {

    private String username;
    private String password;
    private String email;
    private int age;

    public Employee() {
    }

    

    public Employee(String username, String email, int age) {
        this.username = username;
        this.email = email;
        this.age = age;
    }



    public Employee(String username, String password, String email, int age) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    String getPassword() {
        return password;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "age=" + age +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return age == employee.age && Objects.equals(username, employee.username) && Objects.equals(password, employee.password) && Objects.equals(email, employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email, age);
    }
}
