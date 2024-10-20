package com.example;

public class DailySalesData {
    private String date;
    private double totalSales;

    public DailySalesData(String date, double totalSales) {
        this.date = date;
        this.totalSales = totalSales;
    }

    public String getDate() {
        return date;
    }

    public double getTotalSales() {
        return totalSales;
    }
}
