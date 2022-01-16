package com.hoanghai.fashionstoreapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseUserManager;
import com.hoanghai.fashionstoreapplication.DatabaseManager.ProgressDialogManager;
import com.hoanghai.fashionstoreapplication.fragment.BasketFragment;
import com.hoanghai.fashionstoreapplication.fragment.HomeFragment;
import com.hoanghai.fashionstoreapplication.fragment.ProfileFragment;
import com.hoanghai.fashionstoreapplication.fragment.SearchProductFragment;
import com.hoanghai.fashionstoreapplication.model.User;

public class HomeMainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigation;
    public static final String USER_KEY = "USER_KEY";
    public static final int REQUEST_PROFILE = 1;
    private Bundle bundleRoot;
    private ValueEventListener valueEventListener;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_main);

    }

    @Override
    protected void onStart() {
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        setUpBottomNavigation();
        loadUserInfo();

        String buy_product = getIntent().getStringExtra("BUY_KEY");
        if(buy_product != null){
            replaceFragment("BasketFragment");
            bottomNavigation.setSelectedItemId(R.id.itembasket);
        }
        super.onStart();
    }

    public void replaceFragment(String tag) {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (tag) {
            case "HomeFragment":
                fragmentTransaction
                        .replace(R.id.fragment_home, HomeFragment.class, null)
                        .addToBackStack(null)
                        .commit();
                break;
            case "ProfileFragment":
                if (bundleRoot == null) {
                    fragmentTransaction
                            .replace(R.id.fragment_home, ProfileFragment.class, null)
                            .addToBackStack(null)
                            .commit();

                } else {
                    fragmentTransaction
                            .replace(R.id.fragment_home, ProfileFragment.class, bundleRoot)
                            .addToBackStack(null)
                            .commit();

                }
                break;

            case "BasketFragment":
                fragmentTransaction
                        .replace(R.id.fragment_home, BasketFragment.class, null)
                        .addToBackStack(null)
                        .commit();
                break;
            case "SearchProductFragment":
                fragmentTransaction
                        .replace(R.id.fragment_home, SearchProductFragment.class, null)
                        .addToBackStack(null)
                        .commit();
                break;
            default: {
                fragmentTransaction
                        .replace(R.id.fragment_home, HomeFragment.class, null)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    public void setUpBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        Log.d("HDH", "clicked item home");
        bottomNavigation.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.itemhome:
                                replaceFragment("HomeFragment");

                                break;
                            case R.id.itemprofile:
                                replaceFragment("ProfileFragment");
                                break;
                            case R.id.itembasket:
                                replaceFragment("BasketFragment");
                                break;
                            case R.id.itemsearch:
                                replaceFragment("SearchProductFragment");
                                break;

                            default: {
                                replaceFragment("HomeFragment");
                            }
                        }
                        return true;
                    }
                });
    }

    public void setEditProfile(View view) {
        Intent intent = new Intent(this, EditProfileActivity.class);
        if (bundleRoot == null) {
            startActivity(intent);
        } else {
            intent.putExtras(bundleRoot);
            startActivityForResult(intent, REQUEST_PROFILE);
        }
    }

    public void signOut(View view) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Bạn có muốn đăng xuất tài khoản không");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAuth.signOut();
                GoogleSignIn.getClient(
                        HomeMainActivity.this,
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                ).signOut();
                goToLogin();
                dialogInterface.dismiss();
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();

    }

    private void goToLogin(){
        Intent intent = new Intent(this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    private void loadUserInfo() {
        ProgressDialog progressDialog =
                ProgressDialog.show(HomeMainActivity.this,
                        null, null
                        , true);
        progressDialog.setContentView(R.layout.design_progress);
        progressDialog.getWindow().setBackgroundDrawable
                (new ColorDrawable(android.graphics.Color.TRANSPARENT));

        progressDialog.show();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(USER_KEY, user);
                    bundleRoot = bundle;
                    progressDialog.dismiss();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        };
        if (firebaseUser != null) {
            DatabaseUserManager
                    .getInstance()
                    .getUser(firebaseUser.getUid())
                    .addValueEventListener(valueEventListener);


        }
        progressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_home);
        if (fragment instanceof ProfileFragment) {

            fragment.onActivityResult(requestCode, resultCode, data);
        } else super.onActivityResult(requestCode, resultCode, data);

    }

    public void goToListProduct(View view) {
        Intent intent = new Intent(this, ListProductForUserActivity.class);
        startActivity(intent);
    }

    public void goToBasket(View view) {
        replaceFragment("BasketFragment");
        bottomNavigation.setSelectedItemId(R.id.itembasket);


    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_home);
        if (f instanceof ProfileFragment) {
            ((ProfileFragment) f).onBackPressed();
            bottomNavigation.setSelectedItemId(R.id.itemhome);

        } else if (f instanceof BasketFragment) {
            ((BasketFragment) f).onBackPressed();
            bottomNavigation.setSelectedItemId(R.id.itemhome);
        } else if (f instanceof SearchProductFragment) {
            ((SearchProductFragment) f).onBackPressed();
            bottomNavigation.setSelectedItemId(R.id.itemhome);
        } else {
            ((HomeFragment) f).onBackPressed();
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        if (firebaseUser != null) {
            DatabaseUserManager
                    .getInstance()
                    .getUser(firebaseUser.getUid())
                    .removeEventListener(valueEventListener);
            Log.d("PTH","stopped");


        }
        super.onStop();
    }

}