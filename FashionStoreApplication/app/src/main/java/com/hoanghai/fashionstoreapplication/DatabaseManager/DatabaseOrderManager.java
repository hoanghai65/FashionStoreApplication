package com.hoanghai.fashionstoreapplication.DatabaseManager;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hoanghai.fashionstoreapplication.model.Order;
import com.hoanghai.fashionstoreapplication.model.Product;

import java.util.ArrayList;
import java.util.List;

public class DatabaseOrderManager {
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseDatabase database;
    private final static String LIST_ORDER = "orders";
    private List<Order> orders = new ArrayList<>();

    public DatabaseOrderManager(){
        reference = FirebaseDatabase
                .getInstance("https://fashionstoreapplication-default-rtdb.asia-southeast1" +
                        ".firebasedatabase.app/").getReference();
    }
    public static DatabaseOrderManager getInstance(){
        return new DatabaseOrderManager();
    }

    public DatabaseReference getListOrder(String uid){
        return reference.child(LIST_ORDER).child(uid);
    }
    public DatabaseReference getListOrderUser(){
        return reference.child(LIST_ORDER);
    }


    public DatabaseReference getOrder(String uid ,String orderKey){
        return getListOrder(uid).child(orderKey);
    }

    public DatabaseReference getUserOder(String uid){
        return reference.child(uid);
    }

}

