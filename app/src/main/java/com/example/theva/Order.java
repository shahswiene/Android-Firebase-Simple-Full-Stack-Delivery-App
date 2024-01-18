package com.example.theva;
import java.io.Serializable;
import java.util.List;

public class Order implements Serializable{
    private String userId;
    private String address;
    private String orderDateTime;
    private String totalAmount;
    private String paymentMethod;
    private String status;
    private List<CartItem> items;

    public Order() {
    }

    public Order(String userId, String address, String orderDateTime, String totalAmount, String paymentMethod, String status, List<CartItem> items) {
        this.userId = userId;
        this.address = address;
        this.orderDateTime = orderDateTime;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.items = items;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getOrderDateTime() { return orderDateTime; }
    public void setOrderDateTime(String orderDateTime) { this.orderDateTime = orderDateTime; }
    public String getTotalAmount() { return totalAmount; }
    public void setTotalAmount(String totalAmount) { this.totalAmount = totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

}
