package com.hoanghai.fashionstoreapplication.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.hoanghai.fashionstoreapplication.R;
import com.hoanghai.fashionstoreapplication.OderStatusFragment.CancelFragment;
import com.hoanghai.fashionstoreapplication.Adapter.StatusAdapter;
import com.hoanghai.fashionstoreapplication.OderStatusFragment.WaitForConfirmFragment;
import com.hoanghai.fashionstoreapplication.OderStatusFragment.WaitForPaymentFragment;

public class BasketFragment extends Fragment {

    public final static String WAIT_FOR_CONFIRM = "Đơn hàng chờ xác thực";
    public final static String WAIT_FOR_PAYMENT = "Đơn hàng chờ thanh toán";
    public final static String CANCEL = "Đơn hàng đã hủy";

    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_basket, container, false);
        setUp(view);
        return view;
    }
    public void setUp(View view){
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPage);

        tabLayout.setupWithViewPager(viewPager);

        StatusAdapter statusAdapter =
                new StatusAdapter(getChildFragmentManager()
                        , FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        statusAdapter.addFragment(new WaitForConfirmFragment(),WAIT_FOR_CONFIRM);
        statusAdapter.addFragment(new WaitForPaymentFragment(),WAIT_FOR_PAYMENT);
        statusAdapter.addFragment(new CancelFragment(),CANCEL);

        viewPager.setAdapter(statusAdapter);
    }
    public void onBackPressed() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(this);
        trans.commit();
        manager.popBackStack();
    }
}