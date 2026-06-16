package com.revature.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {
    // This method will eventually return an object of type Connection
    // ...which we'll use to interact with our database
    public static Connection getConnection() throws SQLException{
        //first we need to register our SQLite driver
        //this process makes the application aware of what SQL flavor we're using
        try {
            Class.forName("org.sqlite.JDBC");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Problem occurred locating driver");
        }

        //This string
        String url = "jdbc:sqlite:/Users/matt/Documents/GitHub/QA-Engineer-Training/Day 12/p0demo/p0demo.db";

        // This return statement is what returns our actual db connection object
        return DriverManager.getConnection(url);
    }
}
