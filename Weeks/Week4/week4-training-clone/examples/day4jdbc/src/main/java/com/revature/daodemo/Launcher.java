package com.revature.daodemo;

import com.revature.daodemo.DAO.JdbcProductDAO;
import com.revature.daodemo.model.Product;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class Launcher {
    public static void main(String[] args) throws SQLException
            {
        String url = "jdbc:sqlite:week4_jdbc_dao_demo.db";

        try (Connection conn = DriverManager.getConnection(url)) {

            //open database connection
            try (Statement st = conn.createStatement()) {

                st.executeUpdate("DROP TABLE IF EXISTS product");

                st.executeUpdate("""
                    CREATE TABLE product (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        sku TEXT NOT NULL UNIQUE,
                        name TEXT NOT NULL,
                        price REAL NOT NULL
                    )

                    """);
            }
            JdbcProductDAO dao = new JdbcProductDAO(conn);

            Product p = new Product(0,"SKU-1","Mug",12.5);
            Product p2 = new Product(1,"SKU-2","Not a Mug",13.5);

            long id = dao.insert(p);
            System.out.println("inserted id=" + id);
            long id2 = dao.insert(p2);
            System.out.println("inserted id=" + id2);

            List<Product> products = dao.findAll();

            for(Product p1: products){
                System.out.println(p1);
            }

            Optional<Product> loaded = dao.findBySku("SKU-1");
            loaded.ifPresent(System.out::println);

            dao.updatePrice("SKU-1",11.99);
            dao.deleteBySku("SKU-1");
            System.out.println("after delete " + dao.findAll().size() + " rows");



        } catch (Exception e) {
            throw new RuntimeException(e);
        }
            }
}
