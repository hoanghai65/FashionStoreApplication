package com.hoanghai.fashionstoreapplication.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hoanghai.fashionstoreapplication.Adapter.ProductAdapter;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseProductManager;
import com.hoanghai.fashionstoreapplication.ItemProductDetailsActivity;
import com.hoanghai.fashionstoreapplication.R;
import com.hoanghai.fashionstoreapplication.model.Product;

import java.util.ArrayList;
import java.util.List;

public class SearchProductFragment extends Fragment   implements ProductAdapter.OnItemClickListener
        , View.OnClickListener {
        private RecyclerView rcvAllProduct;
        private List<Product> products = new ArrayList<>();
        private List<Product> RootProducts = new ArrayList<>();
        private ProductAdapter productAdapter;
        private List<String> listKey = new ArrayList<>();
        private TextInputLayout tilSearch;
        private AutoCompleteTextView actvSearch;

        private Chip chipClothes, chipTrouser, chipBag, chipShoes, chipSandal, chipAllProduct;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_search_product, container, false);
            initDataView(view);
            setUp(view);
            loadProductData();
            findProductBySearchKey();
            return view;
        }

        private void setUp(View view) {
            chipClothes = view.findViewById(R.id.chipClothes);
            chipTrouser = view.findViewById(R.id.chipTrouser);
            chipBag = view.findViewById(R.id.chipBag);
            chipShoes = view.findViewById(R.id.chipShoes);
            chipSandal = view.findViewById(R.id.chipSandal);
            chipAllProduct = view.findViewById(R.id.chipAllProduct);
            tilSearch = view.findViewById(R.id.tilSearch);
            actvSearch = view.findViewById(R.id.actvSearch);

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


        private void initDataView(View view) {
            rcvAllProduct = view.findViewById(R.id.rcvAllListProduct);
            productAdapter = new ProductAdapter(getContext()
                    , R.layout.item_list_product
                    , products, this);
            rcvAllProduct.setAdapter(productAdapter);
            rcvAllProduct.setLayoutManager(new LinearLayoutManager(getContext()));
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
            Intent intent = new Intent(getActivity(), ItemProductDetailsActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onItemLongClick(Product product) {


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

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.chipClothes:
                    if (chipClothes.isCheckable()) {
                        productAdapter.setList(ChangeListAdapterByCategory("c"));
                    }
                    break;
                case R.id.chipBag:
                    if (chipBag.isCheckable()) {
                        productAdapter.setList(ChangeListAdapterByCategory("b"));
                    }
                    break;
                case R.id.chipShoes:
                    if (chipShoes.isCheckable()) {
                        productAdapter.setList(ChangeListAdapterByCategory("s"));
                    }
                    break;
                case R.id.chipTrouser:
                    if (chipTrouser.isCheckable()) {
                        Log.d("HDH", "click quan");
                        productAdapter.setList(ChangeListAdapterByCategory("t"));

                    }
                    break;
                case R.id.chipSandal:
                    if (chipSandal.isCheckable()) {
                        productAdapter.setList(ChangeListAdapterByCategory("sd"));
                    }
                    break;
                case R.id.chipAllProduct:
                    if (chipAllProduct.isCheckable()) {
                        productAdapter.setList(RootProducts);
                    }
                    break;

            }
        }

    public void onBackPressed() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(this);
        trans.commit();
        manager.popBackStack();
    }
    }