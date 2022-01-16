package com.hoanghai.fashionstoreapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseUserManager;
import com.hoanghai.fashionstoreapplication.model.User;

import java.util.Calendar;

public class GetInfoActivity extends AppCompatActivity {
    private TextInputLayout tilUserName, tilEmail, tilPhoneNumber;
    private TextInputEditText tetUserName, tetEmail, tetPhoneNumber;
    private TextView txtDate, txtGender;
    private ImageView ivDate, ivGender;
    private boolean isEmail, isUserName, isPhoneNumber;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_info);
        setUpInfo();

        checkEmail();
        checkNumber();
        checkUserName();
    }

    void setUpInfo() {
        tilUserName = findViewById(R.id.tilInputUser);
        tilEmail = findViewById(R.id.tilEmail);
        tilPhoneNumber = findViewById(R.id.tilPhoneNumber);

        tetUserName = findViewById(R.id.tetUserName);
        tetEmail = findViewById(R.id.tetEmail);
        tetPhoneNumber = findViewById(R.id.tetPhoneNumber);

        txtDate = findViewById(R.id.txtDate);
        txtGender = findViewById(R.id.txtGender);

        ivDate = findViewById(R.id.ivDate);
        ivGender = findViewById(R.id.ivGender);
    }

    private void checkUserName() {
        String pattern = "^[^<>{}\\\"/|;:.,~!?@#$%^=&*\\]\\\\()\\[¿§«»ω⊙¤°℃℉€¥£¢¡®©_+-]*$";
        tetUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    tilUserName.setError("Tên không được để trống");
                    isUserName = false;
                } else {
                    String userName = charSequence.toString();
                    if (userName.matches(pattern)) {
                        isUserName = true;
                        tilUserName.setError(null);
                    } else {
                        tilUserName.setError("Tên không được chứa ký tự đặc biêt");
                        isUserName = false;
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
                    tilEmail.setError("Tên không được để trống");
                    isEmail = false;
                } else {
                    String mail = charSequence.toString();
                    if (mail.matches(pattern)) {
                        isEmail = true;
                        tilEmail.setError(null);
                    } else {
                        tilEmail.setError("Email sai định dạng");
                        isEmail = false;
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void checkNumber() {
        String pattern = "^(0)[2-9](\\d{2}){4}$";
        tetPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 10) {
                    tilPhoneNumber.setError("Số điện thoại không đúng");
                    isPhoneNumber = false;
                } else {
                    String mail = charSequence.toString();
                    if (mail.matches(pattern)) {
                        isPhoneNumber = true;
                        tilPhoneNumber.setError(null);
                    } else {
                        tilPhoneNumber.setError("Số điện thoại chỉ được chứa số");
                        isPhoneNumber = false;
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void checkSave(View view) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Bạn có muốn lưu thông tin này không");
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(isUserName && isPhoneNumber && isEmail){
                    upLoadUserInfo();
                }
                else {
                    Toast.makeText(GetInfoActivity.this
                            , "Thông tin nhập sai định hoặc đang để trống"
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNeutralButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
    private void goHome(){
        Intent intent = new Intent(this,HomeMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        setTokenForUser();
        finish();
    }

    public void setBirthday(View view) {
        MaterialDatePicker birthday =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

        birthday.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis((long) selection);
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                txtDate.setText(mDay+"/"+(mMonth+1)+"/"+mYear);
                Log.d("HDH",mDay+"/" + (mMonth+1) +"/" + mYear);
                Log.d("HDH","date " + selection);
            }
        });
        birthday.show(getSupportFragmentManager(),null);

    }

    public void setGender(View view) {
        String[] listGender = {"Nam","Nữ","Khác"};
        MaterialAlertDialogBuilder setGender = new MaterialAlertDialogBuilder(this);
        setGender.setTitle("Chọn giới tính");

        setGender.setItems(listGender, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("HDH","Giới tính " + listGender[i]);
                txtGender.setText(listGender[i]);
                dialogInterface.dismiss();
            }
        });
        setGender.show();

    }

    private void upLoadUserInfo(){
        ProgressDialog progressDialog =
                ProgressDialog.show(GetInfoActivity.this,
                        null, null
                        , true);
        progressDialog.setContentView(R.layout.design_progress);
        progressDialog.getWindow().setBackgroundDrawable
                (new ColorDrawable(android.graphics.Color.TRANSPARENT));

        progressDialog.show();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userName = tetUserName.getText().toString();
        String email = tetEmail.getText().toString();
        String phoneNumber = tetPhoneNumber.getText().toString();
        String birthday = txtDate.getText().toString();
        String gender = txtGender.getText().toString();

        User user = new User(userName,email,phoneNumber,birthday,gender);
        DatabaseUserManager.getInstance()
                .getUser(firebaseUser.getUid())
                .setValue(user,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                        goHome();
                        progressDialog.dismiss();
                    }
                });

        progressDialog.dismiss();
    }

    private void setTokenForUser(){
        FirebaseMessaging.getInstance()
                .getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(task.isSuccessful()){
                            String token = task.getResult();
                            Toast.makeText(GetInfoActivity.this, "token = "+ token, Toast.LENGTH_SHORT).show();
                            DatabaseUserManager
                                    .getInstance()
                                    .getUser(firebaseUser.getUid())
                                    .child("token")
                                    .setValue(token);
                        }
                    }
                });
    }


}