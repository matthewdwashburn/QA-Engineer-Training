package com.revature.DAOs;
import java.util.ArrayList;
import com.revature.models.Employee;

public interface EmployeeDAOInterface {
    // Here we will lay out functionalities that EmployeeDAO will implement

    // A method to select all employees
    ArrayList<Employee> getEmployees();

    // A method to insert a new employee
    Employee insertEmployee(Employee employee);

    // If were sending an Employee, why return one back?
    // Just so the User can see what they've inserted. Think of it as a confirmation.
    
} 


