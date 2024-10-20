package com.example;

public class ProductInventory {
    private String productName;
    private double price;
    private int totalQuantity;

    public ProductInventory(String productName, double price, int totalQuantity) {
        this.productName = productName;
        this.price = price;
        this.totalQuantity = totalQuantity;
    }

    // Getters
    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    // Setters (if needed)
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}
