package com.example;

public class SalesData {
    private String productName;
    private double productPrice;
    private int itemsSold;
    private double totalSales;

    public SalesData(String productName, double productPrice, int itemsSold, double totalSales) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.itemsSold = itemsSold;
        this.totalSales = totalSales;
    }

    public String getProductName() {
        return productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public int getItemsSold() {
        return itemsSold;
    }

    public double getTotalSales() {
        return totalSales;
    }
}
