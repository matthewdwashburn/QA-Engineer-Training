package com.revature.demojdbc;

import java.sql.*;

public class demo_prepared_statments {

    private static final String URL = "jdbc:sqlite:week4_jdbc_prep_demo.db";

    public static void main(String[] args) throws SQLException {
//        String userInput = "alice";
        //try this for example SQL Injection attack
        String userInput = "' OR '1'='1";


        //open database connection
        try(Connection conn = DriverManager.getConnection(URL)){
            try(Statement st = conn.createStatement()) {

                //drop old table if exists
                st.executeUpdate("DROP TABLE IF EXISTS user_account");

                //CREATE TABLE
                st.executeUpdate("""
                    CREATE TABLE user_account (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        secret TEXT NOT NULL
                    )
                """);

                //Insert Sample data
                st.executeUpdate("INSERT INTO user_account (name,secret) VALUES ('alice','pw1'),('bob','pw2')");
            }

            //EXAMPLE 1: UNSAFE STATEMENT

            System.out.println("unsafe concatenation");
            //User input is directly inserted into SQL text
            String unsafeSQL =
                    "SELECT name, secret from user_account WHERE name = '"+ userInput + "'";
            System.out.println("SQL: " + unsafeSQL);

            try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(unsafeSQL)){
                        while(rs.next()){
                            System.out.printf(
                                    "matched: %s / %s%n",
                                    rs.getString(1), //name column
                                    rs.getString(2) //secret column
                            );
                        }

            }
            String safeSQL = "Select name, secret from user_account WHERE name = ?";
            //? is a parameter placeholder
            System.out.println("Parameter binding(PrepareStatement)");
            try (PreparedStatement ps = conn.prepareStatement(safeSQL)){
                //Bind user input to parameter #1
                ps.setString(1,userInput);

                //ExecuteQuery
                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        System.out.printf(
                                "matched: %s / %s%n",
                                rs.getString(1), //name column
                                rs.getString(2) //secret column
                        );
                    }
                }
            }

        }

    }
}
