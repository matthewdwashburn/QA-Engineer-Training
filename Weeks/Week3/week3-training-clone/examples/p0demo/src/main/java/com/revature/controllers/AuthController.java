package com.revature.controllers;

import com.revature.DAOs.AuthDAO;
import com.revature.models.Employee;
import com.revature.models.LoginDTO;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import jakarta.servlet.http.HttpSession;

public class AuthController {

    //AuthDAO so we can its methods
    AuthDAO aDAO = new AuthDAO();

    //empty HttpSession object that will be filled upon successful login
    public static HttpSession ses;
    //to prevent functionalities from running until login, have them check whether this Session is null;

    //login will be a POST request, since the user is expected to send some data in the HTTP Request
    public Handler loginHandler = (ctx) -> {

        //ctx.bodyAsClass() to grab the HTTP request data and convert it into a LoginDTO object
        LoginDTO lDTO  =ctx.bodyAsClass(LoginDTO.class);

        //if login successful, this Employee object will be populated, otherwise, null
        Employee loggedInEmployee = aDAO.login(lDTO.getFirst_name(),lDTO.getLast_name());
        System.out.println(loggedInEmployee);

        if(loggedInEmployee!=null){

            //this is how we create a session
            ses= ctx.req().getSession();

            //we can use setAttribute() to set certain values to certain keys
            //THIS IS HOW WE CAN SAVE DATA IN A SESSION
            ses.setAttribute("employee_id",loggedInEmployee.getEmployee_id());

            ctx.json(loggedInEmployee);
            ctx.status(HttpStatus.ACCEPTED);
        } else {
            ctx.status(HttpStatus.UNAUTHORIZED);
        }

    };



}
