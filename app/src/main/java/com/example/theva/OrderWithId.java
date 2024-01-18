package com.example.theva;

public class OrderWithId {
    private String orderId;
    private Order order;

    public OrderWithId(String orderId, Order order) {
        this.orderId = orderId;
        this.order = order;
    }

    public String getOrderId() {
        return orderId;
    }

    public Order getOrder() {
        return order;
    }
}
