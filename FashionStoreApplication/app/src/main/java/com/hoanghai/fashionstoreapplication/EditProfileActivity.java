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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseUserManager;
import com.hoanghai.fashionstoreapplication.DatabaseManager.ProgressDialogManager;
import com.hoanghai.fashionstoreapplication.model.User;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private CircleImageView profile_image;
    private LayoutInflater inflater;
    private TextView txtName,txtEmail,txtPhoneNumber
            ,txtDate,txtGender,txtUserName;
    private View textInputView;
    private TextInputEditText inputEditText;
    private TextInputLayout textInputLayout;
    private ProgressDialogManager dialogManager;
    public static final String RESULT_KEY = "RESULT_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setProfileImage();
        setUpUserInfo();
        inflater = LayoutInflater.from(this);
        checkInput();


    }
    private void setUpUserInfo(){
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtDate = findViewById(R.id.txtDate);
        txtGender = findViewById(R.id.txtGender);
        txtUserName = findViewById(R.id.txtUserName);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            User user = (User) bundle.getSerializable(HomeMainActivity.USER_KEY);
            txtUserName.setText(user.getUserName());
            txtName.setText(user.getUserName());
            txtEmail.setText(user.getEmail());
            txtPhoneNumber.setText(user.getPhoneNumber());
            txtDate.setText(user.getBirthday());
            txtGender.setText(user.getGender());
        }
    }

    private void checkInput(){
        textInputView = inflater.inflate(R.layout.design_text_input,null,false);
        textInputLayout = textInputView.findViewById(R.id.tilInput);
        inputEditText = textInputView.findViewById(R.id.et_input);

        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() ==0) {
                    textInputLayout.setError("Không được để trống");
                } else textInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    public void setProfileImage(){
        profile_image = findViewById(R.id.profile_image);
        Glide.with(this).
                load("http://goo.gl/gEgYUd")
                .error(R.drawable.ic_profile)
                .into(profile_image);

    }


    public void setEditUserName(View view) {
        View viewStack = view;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Chỉnh sửa tên");
        builder.setMessage("Nhập tên của bạn");
        if(textInputView.getParent() != null){
            ((ViewGroup)textInputView.getParent()).removeView(textInputView);
        }
        builder.setView(textInputView);
        builder.setBackground(getDrawable(R.drawable.borderbackground));
        inputEditText.setText("");

        String pattern = "^[^<>{}\\\"/|;:.,~!?@#$%^=&*\\]\\\\()\\[¿§«»ω⊙¤°℃℉€¥£¢¡®©_+-]*$";
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TextView txtNameInput = textInputView.findViewById(R.id.et_input);
                String name = txtNameInput.getText().toString();

                if(name.matches(pattern)) {
                    txtName.setText(name);

                }
                else {
                    Snackbar snackbar =
                            Snackbar.make(view,
                                    "Tên không được chứa các ký tự đặc biệt"
                                    ,Snackbar.LENGTH_LONG)
                                    .setAction("Thử lại", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            view = viewStack;
                                            setEditUserName(view);
                                        }
                                    });
                    snackbar.show();
                }
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void setEditEmail(View view) {
        View viewStack = view;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Thay đổi Email");
        builder.setMessage("Nhập email mới");
        if(textInputView.getParent() != null){
            ((ViewGroup)textInputView.getParent()).removeView(textInputView);
        }
        builder.setView(textInputView);
        builder.setBackground(getDrawable(R.drawable.borderbackground));
        inputEditText.setText("");

        String pattern = "^[\\w_]+[\\w]+@gmail.com$";

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TextView Email = textInputView.findViewById(R.id.et_input);
                String email = Email.getText().toString();
                if(email.matches(pattern)) {
                    txtEmail.setText(email);

                }
                else {
                    Snackbar snackbar =
                            Snackbar.make(view,
                                    "Email không hợp lệ"
                                    ,Snackbar.LENGTH_LONG)
                                    .setAction("Thử lại", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            view = viewStack;
                                            setEditEmail(view);
                                        }
                                    });
                    snackbar.show();
                }
                dialogInterface.dismiss();

            }
        });
        builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("HDH","Thoát ra");
            }
        });
        builder.show();
    }

    public void setEditPhoneNumber(View view) {
        View viewStack = view;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Thay đổi số điện thoại");
        builder.setMessage("Nhập số điện thoại");
        if(textInputView.getParent() != null){
            ((ViewGroup)textInputView.getParent()).removeView(textInputView);
        }
        builder.setView(textInputView);
        builder.setBackground(getDrawable(R.drawable.borderbackground));

        inputEditText.setText("");
        String pattern = "^(0)[2-9](\\d{2}){4}$";

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TextView PhoneNumber = textInputView.findViewById(R.id.et_input);
                String phone = PhoneNumber.getText().toString();
                if(phone.matches(pattern)) {
                    txtPhoneNumber.setText(phone);


                }else {
                    Snackbar snackbar =
                            Snackbar.make(view,
                                    "Số điện thoại không hợp lệ"
                                    ,Snackbar.LENGTH_LONG)
                                    .setAction("Thử lại", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            view = viewStack;
                                            setEditPhoneNumber(view);
                                        }
                                    });
                    snackbar.show();
                }
                dialogInterface.dismiss();
            }

        });
        builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    public void setEditBirthday(View view) {
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

    public void setEditGender(View view) {
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



    public void setUpSaveData(View view) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Bạn muốn thay đổi không ?");
        builder.setBackground(getDrawable(R.drawable.borderbackground));
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                txtUserName.setText(txtName.getText());
                setUpdateProfileData();

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

    private void setUpdateProfileData(){

        dialogManager = ProgressDialogManager.getInstance(this);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String userName = txtName.getText().toString();
        String email = txtEmail.getText().toString();
        String phoneNumber = txtPhoneNumber.getText().toString();
        String birthday = txtDate.getText().toString();
        String gender = txtGender.getText().toString();
        if(firebaseUser.getUid() != null){
            User user = new User(userName,email,phoneNumber,birthday,gender);
            Log.d("HDH","user = " + user.toString());
            DatabaseUserManager
                    .getInstance()
                    .getUser(firebaseUser.getUid())
                    .setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Bundle bundle = new Bundle();
                            Intent intent = new Intent();
                            bundle.putSerializable(RESULT_KEY,user);
                            intent.putExtras(bundle);
                            setResult(RESULT_OK,intent);
                            setTokenForUser(firebaseUser);
                            dialogManager.setDismissProgressDialog();
                            Toast.makeText(EditProfileActivity.this, "Sửa thông tin thành công", Toast.LENGTH_SHORT).show();

                        }
                    });
        }
        dialogManager.setDismissProgressDialog();
    }

    private void setTokenForUser(FirebaseUser firebaseUser){
        FirebaseMessaging.getInstance()
                .getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(task.isSuccessful()){
                            String token = task.getResult();
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