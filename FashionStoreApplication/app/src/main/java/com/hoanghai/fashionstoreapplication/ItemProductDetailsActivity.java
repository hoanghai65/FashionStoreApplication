package com.hoanghai.fashionstoreapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseProductManager;
import com.hoanghai.fashionstoreapplication.fragment.HomeFragment;
import com.hoanghai.fashionstoreapplication.model.Order;
import com.hoanghai.fashionstoreapplication.model.Product;

public class ItemProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtProductName,txtPrice,txtDetailDescription,txtCount;
    private ImageView imgProduct;
    private Chip chipSizeS,chipSizeM,chipSizeL,chipSizeXL;
    private LinearLayout lnBuy;
    private int rootAmount = 1;
    private Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_product_details);
        Bundle bundle = getIntent().getExtras();
        String productKey = bundle.getString(Order.ORDER_KEY);
        if(productKey != null){
            loadProductDataToOrder(productKey);
        }else {
            setUpProduct();
            setUpItemClickAble();
        }


    }
    private void setUpProduct(){
        imgProduct = findViewById(R.id.img_product);
        txtProductName = findViewById(R.id.txtProductName);
        txtPrice = findViewById(R.id.txtPrice);
        txtDetailDescription = findViewById(R.id.txtDetailDescription);
        txtCount = findViewById(R.id.txtCount);
        Bundle bundle = getIntent().getExtras();
        product = (Product) bundle.getSerializable(HomeFragment.KEY_PRODUCT);
        Glide.with(this)
                .load(product.getImage())
                .centerCrop()
                .placeholder(R.drawable.ic_uploadimage)
                .into(imgProduct);
        txtProductName.setText(product.getName());
        txtPrice.setText(product.getPrice() + " vnd");
        txtDetailDescription.setText(product.getDescription());


    }

    private void setUpItemClickAble(){
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


        if(product.getCategory().equals("c")){
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.chipSizeS:{
                if(chipSizeS.isCheckable()) {
                    Toast.makeText(this, "S", Toast.LENGTH_SHORT).show();
                }

                break;
            }
            case R.id.chipSizeM:{
                if(chipSizeS.isCheckable()) {
                    Toast.makeText(this, "M", Toast.LENGTH_SHORT).show();
                }

                break;
            }
            case R.id.chipSizeL:{
                if(chipSizeS.isCheckable()) {
                    Toast.makeText(this, "L", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.chipSizeXL:{
                if(chipSizeS.isCheckable()) {
                    Toast.makeText(this, "XL", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.lnBuy:{
                Bundle bundle = new Bundle();
                bundle.putSerializable(HomeFragment.KEY_PRODUCT,product);
                Intent intent = new Intent(this,BuyProductActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            }
        }
    }

    private void loadProductDataToOrder(String productKey){
        imgProduct = findViewById(R.id.img_product);
        txtProductName = findViewById(R.id.txtProductName);
        txtPrice = findViewById(R.id.txtPrice);
        txtDetailDescription = findViewById(R.id.txtDetailDescription);
        txtCount = findViewById(R.id.txtCount);

        DatabaseProductManager
                .getInstance()
                .getInfoProduct(productKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        product = snapshot.getValue(Product.class);
                        if(product != null){
                            Glide.with(ItemProductDetailsActivity.this)
                                    .load(product.getImage())
                                    .centerCrop()
                                    .placeholder(R.drawable.ic_uploadimage)
                                    .into(imgProduct);
                            txtProductName.setText(product.getName());
                            txtPrice.setText(product.getPrice() + " vnd");
                            txtDetailDescription.setText(product.getDescription());

                            setUpItemClickAble();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}