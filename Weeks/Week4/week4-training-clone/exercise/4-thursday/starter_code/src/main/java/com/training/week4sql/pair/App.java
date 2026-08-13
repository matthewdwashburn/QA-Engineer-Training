package com.training.week4sql.pair;

import com.training.week4sql.pair.dao.JdbcOrderDao;
import com.training.week4sql.pair.dao.JdbcProductDao;
import com.training.week4sql.pair.dao.OrderDao;
import com.training.week4sql.pair.dao.ProductDao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Pair exercise entry point. TODO: wire ProductDao + OrderDao flows per pair_programming_jdbc.md
 */
public final class App {

    private static final String URL = "jdbc:sqlite:pair_store.db";

    public static void main(String[] args) throws Exception {
        try (Connection conn = DriverManager.getConnection(URL)) {
            bootstrap(conn);
            ProductDao products = new JdbcProductDao(conn);
            OrderDao orders = new JdbcOrderDao(conn);

            System.out.println("Products: " + products.findAll());
            System.out.println("TODO: implement insert / order flow — see pair_programming_jdbc.md");
        }
    }

    private static void bootstrap(Connection conn) throws Exception {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("PRAGMA foreign_keys = ON");
            st.executeUpdate("DROP TABLE IF EXISTS order_line");
            st.executeUpdate("DROP TABLE IF EXISTS customer_order");
            st.executeUpdate("DROP TABLE IF EXISTS product");
            st.executeUpdate(
                    """
                    CREATE TABLE product (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        sku TEXT NOT NULL UNIQUE,
                        name TEXT NOT NULL,
                        price REAL NOT NULL CHECK (price >= 0),
                        stock_qty INTEGER NOT NULL CHECK (stock_qty >= 0)
                    )""");
            st.executeUpdate(
                    """
                    CREATE TABLE customer_order (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        customer_email TEXT NOT NULL,
                        status TEXT NOT NULL CHECK (status IN ('OPEN','PAID'))
                    )""");
            st.executeUpdate(
                    """
                    CREATE TABLE order_line (
                        order_id INTEGER NOT NULL REFERENCES customer_order(id) ON DELETE CASCADE,
                        line_no INTEGER NOT NULL,
                        product_id INTEGER NOT NULL REFERENCES product(id),
                        qty INTEGER NOT NULL CHECK (qty > 0),
                        unit_price REAL NOT NULL CHECK (unit_price >= 0),
                        PRIMARY KEY (order_id, line_no)
                    )""");
        }
    }
}
