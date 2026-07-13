package com.revature.controllers;

import com.revature.DAOs.AuthDAO;
import com.revature.models.Employee;
import com.revature.models.LoginDTO;

import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import jakarta.servlet.http.HttpSession;

public class AuthController {
    //AuthDAO so we can use its methods
    AuthDAO aDAO = new AuthDAO();

    //empty HttpSession object that will be filled upon successful login
    public static HttpSession sesh;

    //to prevent functionalities from from running until login, have them check whether this Session is null;

    //login will be a POST request, since the user is expected to send some data in the HTTP Request
    public Handler loginHandler = (ctx -> {
        //grabs the HTTP request body and converts it into a login DTO object
        LoginDTO lDTO = ctx.bodyAsClass(LoginDTO.class);

        //If login successful, this Employee object will be populated, otherwise, null
        Employee loggedInEmployee = aDAO.login(lDTO.getFirst_name(), lDTO.getLast_name());
        System.out.println(loggedInEmployee);

        if(loggedInEmployee != null) {
            //This is how we create a session
            sesh = ctx.req().getSession();

            //we can use setAttribute() to set certain values to certain keys
            //THIS IS HOW WE CAN SAVE DATA IN A SESSION
            sesh.setAttribute("employee_id", loggedInEmployee.getEmployee_id());

            ctx.json(loggedInEmployee);
            ctx.status(HttpStatus.ACCEPTED);
        } else {
            ctx.status(HttpStatus.UNAUTHORIZED);
        }
    });
}
