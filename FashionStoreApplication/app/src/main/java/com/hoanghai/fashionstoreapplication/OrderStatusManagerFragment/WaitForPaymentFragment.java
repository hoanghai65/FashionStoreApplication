package com.hoanghai.fashionstoreapplication.OrderStatusManagerFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hoanghai.fashionstoreapplication.Adapter.OrderAdapter;
import com.hoanghai.fashionstoreapplication.DatabaseManager.WrapContentLinearLayoutManager;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseOrderManager;
import com.hoanghai.fashionstoreapplication.OrderDetailActivity;
import com.hoanghai.fashionstoreapplication.OrderDetailManagerActivity;
import com.hoanghai.fashionstoreapplication.OrderManagerActivity;
import com.hoanghai.fashionstoreapplication.R;
import com.hoanghai.fashionstoreapplication.fragment.BasketFragment;
import com.hoanghai.fashionstoreapplication.model.Order;

import java.util.ArrayList;
import java.util.List;


public class WaitForPaymentFragment extends Fragment
        implements OrderAdapter.OnItemClickListener {
    private View view;
    private RecyclerView rcvOrder;
    private List<Order> orders = new ArrayList<>();
    private Order rootOrder;
    private List<String> listOrderKey = new ArrayList<>();
    private OrderAdapter orderAdapter;
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
                                    && order.getStatus().equals(OrderManagerActivity.WAIT_FOR_PAYMENT)) {
                                orderAdapter.addItem(order);
                                listOrderKey.add(orderKey);


                            }else{
                                orderAdapter.notifyItemInserted(position);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }





    @Override
    public void onItemClick(Order order) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Order.ORDER_KEY_ADMIN,order);
        Intent intent = new Intent(getActivity(), OrderDetailManagerActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public void onRemoveOrder(Order order) {

    }



}