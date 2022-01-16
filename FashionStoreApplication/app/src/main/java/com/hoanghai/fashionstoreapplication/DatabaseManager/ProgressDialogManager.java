package com.hoanghai.fashionstoreapplication.DatabaseManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AppCompatActivity;

import com.hoanghai.fashionstoreapplication.GetInfoActivity;
import com.hoanghai.fashionstoreapplication.R;

public class ProgressDialogManager{
    private ProgressDialog dialog;
    public ProgressDialogManager(Context context){
        dialog =  ProgressDialog.show(context,
                null, null
                , true);
        dialog.setContentView(R.layout.design_progress);
        dialog.getWindow().setBackgroundDrawable
                (new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public static ProgressDialogManager getInstance(Context context){
        return  new ProgressDialogManager(context);
    }

    public void setShowProgressDialog(){
        dialog.show();
    }
    public void setDismissProgressDialog(){
        dialog.dismiss();
    }




}
