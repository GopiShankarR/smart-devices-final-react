package com.example;

import java.io.Serializable;

public class Accessory {
    private String aid;
    private String name;
    private String imageURL;

    public Accessory(String aid, String name, String imageURL) {
        this.aid = aid;
        this.name = name;
        this.imageURL = imageURL;
    }

    public String getAid() {
        return aid;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }
}