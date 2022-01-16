package com.hoanghai.fashionstoreapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseUserManager;
import com.hoanghai.fashionstoreapplication.DatabaseManager.ProgressDialogManager;
import com.hoanghai.fashionstoreapplication.model.User;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    private TextInputEditText tetEmail, tetPassword;
    private Button btnLogin;
    private Button btn_loginFb;
    private SignInButton btn_loginGg;
    private User user;
    private ValueEventListener valueEventListener;
    private ProgressDialogManager dialogManager;

    private CallbackManager mCallbackManager;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tetEmail = findViewById(R.id.tetEmail);
        tetPassword = findViewById(R.id.tetPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btn_loginFb = findViewById(R.id.fb);
        btn_loginGg = findViewById(R.id.btn_loginGg);
        tetEmail.setText("");
        tetPassword.setText("");

        setUpFacebook();

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("433135333053-cq0vrdq21oroj8onp6ikck3acar2le0o.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btn_loginGg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser != null) {
            updateId(firebaseUser);
        }

    }

    private void updateId(FirebaseUser user){

        if(user != null) {
            if(user.isEmailVerified()){
                checkUserExist(user);
                return;
            }
            for (UserInfo userInfo:user.getProviderData()) {
                if (userInfo.getProviderId().equals("facebook.com")) {
                    checkUserExist(user);
                    Log.d("PTH","login by facebook");
                    return;
                }
                else if(userInfo.getProviderId().equals("google.com")){
                    checkUserExist(user);
                    Log.d("PTH","login by google");
                    return;
                }
            }
        }



    }
    private void reload(boolean isAdmin) {

        if(isAdmin){
            goToAdmin();
        }
        else {
            goToHome();
        }
    }

    public void setLogin(View view) {
        dialogManager = ProgressDialogManager.getInstance(this);
        String email = tetEmail.getText().toString().trim();
        String password = tetPassword.getText().toString().trim();
        Log.d("HDH", email + "  " + password);
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email và mật khẩu không được để trống"
                    , Toast.LENGTH_SHORT).show();
            dialogManager.setDismissProgressDialog();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                firebaseUser = mAuth.getCurrentUser();
                                if (firebaseUser.isEmailVerified()) {
                                    checkUserExist(firebaseUser);

                                } else {
                                    Toast.makeText(
                                            LoginActivity.this,
                                            "Tài khoản email chưa xác thực"
                                            , Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "Tài Khoản hoặc mật khẩu không đúng",
                                        Toast.LENGTH_SHORT).show();
                            }
                            dialogManager.setDismissProgressDialog();
                        }
                    });
        }
    }

    private void goToGetInfo() {
        Toast.makeText(this,
                "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, GetInfoActivity.class);
        startActivity(intent);
    }

    public void gotoSignUp(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    private void goToHome(){
        Intent intent = new Intent(this, HomeMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void goToAdmin() {
        Intent intent = new Intent(this, AdminMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void checkUserExist(FirebaseUser firebaseUser) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (user == null) {
                    goToGetInfo();

                } else {
                    checkAdmin(firebaseUser);
                    setTokenForUser(firebaseUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this
                        , "Đã xảy ra lỗi, vui lòng đăng nhập lại" + error.getMessage()
                        , Toast.LENGTH_SHORT).show();
            }
        };
        DatabaseUserManager
                .getInstance()
                .getUser(firebaseUser.getUid())
                .addValueEventListener(valueEventListener);
    }

    private void checkAdmin(FirebaseUser firebaseUser) {


        String userUid = firebaseUser.getUid();
        DatabaseUserManager
                .getInstance()
                .getListAdmin()
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAdmin = false;
                for(DataSnapshot data : snapshot.getChildren()){
                    String id = (String) data.getKey();
                    if(userUid.equals(id)){
                        isAdmin = true;
                    }
                }
                reload(isAdmin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setTokenForUser(FirebaseUser firebaseUser){
        FirebaseMessaging.getInstance()
                .getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(task.isSuccessful()){
                            String token = task.getResult();
//                            Toast.makeText(LoginActivity.this, "token = "+ token, Toast.LENGTH_SHORT).show();
                            DatabaseUserManager
                                    .getInstance()
                                    .getUser(firebaseUser.getUid())
                                    .child("token")
                                    .setValue(token);
                        }
                    }
                });
    }

    public void LoginFacebook(View view) {



        LoginManager.getInstance()
                .logInWithReadPermissions(LoginActivity.this
                        , Arrays.asList("public_profile", "user_friends"));

    }

    private void setUpFacebook(){
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");

                        Log.d("HDH", "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("PTH", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("PTH", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateId(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("PTH", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateId(null);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ProgressDialogManager dialogManager = ProgressDialogManager.getInstance(this);
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                Log.d("HDH", "firebaseAuthWithGoogle:" + account.getId());
                handleGoogleAccessToken(account.getIdToken());
                dialogManager.setDismissProgressDialog();

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("HDH", "Google sign in failed", e);
                dialogManager.setDismissProgressDialog();


            }
        }
        dialogManager.setDismissProgressDialog();
    }

    private void handleGoogleAccessToken(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("HDH", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateId(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("HDH", "signInWithCredential:failure", task.getException());
                            updateId(null);
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        if(firebaseUser != null && valueEventListener != null) {
            DatabaseUserManager
                    .getInstance()
                    .getUser(firebaseUser.getUid())
                    .removeEventListener(valueEventListener);

        }
        super.onStop();
    }
}