package com.revature.demojdbc;

import java.sql.*;

public class demo_jdbc_connection {

    //JDBC conection string.
    //SQLite will create a database file if it does not exist
    private static final String URL = "jdbc:sqlite:week4_jdbc_demo.db";

    public static void main(String[] args) throws SQLException {
        //Open a database connection
        //try-with-resources automatically closes the connection
        try (Connection conn = DriverManager.getConnection(URL)) {

            //Create a fresh customer table
            bootstrapSchema(conn);

            //INSERT DATA

            //Create a parameterized SQL statement
            //? placeholders will be replaced with values safely

            try (PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO customer (email, name) VALUES (?,?)")) {

                // Set values for the placeholders
                ins.setString(1,"jdbc@example.com");
                ins.setString(2,"JDBC Explorer");

                //Execute INSERT statement

                ins.executeUpdate();
            }

            //Create a parameterized SELECT statement
            try(PreparedStatement q = conn.prepareStatement(
                    "SELECT id, email, name FROM customer WHERE email = ?")){

                //Set the email parameter
                q.setString(1,"jdbc@example.com");

                //Execute query to obtain a ResultSet
                try (ResultSet rs = q.executeQuery()){

                    //Move through each row in the result set.
                    while(rs.next()){

                        //Read column values from current row
                        long id = rs.getLong("id");
                        String email = rs.getString("email");
                        String name = rs.getString("name");

                        //Print row contents.
                        System.out.printf("row: id=%d email=%s name=%s%n", id, email, name);
                    }
                }

            }


        }
    }

    private static void bootstrapSchema(Connection conn) throws SQLException {
        //Statement is typically used for static SQL
        try( Statement st = conn.createStatement()){

            //Remove table if already exists
            st.executeUpdate("DROP TABLE IF EXISTS customer");

            //Create a new customer table.
            st.executeUpdate("""
                CREATE TABLE customer (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    email TEXT NOT NULL UNIQUE,
                    name TEXT NOT NULL
                )""");
        }
    }

}
