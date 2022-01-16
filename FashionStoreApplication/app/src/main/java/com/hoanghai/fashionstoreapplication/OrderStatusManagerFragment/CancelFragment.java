package com.hoanghai.fashionstoreapplication.OrderStatusManagerFragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hoanghai.fashionstoreapplication.Adapter.OrderAdapter;
import com.hoanghai.fashionstoreapplication.CreateProductActivity;
import com.hoanghai.fashionstoreapplication.DatabaseManager.ProgressDialogManager;
import com.hoanghai.fashionstoreapplication.DatabaseManager.WrapContentLinearLayoutManager;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseOrderManager;
import com.hoanghai.fashionstoreapplication.ItemProductDetailsActivity;
import com.hoanghai.fashionstoreapplication.OrderDetailActivity;
import com.hoanghai.fashionstoreapplication.OrderManagerActivity;
import com.hoanghai.fashionstoreapplication.R;
import com.hoanghai.fashionstoreapplication.model.Order;

import java.util.ArrayList;
import java.util.List;


public class CancelFragment extends Fragment
        implements OrderAdapter.OnItemClickListener {
    private View view;
    private RecyclerView rcvOrder;
    private List<Order> orders = new ArrayList<>();
    private Order rootOrder;
    private List<String> listOrderKey = new ArrayList<>();
    private OrderAdapter orderAdapter;
    private ProgressDialogManager dialogManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wait_confirm_manager, container, false);
        setUpRcv(view);
        return view;
    }

    private void setUpRcv(View view) {
        loadUserData();
        orderAdapter = new OrderAdapter(getContext()
                , R.layout.item_order, orders, this);
        rcvOrder = view.findViewById(R.id.rcvOrder);
        rcvOrder.setAdapter(orderAdapter);
        rcvOrder.setLayoutManager
                (new WrapContentLinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
    }

    private void loadUserData() {
        DatabaseOrderManager
                .getInstance()
                .getListOrderUser()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String userID = data.getKey();
                            loadOrderData(userID);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void loadOrderData(String userID) {
        if(getContext() != null) {
            dialogManager = ProgressDialogManager.getInstance(getContext());
        }
        DatabaseOrderManager
                .getInstance()
                .getListOrder(userID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data : snapshot.getChildren()){
                            String orderKey = data.getKey();
                            addOrderData(userID,orderKey);
                        }
                        dialogManager.setDismissProgressDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialogManager.setDismissProgressDialog();
                    }
                });
        dialogManager.setDismissProgressDialog();
    }

    public void addOrderData(String userId,String orderKey){

        DatabaseOrderManager
                .getInstance()
                .getOrder(userId,orderKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Order order = snapshot.getValue(Order.class);
                        int position = listOrderKey.indexOf(orderKey);
                        if (order != null) {
                            if(!listOrderKey.contains(orderKey)
                                    && order.getStatus().equals(OrderManagerActivity.CANCEL)) {
                                orderAdapter.addItem(order);
                                listOrderKey.add(orderKey);


                            }else{
                                orderAdapter.notifyItemInserted(position);
                            }
                        }
                        dialogManager.setDismissProgressDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialogManager.setDismissProgressDialog();
                    }
                });
        dialogManager.setDismissProgressDialog();

    }





    @Override
    public void onItemClick(Order order) {
        Bundle bundle = new Bundle();
        bundle.putString(Order.ORDER_KEY,order.getProductKey());
        Intent intent = new Intent(getContext(), ItemProductDetailsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public void onRemoveOrder(Order order) {
        ProgressDialog progressDialog =
                ProgressDialog.show(getContext(),
                        null, null
                        , true);
        progressDialog.setContentView(R.layout.design_progress);
        progressDialog.getWindow().setBackgroundDrawable
                (new ColorDrawable(android.graphics.Color.TRANSPARENT));


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Bạn muốn xóa đơn hàng này không ?");
        builder.setBackground(getResources().getDrawable(R.drawable.borderbackground));
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressDialog.show();
                int index = orders.indexOf(order);
                if (index != -1) {
                    DatabaseOrderManager
                            .getInstance()
                            .getOrder(order.getUidUser(),order.getIdOrder())
                            .removeValue();
                    orderAdapter.removeItem(order);
                    Log.d("PTH", " remove position = " + index);
                    Toast.makeText(getContext(),
                            "Xóa đơn hàng thành công", Toast.LENGTH_SHORT).show();

                }
                progressDialog.dismiss();
                dialogInterface.dismiss();

            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                progressDialog.dismiss();
            }
        });
        progressDialog.dismiss();
        builder.show();

    }



}