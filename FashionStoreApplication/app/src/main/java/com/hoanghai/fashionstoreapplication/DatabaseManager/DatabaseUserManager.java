package com.hoanghai.fashionstoreapplication.DatabaseManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hoanghai.fashionstoreapplication.model.User;

public class DatabaseUserManager {
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseDatabase database;


    public DatabaseUserManager(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference =
                FirebaseDatabase.getInstance
                        ("https://fashionstoreapplication-default-rtdb" +
                                ".asia-southeast1.firebasedatabase.app/").getReference();
    }
    public static DatabaseUserManager getInstance(){
        return new DatabaseUserManager();
    }
    public FirebaseDatabase getDatabase() {
        return database;
    }

    public void setDatabase(FirebaseDatabase database) {
        this.database = database;
    }

    public DatabaseReference getListUser(){
        return  reference.child("users");
    }
    public DatabaseReference getListAdmin(){
        return  reference.child("admin");
    }
    public DatabaseReference getAdmin(String uidAdmin){
        return getListAdmin().child(uidAdmin);
    }

    public String getUserUid(){
        return  firebaseUser.getUid();
    }
    public DatabaseReference getUser(String key){
        return  reference.child("users").child(key);
    }


}
