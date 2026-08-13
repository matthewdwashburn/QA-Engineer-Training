package com.training.week4sql.pair.dao;

import com.training.week4sql.pair.domain.Product;
import java.util.List;
import java.util.Optional;

public interface ProductDao {
    long insert(Product product) throws Exception;

    Optional<Product> findBySku(String sku) throws Exception;

    List<Product> findAll() throws Exception;

    void updateStock(String sku, int newQty) throws Exception;

    void deleteBySku(String sku) throws Exception;
}
