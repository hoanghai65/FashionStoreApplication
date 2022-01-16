package com.hoanghai.fashionstoreapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.hoanghai.fashionstoreapplication.Adapter.StatusAdapter;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseOrderManager;
import com.hoanghai.fashionstoreapplication.OrderStatusManagerFragment.CancelFragment;
import com.hoanghai.fashionstoreapplication.OrderStatusManagerFragment.WaitForConfirmFragment;
import com.hoanghai.fashionstoreapplication.OrderStatusManagerFragment.WaitForPaymentFragment;
import com.hoanghai.fashionstoreapplication.model.Order;

public class OrderManagerActivity extends AppCompatActivity {
    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public final static String WAIT_FOR_CONFIRM = "Đơn hàng chờ xác thực";
    public final static String WAIT_FOR_PAYMENT = "Đơn hàng chờ thanh toán";
    public final static String CANCEL = "Đơn hàng đã hủy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_manager);
        setUp();
    }



    private void setUp(){
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPage);

        tabLayout.setupWithViewPager(viewPager);

        StatusAdapter statusAdapter =
                new StatusAdapter(getSupportFragmentManager()
                        , FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        statusAdapter.addFragment(new WaitForConfirmFragment(),WAIT_FOR_CONFIRM);
        statusAdapter.addFragment(new WaitForPaymentFragment(),WAIT_FOR_PAYMENT);
        statusAdapter.addFragment(new CancelFragment(),CANCEL);

        viewPager.setAdapter(statusAdapter);
    }


}