package com.hoanghai.fashionstoreapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText tetEmail, tetPassword, tetConfirmPassword;
    private Button btnRegister;

    private boolean isEmail, isPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setUpInfo();
        checkEmail();
        checkPassword();

        mAuth = FirebaseAuth.getInstance();
    }

    void setUpInfo() {

        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        tetEmail = findViewById(R.id.tetEmail);
        tetPassword = findViewById(R.id.tetPassword);
        tetConfirmPassword = findViewById(R.id.tetConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);

    }

    private void checkEmail() {
        String pattern = "^[a-zA-Z][\\w_]+@([\\w]+\\.[\\w]+|[\\w]+\\.[\\w]{2,}\\.[\\w]{2,})$";
        tetEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    tilEmail.setError("T??n kh??ng ???????c ????? tr???ng");
                    isEmail = false;
                } else {
                    String mail = charSequence.toString();
                    if (mail.matches(pattern)) {
                        Log.d("HDH", tetEmail.getText().toString());
                        isEmail = true;
                        tilEmail.setError(null);
                    } else {
                        tilEmail.setError("Email sai ?????nh d???ng");
                        isEmail = false;
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void checkPassword() {
        String pattern = "^[^<>{}\\\"/|;:.,~!?@#$%^=&*\\]\\\\()\\[??????????????????????????????????????_+-]*$";
        tetPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    tilPassword.setError("M???t kh???u kh??ng ???????c ????? tr???ng");
                    isPassword = false;
                } else {
                    String userName = charSequence.toString();
                    if (userName.matches(pattern)) {
                        isPassword = true;
                        tilPassword.setError(null);
                    } else {
                        tilPassword.setError("M???t Kh???u kh??ng ???????c ch???a k?? t??? ?????c bi??t");
                        isPassword = false;
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    public void goToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickRegister(View view) {
        String email = tetEmail.getText().toString().trim();
        String password = tetPassword.getText().toString().trim();
        String conFirmPassword = tetConfirmPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email v?? m???t kh???u kh??ng ???????c ????? tr???ng"
                    , Toast.LENGTH_SHORT).show();
        } else if (password.length() < 8) {
            Toast.makeText(this, "M???t kh???u ch???a ??t nh???t 8 k?? t???", Toast.LENGTH_SHORT).show();
        } else {
            if (isEmail && isPassword && password.equals(conFirmPassword)) {
                Log.d("HDH", "?????n ????y ");
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    sendEmailVerification(user);
                                    Log.d("HDH", "isnNew " + task.getResult().getAdditionalUserInfo().isNewUser());
                                    Toast.makeText(RegisterActivity.this, "????ng k?? th??nh c??ng",
                                            Toast.LENGTH_SHORT).show();


                                } else {
                                    String error = task.getException().toString();
                                    Toast.makeText(RegisterActivity.this, error,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(RegisterActivity.this, "X??c nh???n m???t kh???u kh??ng ????ng",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            MaterialAlertDialogBuilder builder =
                                    new MaterialAlertDialogBuilder(RegisterActivity.this);
                            builder.setTitle("Quay ra m??n h??nh ????ng nh???p");

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
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

                    }
                });
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}