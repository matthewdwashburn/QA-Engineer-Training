package com.revature.controllers;

import java.util.ArrayList;

import com.revature.DAOs.EmployeeDAO;
import com.revature.models.Employee;

import io.javalin.http.Handler;

/* 
The controller is where HTTP requests get sent after Javalin directs them
It's the layer that JSON comes in and gets translated to Java and vice versa
We'll either be getting data from the service or DAO to get an HTTP Response to 
the front end (select) OR we'll be recieving data from the front end to send to the database
(Insert, update, delete)
*/

public class EmployeeController {
    EmployeeDAO eDAO = new EmployeeDAO();

    public Handler getEmployeeHandler = (ctx -> {
        /* Ctx is the context object. This object contains methods that we can use to process HTTP requests
        and send HTTP responses. Here, we are giving it a variable called "ctx" so that we can access its methods.
        */

        // We need an ArrayList of Employees, courtesy of our EmployeeDAO
        ArrayList<Employee> employees = eDAO.getEmployees();

        //Use the .json() method to turn our dava into a JSON string
        ctx.json(employees);
        
        // we can set the status code with ctx.status()
        ctx.status(200); // 200 is the default "OK message"
    });

    public Handler insertEmployee = (ctx -> {
        //we have JSON data coming in, which we can convert to java with ctx.bodyAsClass();
        //What's body?? It refers to the HTTP request body (the data sent in the HTTP Request)
        Employee newEmp = ctx.bodyAsClass(Employee.class); // we now have a Java string holding a JSON String

        Employee returnedEmp = eDAO.insertEmployee(newEmp);

        //If insert was successful, returnedEmp will hold an Employee object (not null)
        if(returnedEmp != null){
            ctx.status(201);
            ctx.json(returnedEmp); // Send back the employee
        } else {
            ctx.status(406); //406 not acceptable
            ctx.result("Insert employee failed");
        }
    });
}
