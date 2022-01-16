package com.hoanghai.fashionstoreapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hoanghai.fashionstoreapplication.Adapter.ProductAdapter;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseProductManager;
import com.hoanghai.fashionstoreapplication.DatabaseManager.ProgressDialogManager;
import com.hoanghai.fashionstoreapplication.fragment.HomeFragment;
import com.hoanghai.fashionstoreapplication.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ListProductForUserActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener {
    private RecyclerView rcvAllProduct;
    private List<Product> products = new ArrayList<>();
    private ProductAdapter productAdapter;
    private List<String> listKey = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product_for_user);
        initDataView();
        loadProductData();
    }

    private void loadProductData() {
        DatabaseProductManager
                .getInstance()
                .getListProduct()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {

                            String productKey = data.getKey();
                            addProductInfo(productKey);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ListProductForUserActivity.this,
                                "Lỗi xảy ra " + error.getMessage()
                                , Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void addProductInfo(String productKey) {
        DatabaseProductManager
                .getInstance()
                .getInfoProduct(productKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
                            if(!listKey.contains(productKey)){
                                productAdapter.addProduct(product);
                                listKey.add(0,productKey);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    private void initDataView() {
        rcvAllProduct = findViewById(R.id.rcvAllListProduct);
        productAdapter = new ProductAdapter(this
                , R.layout.item_product
                , products, this);
        rcvAllProduct.setAdapter(productAdapter);
        rcvAllProduct.setLayoutManager
                (new StaggeredGridLayoutManager
                        (2,StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    public void onItemClick(Product product) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(HomeFragment.KEY_PRODUCT, product);
        Intent intent = new Intent(this, ItemProductDetailsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Product product) {

    }
}