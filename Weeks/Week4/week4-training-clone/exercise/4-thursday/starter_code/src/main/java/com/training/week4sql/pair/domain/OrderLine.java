package com.training.week4sql.pair.domain;

public final class OrderLine {
    private final long orderId;
    private final int lineNo;
    private final long productId;
    private final int qty;
    private final double unitPrice;

    public OrderLine(long orderId, int lineNo, long productId, int qty, double unitPrice) {
        this.orderId = orderId;
        this.lineNo = lineNo;
        this.productId = productId;
        this.qty = qty;
        this.unitPrice = unitPrice;
    }

    public long orderId() {
        return orderId;
    }

    public int lineNo() {
        return lineNo;
    }

    public long productId() {
        return productId;
    }

    public int qty() {
        return qty;
    }

    public double unitPrice() {
        return unitPrice;
    }
}
