package com.revature.daodemo.DAO;

import com.revature.daodemo.model.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

//DAO Interface
//Defines database operations without specifying
//how they are implemented
public interface ProductDAO {

    //CREATE
    long insert(Product product) throws SQLException;

    //READ
    Optional<Product> findBySku(String sku) throws Exception;

    //UPDATE
    void updatePrice(String sku, double newPrice) throws SQLException;

    //DELETE
    void deleteBySku(String sku) throws SQLException;

    // READ ALL
    List<Product> findAll() throws SQLException;

}
