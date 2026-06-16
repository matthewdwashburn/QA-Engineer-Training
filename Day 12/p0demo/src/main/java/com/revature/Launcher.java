package com.revature;

import com.revature.DAOs.EmployeeDAO;
import com.revature.models.Employee;
import java.util.ArrayList;

public class Launcher {
    public static void main(String[] args) {
        Employee e1 = new Employee("john", "smith");
        System.out.println(e1);
    }

    EmployeeDAO eDAO = new EmployeeDAO();

    ArrayList<Employee> employees = eDAO.getEmployees();
}