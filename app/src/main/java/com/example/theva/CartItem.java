package com.example.theva;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String productName;
    private String price;
    private int quantity;
    private String imageUrl;

    // Default constructor is required for Firebase deserialization
    public CartItem() {
    }

    public CartItem(String productName, String price, int quantity, String imageUrl) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
