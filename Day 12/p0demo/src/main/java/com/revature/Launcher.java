package com.revature;

import com.revature.DAOs.AuthDAO;
import com.revature.DAOs.EmployeeDAO;
import com.revature.controllers.AuthController;
import com.revature.controllers.EmployeeController;
import com.revature.models.Employee;

import io.javalin.Javalin;

import java.util.ArrayList;

public class Launcher {
    public static void main(String[] args) {
        Employee e1 = new Employee("john", "smith");

        EmployeeDAO eDAO = new EmployeeDAO();

        // eDAO.insertEmployee(e1);

        ArrayList<Employee> employees = eDAO.getEmployees();

        for (Employee e : employees) {
            System.out.println(e);
        }

        EmployeeController ec = new EmployeeController();
        AuthController ac = new AuthController();

        AuthDAO ad = new AuthDAO();
        System.out.println("Login info: " + ad.login("john", "smith"));


        // Typical javalin object creation syntax
        var app = Javalin.create( config -> {
            config.routes.get("/hello", ctx -> ctx.result("Hello World"));
            config.routes.post("/login", ac.loginHandler);
            config.routes.get("/employees", ec.getEmployeeHandler);
            config.routes.post("/employees", ec.insertEmployee);
        }).start(3000);

    }

}