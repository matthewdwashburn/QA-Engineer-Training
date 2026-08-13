package com.training.week4sql.pair.domain;

public final class CustomerOrder {
    private final long id;
    private final String customerEmail;
    private final String status;

    public CustomerOrder(long id, String customerEmail, String status) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.status = status;
    }

    public long id() {
        return id;
    }

    public String customerEmail() {
        return customerEmail;
    }

    public String status() {
        return status;
    }

    @Override
    public String toString() {
        return "CustomerOrder{id=" + id + ", customerEmail=" + customerEmail + ", status=" + status + "}";
    }
}
