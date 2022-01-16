package com.hoanghai.fashionstoreapplication;

import androidx.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseOrderManager;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseProductManager;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseUserManager;
import com.hoanghai.fashionstoreapplication.DatabaseManager.ProgressDialogManager;
import com.hoanghai.fashionstoreapplication.fcm.FcmNotificationsSender;
import com.hoanghai.fashionstoreapplication.fragment.BasketFragment;
import com.hoanghai.fashionstoreapplication.model.Order;
import com.hoanghai.fashionstoreapplication.model.Product;
import com.hoanghai.fashionstoreapplication.model.User;

public class OrderDetailActivity extends AppCompatActivity {
    private TextView txtProductName,txtCount,txtSize,txtTotalPrice
    ,txtUserName,txtPhoneNumber,txtAddress,txtStartDate,txtProductDetail;
    private TextView btnCancelOrder;
    private Order order;
    private String productName;
    private String userName;
    private ProgressDialogManager dialogManager;
    private TextInputLayout tilPhoneNumber;
    private TextInputEditText tetPhoneNumber;
    private boolean isPhoneNumber = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        setUpOrderDetail();
        setOrderDetailData();
    }

    private void setUpOrderDetail() {
        txtProductName = findViewById(R.id.txtProductName);
        txtCount = findViewById(R.id.txtCount);
        txtSize = findViewById(R.id.txtSize);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtUserName = findViewById(R.id.txtUserName);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtAddress = findViewById(R.id.txtAddress);
        txtStartDate = findViewById(R.id.txtStartDate);
        txtProductDetail = findViewById(R.id.txtProductDetail);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);

        Bundle bundle = getIntent().getExtras();
        order = (Order) bundle.getSerializable(Order.ORDER_KEY);


    }
    private void setOrderDetailData(){
        if(order != null){

            txtCount.setText(order.getAmount());
            txtSize.setText(order.getSize());
            txtTotalPrice.setText(order.getTotalPrice());
            txtPhoneNumber.setText(order.getPhoneNumber());
            txtAddress.setText(order.getAddress());
            txtPhoneNumber.setText(order.getPhoneNumber());
            txtStartDate.setText(order.getIdOrder());
            txtUserName.setText(order.getUserName());
            loadProductData();
        }
    }
    private void loadProductData(){
        Log.d("PTH","product = " + "HDH");
        DatabaseProductManager
                .getInstance()
                .getInfoProduct(order.getProductKey())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Product product = snapshot.getValue(Product.class);

                        if(product != null){
                            productName = product.getName();
                            txtProductName.setText(productName);
                            Log.d("PTH","product = "+ productName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    public void showProductInfoDetail(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(Order.ORDER_KEY,order.getProductKey());
        Intent intent = new Intent(this, ItemProductDetailsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void cancelOrder(View view) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Bạn có chắc chắn muốn hủy đơn hàng này không");

        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(!order.getStatus().equals(BasketFragment.CANCEL)) {
                    order.setStatus(BasketFragment.CANCEL);
                    DatabaseOrderManager
                            .getInstance()
                            .getOrder(order.getUidUser(), order.getIdOrder())
                            .child("status")
                            .setValue(BasketFragment.CANCEL)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    sendNotificationToAdmin();
                                    Toast.makeText(OrderDetailActivity.this
                                            , "Bạn đã hủy đơn hàng thành công"
                                            , Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    Toast.makeText(OrderDetailActivity.this
                            , "Đơn hàng đã được hủy"
                            , Toast.LENGTH_SHORT).show();
                }
                dialogInterface.dismiss();
            }
        });
        builder.setNeutralButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();

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

    public void EditOrderDetail(View view) {
        dialogManager = ProgressDialogManager.getInstance(this);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View viewDesign =
                LayoutInflater
                        .from(this)
                        .inflate(R.layout.design_edit_order_detail,null);

        tilPhoneNumber = viewDesign.findViewById(R.id.tilPhoneNumber);
        tetPhoneNumber = viewDesign.findViewById(R.id.tetPhoneNumber);
        tetPhoneNumber.setText(txtPhoneNumber.getText().toString());

        checkPhoneNumber();
        EditText edtAddress = viewDesign.findViewById(R.id.edtAddress);
        EditText edtUserName = viewDesign.findViewById(R.id.edtUserName);
        edtUserName.setText(txtUserName.getText().toString());
        edtAddress.setText(txtAddress.getText().toString());
        builder.setView(viewDesign);
        builder.setBackground(getDrawable(R.drawable.borderbackground));
        builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(edtAddress.getText().length() < 1 || !isPhoneNumber
                || edtUserName.getText().length() < 1){
                    Toast.makeText(OrderDetailActivity.this
                            , "Không được để trống"
                            , Toast.LENGTH_SHORT).show();
                }
                else {
                    DatabaseOrderManager
                            .getInstance()
                            .getOrder(order.getUidUser(), order.getIdOrder())
                            .child("userName")
                            .setValue(edtUserName.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    txtUserName.setText(edtUserName.getText());

                                }
                            });
                    DatabaseOrderManager
                            .getInstance()
                            .getOrder(order.getUidUser(), order.getIdOrder())
                            .child("phoneNumber")
                            .setValue(tetPhoneNumber.getText().toString())
                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     txtPhoneNumber.setText(tetPhoneNumber.getText());
                                 }
                             });
                    DatabaseOrderManager
                            .getInstance()
                            .getOrder(order.getUidUser(), order.getIdOrder())
                            .child("address")
                            .setValue(edtAddress.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    txtAddress.setText(edtAddress.getText());
                                }
                            });

                    dialogManager.setDismissProgressDialog();

                }
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                dialogManager.setDismissProgressDialog();
            }
        });
        builder.show();
        dialogManager.setDismissProgressDialog();

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
                        String title = "Khách hàng"
                                + txtUserName.getText().toString()
                                + " đã hủy đơn hàng";
                        String body = "Tên sản phẩm " + txtProductName.getText().toString();
                        if(token != null) {
                            FcmNotificationsSender notificationsSender
                                    = new FcmNotificationsSender(token, title, body,
                                    getApplicationContext(), OrderDetailActivity.this);

                            notificationsSender.SendNotifications();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}