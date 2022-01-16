package com.hoanghai.fashionstoreapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
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

public class ListProductManagerActivity extends AppCompatActivity
        implements ProductAdapter.OnItemClickListener, View.OnClickListener {


    private RecyclerView rcvAllProduct;
    private List<Product> products = new ArrayList<>();
    private List<Product> RootProducts = new ArrayList<>();
    private ProductAdapter productAdapter;
    private List<String> listKey = new ArrayList<>();
    private boolean isAllProduct = false;

    private Chip chipClothes, chipTrouser, chipBag, chipShoes, chipSandal, chipAllProduct;

    private TextInputLayout tilSearch;
    private AutoCompleteTextView actvSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product_manager);
        initDataView();
        setUp();
        loadProductData();
        findProductBySearchKey();
    }

    private void setUp() {
        chipClothes = findViewById(R.id.chipClothes);
        chipTrouser = findViewById(R.id.chipTrouser);
        chipBag = findViewById(R.id.chipBag);
        chipShoes = findViewById(R.id.chipShoes);
        chipSandal = findViewById(R.id.chipSandal);
        chipAllProduct = findViewById(R.id.chipAllProduct);
        tilSearch = findViewById(R.id.tilSearch);
        actvSearch = findViewById(R.id.actvSearch);

        chipClothes.setOnClickListener(this);
        chipTrouser.setOnClickListener(this);
        chipBag.setOnClickListener(this);
        chipShoes.setOnClickListener(this);
        chipSandal.setOnClickListener(this);
        chipAllProduct.setOnClickListener(this);
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
                        RootProducts = products;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    private void initDataView() {
        rcvAllProduct = findViewById(R.id.rcvAllListProduct);
        productAdapter = new ProductAdapter(this
                , R.layout.item_list_product
                , products, this);
        rcvAllProduct.setAdapter(productAdapter);
        rcvAllProduct.setLayoutManager(new LinearLayoutManager(this));
    }

    private void findProductBySearchKey(){
        actvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0) {
                    List<Product> list = new ArrayList<>();
                    for (Product product : products) {
                        String searchKey = charSequence.toString().trim().toLowerCase();
                        if (product.getName().trim().toLowerCase().contains(searchKey)) {
                            list.add(product);
                        }
                    }
                    productAdapter.setList(list);
                }else{
                    productAdapter.setList(products);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


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
        ProgressDialog progressDialog =
                ProgressDialog.show(this,
                        null, null
                        , true);
        progressDialog.setContentView(R.layout.design_progress);
        progressDialog.getWindow().setBackgroundDrawable
                (new ColorDrawable(android.graphics.Color.TRANSPARENT));


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Bạn muốn xóa sản phẩm này không ?");
        builder.setBackground(getResources().getDrawable(R.drawable.borderbackground));
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressDialog.show();
                int index = products.indexOf(product);
                if (index != -1) {
                    String productKey = product.getImvRes();
                    String category = product.getCategory();
                    String key = listKey.get(index);


                    products.remove(index);
                    listKey.remove(index);
                    productAdapter.notifyItemRemoved(index);
                    removeProduct(productKey);
                    if(!isAllProduct){
                        Log.d("PTH", " update");
                        updateProduct(category);
                    }
                    Log.d("PTH", " remove position = " + index + " productKey " + productKey + " key = "+ key);
                    Toast.makeText(ListProductManagerActivity.this,
                            "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();

                }
                progressDialog.dismiss();
                dialogInterface.dismiss();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                progressDialog.dismiss();
            }
        });
        progressDialog.dismiss();
        builder.show();

    }

    private void removeProduct(String productKey){
        DatabaseProductManager
                .getInstance()
                .getInfoProduct(productKey)
                .removeValue();
    }

    private List<Product> ChangeListAdapterByCategory(String category) {
        List<Product> list = new ArrayList<>();
        for (Product product : products) {
            if (category.equals(product.getCategory())) {
                list.add(product);
            }
        }
        return list;
    }

    private void updateProduct(String category) {
        productAdapter.setList(ChangeListAdapterByCategory(category));
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.chipClothes:
                if (chipClothes.isCheckable()) {
                    updateProduct("c");
//                    productAdapter.setList(ChangeListAdapterByCategory("c"));
                    isAllProduct = false;
                }
                break;
            case R.id.chipBag:
                if (chipBag.isCheckable()) {
                    updateProduct("b");
//                    productAdapter.setList(ChangeListAdapterByCategory("b"));
                    isAllProduct = false;
                }
                break;
            case R.id.chipShoes:
                if (chipShoes.isCheckable()) {
                    updateProduct("s");
//                    productAdapter.setList(ChangeListAdapterByCategory("s"));
                    isAllProduct = false;
                }
                break;
            case R.id.chipTrouser:
                if (chipTrouser.isCheckable()) {
                    Log.d("HDH", "click quan");
                    updateProduct("t");
//                    productAdapter.setList(ChangeListAdapterByCategory("t"));
                    isAllProduct = false;
                }
                break;
            case R.id.chipSandal:
                if (chipSandal.isCheckable()) {
                    updateProduct("sd");
//                    productAdapter.setList(ChangeListAdapterByCategory("sd"));
                    isAllProduct = false;
                }
                break;
            case R.id.chipAllProduct:
                if (chipAllProduct.isCheckable()) {
                    productAdapter.setList(RootProducts);
                    isAllProduct = true;
                }
                break;

        }
    }


}