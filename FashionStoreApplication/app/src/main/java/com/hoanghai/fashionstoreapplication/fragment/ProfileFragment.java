package com.hoanghai.fashionstoreapplication.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hoanghai.fashionstoreapplication.EditProfileActivity;
import com.hoanghai.fashionstoreapplication.HomeMainActivity;
import com.hoanghai.fashionstoreapplication.R;
import com.hoanghai.fashionstoreapplication.model.User;


public class ProfileFragment extends Fragment {
    private TextView txtName,txtEmail,txtPhoneNumber
            ,txtDate,txtGender;
    private User user;
    private boolean isResult = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        setUpProfile(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadUserInfo();
    }

    private void setUpProfile(View view){
        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPhoneNumber = view.findViewById(R.id.txtPhoneNumber);
        txtDate = view.findViewById(R.id.txtDate);
        txtGender = view.findViewById(R.id.txtGender);
    }

    private void loadUserInfo(){
        if(!isResult) {
            Bundle bundle = getArguments();
            Log.d("HDH", "bundel user info " + bundle);
            if (bundle != null) {
                user = (User) bundle.getSerializable(HomeMainActivity.USER_KEY);
                txtName.setText(user.getUserName());
                txtEmail.setText(user.getEmail());
                txtPhoneNumber.setText(user.getPhoneNumber());
                txtDate.setText("Ngày sinh: " + user.getBirthday());
                txtGender.setText("Giới tính: " + user.getGender());
            }
        }
        else {
            txtName.setText(user.getUserName());
            txtEmail.setText(user.getEmail());
            txtPhoneNumber.setText(user.getPhoneNumber());
            txtDate.setText("Ngày sinh: " + user.getBirthday());
            txtGender.setText("Giới tính: " + user.getGender());
        }


    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onBackPressed() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(this);
        trans.commit();
        manager.popBackStack();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == HomeMainActivity.REQUEST_PROFILE
                && resultCode == Activity.RESULT_OK
                && data != null){
                Bundle bundle = data.getExtras();
                if(bundle != null){
                    user = (User) bundle.getSerializable(EditProfileActivity.RESULT_KEY);
                    isResult = true;
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}