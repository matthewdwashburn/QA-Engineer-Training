package com.revature;

import com.revature.DAOs.EmployeeDAO;
import com.revature.models.Employee;
import java.util.ArrayList;

public class Launcher {
    public static void main(String[] args) {
        Employee e1 = new Employee("john", "smith");

        EmployeeDAO eDAO = new EmployeeDAO();

        eDAO.insertEmployee(e1);

        ArrayList<Employee> employees = eDAO.getEmployees();

        for (Employee e : employees) {
            System.out.println(e);
        }
    }

}