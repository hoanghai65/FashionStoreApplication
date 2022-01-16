package com.hoanghai.fashionstoreapplication.model;

public class Category {
    private int ivRes;
    private String name;
    private int itemAmount;
    private boolean isClicked;
    private String id;


    public Category(int ivRes, String name, int itemAmount, String id) {
        this.ivRes = ivRes;
        this.name = name;
        this.itemAmount = itemAmount;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIvRes() {
        return ivRes;
    }

    public void setIvRes(int ivRes) {
        this.ivRes = ivRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(int itemAmount) {
        this.itemAmount = itemAmount;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }
}
