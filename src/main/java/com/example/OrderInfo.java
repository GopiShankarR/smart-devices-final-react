package com.example;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.List;

public class OrderInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int userId;
    private String username;
    private String confirmationNumber;
    private String deliveryDate;
    private String orderPlacedDate;
    private String deliveryOption;
    private String status;
    private List<ProductInfo> products = new ArrayList<>();

    public OrderInfo(int userId, String username, String confirmationNumber, String deliveryDate, String orderPlacedDate, String status, String deliveryOption) {
        this.userId = userId;
        this.username = username;
        this.confirmationNumber = confirmationNumber;
        this.deliveryDate = deliveryDate;
        this.orderPlacedDate = orderPlacedDate;
        this.status = status;
        this.deliveryOption = deliveryOption;
    }

    public int getUserId() {
      return userId;
    }

    public String getUsername() {
      return username;
    }

    public String getConfirmationNumber() {
      return confirmationNumber;
    }

    public String getDeliveryDate() {
      return deliveryDate;
    }

    public String getOrderPlacedDate() {
      return orderPlacedDate;
    }

    public String getStatus() {
      return status;
    }

    public String getDeliveryOption() {
      return deliveryOption;
    }

    public List<ProductInfo> getProducts() { 
      return products; 
    }

    public void addProduct(ProductInfo product) {
      this.products.add(product);
    }
}
