package com.hoanghai.fashionstoreapplication.model;

import java.io.Serializable;

public class Order implements Serializable {
    private String idOrder;
    private String productKey;
    private String status;
    private String size;
    private String amount;
    private String totalPrice;
    private String phoneNumber;
    private String address;
    private String uidUser;
    private String userName;
    public final static String ORDER_KEY = "ORDER_KEY";
    public final static String ORDER_KEY_ADMIN = "ORDER_KEY_ADMIN";

    public Order(String idOrder, String productKey
            , String status, String size, String amount
            , String totalPrice, String phoneNumber, String address, String uidUser) {
        this.idOrder = idOrder;
        this.productKey = productKey;
        this.status = status;
        this.size = size;
        this.amount = amount;
        this.totalPrice = totalPrice;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.uidUser = uidUser;
    }

    public Order(String idOrder, String productKey
            , String status, String size
            , String amount, String totalPrice
            , String phoneNumber, String address, String uidUser, String userName) {
        this.idOrder = idOrder;
        this.productKey = productKey;
        this.status = status;
        this.size = size;
        this.amount = amount;
        this.totalPrice = totalPrice;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.uidUser = uidUser;
        this.userName = userName;
    }

    public Order() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "idOrder='" + idOrder + '\'' +
                ", productKey='" + productKey + '\'' +
                ", status='" + status + '\'' +
                ", size='" + size + '\'' +
                ", amount='" + amount + '\'' +
                ", totalPrice='" + totalPrice + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", uidUser='" + uidUser + '\'' +
                '}';
    }
}
