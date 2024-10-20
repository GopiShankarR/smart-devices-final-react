package com.example;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private int id;
    private String name;
    private double price;
    private String description;
    private String manufacturer;
    private String imageUrl;
    private String category;
    private int productQuantity;
    private boolean onSale;
    private boolean manufacturerRebate;
    private List<Accessory> accessories;

    public Product(int id, String name, double price, String description, String manufacturer, String imageUrl, String category, int productQuantity, boolean onSale, boolean manufacturerRebate, List<Accessory> accessories) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.manufacturer = manufacturer;
        this.imageUrl = imageUrl;
        this.category = category;
        this.productQuantity = productQuantity;
        this.onSale = onSale;
        this.manufacturerRebate = manufacturerRebate;
        this.accessories = accessories != null ? accessories : new ArrayList<>();
    }

    public Product(int id, String name, double price, String description, String manufacturer, String imageUrl, String category, int productQuantity, boolean onSale, boolean manufacturerRebate) {
        this(id, name, price, description, manufacturer, imageUrl, category, productQuantity, onSale, manufacturerRebate, new ArrayList<>()); 
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public boolean isOnSale() {
        return onSale;
    }

    public void setOnSale(boolean onSale) {
        this.onSale = onSale;
    }

    public boolean hasManufacturerRebate() {
        return manufacturerRebate;
    }

    public void setManufacturerRebate(boolean manufacturerRebate) {
        this.manufacturerRebate = manufacturerRebate;
    }

    public List<Accessory> getAccessories() {
        return accessories;
    }

    public void addAccessory(Accessory accessory) {
        this.accessories.add(accessory);
    }
}
