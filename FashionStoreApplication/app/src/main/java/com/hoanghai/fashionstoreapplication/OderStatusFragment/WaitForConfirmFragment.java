package com.hoanghai.fashionstoreapplication.OderStatusFragment;

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
import com.hoanghai.fashionstoreapplication.DatabaseManager.ProgressDialogManager;
import com.hoanghai.fashionstoreapplication.DatabaseManager.WrapContentLinearLayoutManager;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseOrderManager;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseUserManager;
import com.hoanghai.fashionstoreapplication.OrderDetailActivity;
import com.hoanghai.fashionstoreapplication.R;
import com.hoanghai.fashionstoreapplication.fragment.BasketFragment;
import com.hoanghai.fashionstoreapplication.model.Order;

import java.util.ArrayList;
import java.util.List;


public class WaitForConfirmFragment extends Fragment implements
        OrderAdapter.OnItemClickListener {
    private View view;
    private RecyclerView rcvOrder;
    private List<Order> orders = new ArrayList<>();
    private List<String> listOrderKey = new ArrayList<>();
    private OrderAdapter orderAdapter;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wait_for_confirm, container, false);

        return view;
    }

    @Override
    public void onStart() {
        setUpRcv(view);
        super.onStart();
    }

    private void setUpRcv(View view) {
        loadOrderData();
        orderAdapter = new OrderAdapter(getContext()
                , R.layout.item_order, orders, this);
        rcvOrder = view.findViewById(R.id.rcvOrder);
        rcvOrder.setAdapter(orderAdapter);
        rcvOrder.setLayoutManager
                (new WrapContentLinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
    }

    private void loadOrderData() {

        userId = DatabaseUserManager.getInstance().getUserUid();
        DatabaseOrderManager
                .getInstance()
                .getListOrder(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String orderKey = data.getKey();
                            addOrderData(orderKey);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void addOrderData(String orderKey) {

        DatabaseOrderManager
                .getInstance()
                .getOrder(userId, orderKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Order order = snapshot.getValue(Order.class);
                        int position = listOrderKey.indexOf(orderKey);
                        if (order != null) {
                            if(!listOrderKey.contains(orderKey)
                                    && order.getStatus().equals(BasketFragment.WAIT_FOR_CONFIRM)) {
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
        bundle.putSerializable(Order.ORDER_KEY,order);
        Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public void onRemoveOrder(Order order) {

    }

}