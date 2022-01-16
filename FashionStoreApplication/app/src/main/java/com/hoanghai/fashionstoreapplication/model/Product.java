package com.hoanghai.fashionstoreapplication.model;

import java.io.Serializable;

public class Product implements Serializable {
    private String imvRes;
    private String image;
    private String name;
    private String price;
    private int amount;
    private String description;
    private String category;

    public Product(){

    }



    public Product(String imvRes, String image, String name, String price, int amount, String description, String category) {
        this.imvRes = imvRes;
        this.image = image;
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.description = description;
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImvRes() {
        return imvRes;
    }

    public void setImvRes(String imvRes) {
        this.imvRes = imvRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {

        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
