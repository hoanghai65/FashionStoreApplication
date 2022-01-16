package com.hoanghai.fashionstoreapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.hoanghai.fashionstoreapplication.Adapter.ProductAdapter;
import com.hoanghai.fashionstoreapplication.fragment.HomeFragment;
import com.hoanghai.fashionstoreapplication.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ListProductCategoryActivity extends AppCompatActivity
        implements ProductAdapter.OnItemClickListener {

    private RecyclerView rcvProductCategory;
    private List<Product> products = new ArrayList<>();
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product_category);
        Bundle bundle = getIntent().getExtras();
        products = (List<Product>) bundle.getSerializable(HomeFragment.KEY_CATEGORY);
        setUpRecyclerView();

    }
    void setUpRecyclerView(){
        productAdapter = new ProductAdapter(this
                ,R.layout.item_product,products,this);
        rcvProductCategory = findViewById(R.id.rcvProductCategory);
        rcvProductCategory.setAdapter(productAdapter);
        rcvProductCategory.setLayoutManager
                (new StaggeredGridLayoutManager
                        (2,StaggeredGridLayoutManager.VERTICAL));
    }



    @Override
    public void onItemClick(Product product) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(HomeFragment.KEY_PRODUCT,product);
        Intent intent = new Intent(this, ItemProductDetailsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Product product) {

    }
}