package com.revature;

import com.revature.DAOs.AuthDAO;
import com.revature.DAOs.EmployeeDAO;
import com.revature.controllers.AuthController;
import com.revature.controllers.EmployeeController;
import com.revature.models.Employee;
import io.javalin.Javalin;

import java.util.ArrayList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Launcher {
    public static void main(String[] args) {

        Employee e1 = new Employee("john","smith",1);
        System.out.println(e1);

        EmployeeDAO eDAO = new EmployeeDAO();

        eDAO.insertEmployee(e1);

        ArrayList<Employee> employees =  eDAO.getEmployees();

        for(Employee e: employees){
            System.out.println(e);
        }

        EmployeeController ec = new EmployeeController();
        AuthController ac = new AuthController();

        AuthDAO ad = new AuthDAO();
        System.out.println(ad.login("john","smith"));

        //Typical Javalin object creation syntax
        Javalin.create( config -> {
            config.routes.get("hello",ctx -> ctx.result("Hello World"));
            config.routes.post("/login", ac.loginHandler);
            config.routes.get("/employees",ec.getEmployeesHandler);
            config.routes.post("/employees",ec.insertEmployee);

        }).start(3000);





    }
}