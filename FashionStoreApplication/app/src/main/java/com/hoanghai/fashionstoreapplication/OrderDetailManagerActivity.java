package com.hoanghai.fashionstoreapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.hoanghai.fashionstoreapplication.fcm.FcmNotificationsSender;
import com.hoanghai.fashionstoreapplication.fragment.BasketFragment;
import com.hoanghai.fashionstoreapplication.model.Order;
import com.hoanghai.fashionstoreapplication.model.Product;

public class OrderDetailManagerActivity extends AppCompatActivity {
    private TextView txtProductName,txtCount,txtSize,txtTotalPrice
            ,txtUserName,txtPhoneNumber,txtAddress,txtStartDate,txtProductDetail;
    private TextView btnCancelOrder,btnConfirm;
    private Order order;
    private String productName;
    private String userName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_manager);
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
        btnConfirm = findViewById(R.id.btnConfirm);

        Bundle bundle = getIntent().getExtras();
        order = (Order) bundle.getSerializable(Order.ORDER_KEY);
        if(order == null) {
            order = (Order) bundle.getSerializable(Order.ORDER_KEY_ADMIN);
            btnConfirm.setAlpha(0);
            btnConfirm.getLayoutParams().width = 0;
        }
        Log.d("PTH","order = "+ order.toString());


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
        userId = order.getUidUser();
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
                                    SendNotificationToUser(userId, "cancel");
                                    Toast.makeText(OrderDetailManagerActivity.this
                                            , "Bạn đã hủy đơn hàng thành công"
                                            , Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else{
                    Toast.makeText(OrderDetailManagerActivity.this
                            , "Đơn hàng đã bị hủy"
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

    public void setConfirmOrder(View view) {
        userId = order.getUidUser();
        String orderId = order.getIdOrder();
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Xác nhận đơn hàng này");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!order.getStatus().equals(BasketFragment.WAIT_FOR_PAYMENT)) {
                    order.setStatus(BasketFragment.WAIT_FOR_PAYMENT);
                    DatabaseOrderManager
                            .getInstance()
                            .getOrder(userId, orderId)
                            .child("status")
                            .setValue(OrderManagerActivity.WAIT_FOR_PAYMENT)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(OrderDetailManagerActivity.this
                                                , "Xác nhận đơn hàng thành công"
                                                , Toast.LENGTH_SHORT).show();

                                        SendNotificationToUser(userId, "confirm");
                                    }

                                }
                            });
                }else{
                    Toast.makeText(OrderDetailManagerActivity.this
                            , "Đơn hàng đã được xác nhận"
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

    private void SendNotificationToUser(String userId,String types) {
        if(types.equals("confirm")) {
            DatabaseUserManager
                    .getInstance()
                    .getUser(userId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String token = (String) snapshot.child("token").getValue();
                            String title = "Xác nhận sản phẩm thành công";
                            String body = "Đơn hàng " + txtProductName.getText().toString() +
                                    " của bạn đã được xác nhận thành công, chờ nhận hàng";
                            if(token != null) {
                                FcmNotificationsSender notificationsSender =
                                        new FcmNotificationsSender(token, title, body
                                                , getApplicationContext(), OrderDetailManagerActivity.this);
                                notificationsSender.SendNotifications();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        else if(types.equals("cancel")){
            DatabaseUserManager
                    .getInstance()
                    .getUser(userId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String token = (String) snapshot.child("token").getValue();
                            String title = "Huỷ xác nhận ";
                            String body = "Đơn hàng " + txtProductName.getText().toString() +
                                    " của bạn đã bị hủy do sản phẩm đã hết hàng";
                            if(token != null) {
                                FcmNotificationsSender notificationsSender =
                                        new FcmNotificationsSender(token, title, body
                                                , getApplicationContext(), OrderDetailManagerActivity.this);
                                notificationsSender.SendNotifications();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, OrderManagerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}