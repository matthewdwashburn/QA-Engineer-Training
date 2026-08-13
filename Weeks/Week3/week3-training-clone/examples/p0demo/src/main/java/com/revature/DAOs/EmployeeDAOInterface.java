package com.revature.DAOs;

import com.revature.models.Employee;

import java.util.ArrayList;

public interface EmployeeDAOInterface {

    //here we will lay out functionalities that EmployeeDAO will implement

    //a method to select all employees
    ArrayList<Employee> getEmployees();
    //Why Arraylist? Get all will return multiple employees.
    //So we need something that can store multiple Employee objects are once

    //A method to insert a new employee
    Employee insertEmployee(Employee emp);
    //if we're sending an Employee, why return one back?
    //just so the User can see what they've inserted. Think of it as a confirmation

    //TODO: delete an employee
}
