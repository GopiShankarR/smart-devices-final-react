package com.example;

public class Store {
    private int storeId;
    private String storeName;
    private String street;
    private String city;
    private String state;
    private String zip_code;

    public Store(int storeId, String storeName, String street, String city, String state, String zip_code) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip_code = zip_code;
    }

    public int getStoreId() {
        return storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zip_code;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZipCode(String zip_code) {
        this.zip_code = zip_code;
    }

    @Override
    public String toString() {
        return storeId + " - " + storeName + " - " + street + ", " + city + ", " + state + " " + zip_code;
    }
}
