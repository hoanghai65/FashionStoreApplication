package com.hoanghai.fashionstoreapplication.DatabaseManager;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hoanghai.fashionstoreapplication.model.Product;

import java.util.ArrayList;
import java.util.List;

public class DatabaseProductManager {
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseDatabase database;
    private final static String LIST_PRODUCT = "Products";
    private List<Product> products = new ArrayList<>();
    private List<Product> rootProducts = new ArrayList<>();

    public DatabaseProductManager(){
        reference = FirebaseDatabase
                .getInstance("https://fashionstoreapplication-default-rtdb.asia-southeast1" +
                        ".firebasedatabase.app/").getReference();
    }
    public static DatabaseProductManager getInstance(){
        return new DatabaseProductManager();
    }

    public DatabaseReference getListProduct(){
        return reference.child(LIST_PRODUCT);
    };

    public DatabaseReference getInfoProduct(String uid){
        return getListProduct().child(uid);
    }



}
