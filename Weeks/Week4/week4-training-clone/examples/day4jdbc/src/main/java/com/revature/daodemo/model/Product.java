package com.revature.daodemo.model;

public final class Product {
    private final long id;
    private final String sku;
    private final String name;
    private final double price;

    public Product(long id, String sku, String name, double price) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
