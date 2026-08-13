package com.training.week4sql.pair.domain;

public final class Product {
    private final long id;
    private final String sku;
    private final String name;
    private final double price;
    private final int stockQty;

    public Product(long id, String sku, String name, double price, int stockQty) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.stockQty = stockQty;
    }

    public long id() {
        return id;
    }

    public String sku() {
        return sku;
    }

    public String name() {
        return name;
    }

    public double price() {
        return price;
    }

    public int stockQty() {
        return stockQty;
    }

    @Override
    public String toString() {
        return "Product{id=" + id + ", sku=" + sku + ", name=" + name + ", price=" + price + ", stockQty=" + stockQty + "}";
    }
}
