package com.example;

public class ProductInfo {
    private int productId;
    private String productName;
    private String productCategory;
    private double productPrice;
    private String productManufacturer;

    // Constructor with all fields
    public ProductInfo(int productId, String productName, String productCategory, double productPrice, String productManufacturer) {
        this.productId = productId;
        this.productName = productName;
        this.productCategory = productCategory;
        this.productPrice = productPrice;
        this.productManufacturer = productManufacturer;
    }

    // Getters
    public int getProductId() { 
      return productId; 
    }

    public String getProductName() { 
      return productName; 
    }

    public String getProductCategory() {
      return productCategory;
    }

    public double getProductPrice() {
      return productPrice;
    }

    public String getProductManufacturer() {
      return productManufacturer;
    }

    // Setters
    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductManufacturer(String productManufacturer) {
        this.productManufacturer = productManufacturer;
    }
}
