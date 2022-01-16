package com.hoanghai.fashionstoreapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseOrderManager;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseUserManager;
import com.hoanghai.fashionstoreapplication.fcm.FcmNotificationsSender;
import com.hoanghai.fashionstoreapplication.fragment.BasketFragment;
import com.hoanghai.fashionstoreapplication.fragment.HomeFragment;
import com.hoanghai.fashionstoreapplication.model.Order;
import com.hoanghai.fashionstoreapplication.model.Product;
import com.hoanghai.fashionstoreapplication.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BuyProductActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtProductName,txtPrice,txtTotalPrice,txtAmount;
    private Chip chipSizeS,chipSizeM,chipSizeL,chipSizeXL;
    private ImageView imgProduct;
    private TextInputLayout tilPhoneNumber;
    private TextInputEditText tetPhoneNumber;
    private EditText edtAddress,edtUserName;
    private LinearLayout lnBuy;

    private boolean isPhoneNumber = false;
    private int amount = 1;
    private ImageView ic_minus,ic_plus;
    private Double price = 1.0;
    private String size = "L";

    private Product rootProduct;
    private  String userId ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_product);

        setUpProduct();
        setUpItemClickAble();
        checkPhoneNumber();
        loadUserData();

    }

    private void setUpProduct(){
        tilPhoneNumber = findViewById(R.id.tilPhoneNumber);
        tetPhoneNumber = findViewById(R.id.tetPhoneNumber);

        imgProduct = findViewById(R.id.ivProduct);
        txtProductName = findViewById(R.id.txtProductName);
        txtPrice = findViewById(R.id.txtPrice);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtAmount = findViewById(R.id.txtCount);
        edtAddress = findViewById(R.id.edtAddress);
        edtUserName = findViewById(R.id.edtUserName);


        Bundle bundle = getIntent().getExtras();
        Product product = (Product) bundle.getSerializable(HomeFragment.KEY_PRODUCT);
        rootProduct = product;
        Glide.with(this)
                .load(product.getImage())
                .centerCrop()
                .placeholder(R.drawable.ic_uploadimage)
                .into(imgProduct);
        txtProductName.setText(product.getName());
        txtPrice.setText(product.getPrice() + " vnd");

        String[] oldPrice = product.getPrice().trim().split("[.]");
        String newPrice = "";
        for(int i = 0; i < oldPrice.length; i++){
            newPrice += oldPrice[i];
        }
        price = Double.parseDouble(newPrice);
        txtTotalPrice.setText("Tổng tiền = " + newPrice + " vnd");

       userId = DatabaseUserManager
                .getInstance()
                .getUserUid();


    }

    private void setUpItemClickAble(){
        ic_minus = findViewById(R.id.ic_minus);
        ic_plus = findViewById(R.id.ic_plus);
        ic_minus.setOnClickListener(this);
        ic_plus.setOnClickListener(this);

        chipSizeS = findViewById(R.id.chipSizeS);
        chipSizeM = findViewById(R.id.chipSizeM);
        chipSizeL = findViewById(R.id.chipSizeL);
        chipSizeXL = findViewById(R.id.chipSizeXL);

        chipSizeS.setOnClickListener(this);
        chipSizeM.setOnClickListener(this);
        chipSizeL.setOnClickListener(this);
        chipSizeXL.setOnClickListener(this);

        lnBuy = findViewById(R.id.lnBuy);
        lnBuy.setOnClickListener(this);

        if(rootProduct.getCategory().equals("c")){
            chipSizeS.setText("S");
            chipSizeM.setText("M");
            chipSizeL.setText("L");
            chipSizeXL.setText("XL");
        }
        else {
            chipSizeS.setText("39");
            chipSizeM.setText("40");
            chipSizeL.setText("41");
            chipSizeXL.setText("42");
        }
    }

    private void loadUserData(){
        DatabaseUserManager
                .getInstance()
                .getUser(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if(user != null){
                            edtUserName.setText(user.getUserName());
                            tetPhoneNumber.setText(user.getPhoneNumber());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkPhoneNumber() {
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
                        isPhoneNumber = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.chipSizeS:{
                if(chipSizeS.isCheckable()) {
                    size = chipSizeS.getText().toString();
                }

                break;
            }
            case R.id.chipSizeM:{
                if(chipSizeM.isCheckable()) {
                    size = chipSizeM.getText().toString();

                }

                break;
            }
            case R.id.chipSizeL:{
                if(chipSizeL.isCheckable()) {
                    size = chipSizeL.getText().toString();
                }
                break;
            }
            case R.id.chipSizeXL:{
                if(chipSizeXL.isCheckable()) {
                    size = chipSizeXL.getText().toString();

                }
                break;
            }
            case R.id.ic_minus:
                if(amount > 1 ){
                    amount--;
                    txtAmount.setText(String.valueOf(amount));
                    String totalPrice = String.valueOf(price*amount);
                    txtTotalPrice.setText("Tổng tiền = " + totalPrice + " vnd");
                }
                break;
            case R.id.ic_plus:
                amount++;
                txtAmount.setText(String.valueOf(amount));
                String totalPrice = String.valueOf(price*amount);
                txtTotalPrice.setText("Tổng tiền = " + totalPrice + " vnd");
                break;

            case  R.id.lnBuy:{
                buyProduct(rootProduct);
                break;
            }
        }
    }

    private void sendNotificationToAdmin() {
        DatabaseUserManager.getInstance()
                .getListAdmin()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data : snapshot.getChildren()){
                            String adminKey = data.getKey();
                            sendNotification(adminKey);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sendNotification(String adminKey) {
     DatabaseUserManager
                .getInstance()
                .getUser(adminKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String token = (String) snapshot.child("token").getValue();
                        String title = "Bạn có một đơn đặt hàng từ khách hàng "
                                + edtUserName.getText().toString()
                                + " vui lòng vào xác nhận";
                        String body = "Tên sản phẩm " + txtProductName.getText().toString();
                        if(token != null) {
                            FcmNotificationsSender notificationsSender
                                    = new FcmNotificationsSender(token, title, body,
                                    getApplicationContext(), BuyProductActivity.this);

                            notificationsSender.SendNotifications();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void buyProduct(Product rootProduct) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Bạn đã mua sản phẩm thành công");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy ");
        String saveCurrentDate = simpleDateFormat.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(calendar.getTime());

        String orderRandomKey = saveCurrentDate + saveCurrentTime;
        if (edtAddress.getText().toString().length() < 1 || !isPhoneNumber) {
            Toast.makeText(this, "Số điện thoại và địa chỉ không được để trống"
                    , Toast.LENGTH_SHORT).show();
        } else {
            Order order =
                    new Order
                            (orderRandomKey
                                    , rootProduct.getImvRes()
                                    , BasketFragment.WAIT_FOR_CONFIRM
                                    , size
                                    , txtAmount.getText().toString()
                                    , txtTotalPrice.getText().toString()
                                    , tetPhoneNumber.getText().toString()
                                    , edtAddress.getText().toString()
                                    ,userId
                                    ,edtUserName.getText().toString());
            builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    DatabaseOrderManager
                            .getInstance()
                            .getListOrder(userId)
                            .child(orderRandomKey)
                            .setValue(order)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        sendNotificationToAdmin();
                                        Toast.makeText(BuyProductActivity.this, "Mua hàng thành công", Toast.LENGTH_SHORT).show();
                                        goToBasket();
                                    }

                                }
                            });
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
    }

    private void goToBasket(){
        Intent intent = new Intent(this,HomeMainActivity.class);
        intent.putExtra("BUY_KEY","GO_TO_BASKET");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}