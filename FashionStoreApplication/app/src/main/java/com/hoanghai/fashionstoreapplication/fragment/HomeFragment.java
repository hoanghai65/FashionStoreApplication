package com.hoanghai.fashionstoreapplication.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hoanghai.fashionstoreapplication.Adapter.CategoryAdapter;
import com.hoanghai.fashionstoreapplication.Adapter.ProductAdapter;
import com.hoanghai.fashionstoreapplication.Adapter.SliderAdapter;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseProductManager;
import com.hoanghai.fashionstoreapplication.ItemProductDetailsActivity;
import com.hoanghai.fashionstoreapplication.ListProductCategoryActivity;
import com.hoanghai.fashionstoreapplication.R;
import com.hoanghai.fashionstoreapplication.model.Category;
import com.hoanghai.fashionstoreapplication.model.Product;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment
        implements CategoryAdapter.onClickCategory, ProductAdapter.OnItemClickListener {
    public static final String KEY_PRODUCT = "KEY_PRODUCT";
    public static final String KEY_CATEGORY = "KEY_CATEGORY";
    private SliderView sliderView;
    private SliderAdapter sliderAdapter;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private List<Product> products = new ArrayList<>();
    private List<Product> rootProducts = new ArrayList<>();

    private RecyclerView rcvCategory;
    private RecyclerView rcvListPopularProduct;
    private List<Category> categoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        sliderView = view.findViewById(R.id.imageSlider);
        rcvCategory = view.findViewById(R.id.rcvCategory);
        rcvListPopularProduct = view.findViewById(R.id.rcvListPopularProduct);

        setUpSlider();
        setUpCategory();
        setUpListPopularProduct();
        return view;

    }



    private void setUpSlider(){
        sliderAdapter = new SliderAdapter(getContext());
        sliderView.setSliderAdapter(sliderAdapter);
        sliderAdapter.addItem(new SliderAdapter.SliderItem(R.drawable.img_sale1));
        sliderAdapter.addItem(new SliderAdapter.SliderItem(R.drawable.img_sale2));
        sliderAdapter.addItem(new SliderAdapter.SliderItem(R.drawable.img_sale2022));
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4);
        sliderView.startAutoCycle();
    }

    private void setUpCategory(){
        initDataCategory();
        categoryAdapter =
                new CategoryAdapter(getContext(),
                        R.layout.item_category,categoryList
                        ,this);
        rcvCategory.setAdapter(categoryAdapter);
        rcvCategory.
                setLayoutManager(new LinearLayoutManager(getContext()
                        ,LinearLayoutManager.HORIZONTAL,false));
    }
    private void setUpListPopularProduct(){
        productAdapter =
                new ProductAdapter(getContext()
                        ,R.layout.item_product,products
                        ,this);
        rcvListPopularProduct.setAdapter(productAdapter);
        rcvListPopularProduct.setLayoutManager(
                new StaggeredGridLayoutManager(
                        2,StaggeredGridLayoutManager.VERTICAL
                       )
        );
        loadProductData();
    }
    private void loadProductData(){
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
                        int position = products.indexOf(product);
                        if (product != null) {
                            if(position != -1){
                                productAdapter.notifyItemChanged(position);
                            }else {
                                products.add(product);
                                productAdapter.notifyItemInserted(products.size() - 1);
                                setAmountForCategory(product.getCategory());
                            }
                        }
                        listPopularProduct(products);
                        rootProducts = products;

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public void listPopularProduct(List<Product> list){
        List<Product> list1 = new ArrayList<>();
        if(list.size() < 10){
            return;
        }
        else {
            int n = list.size() - 10;
            for(int i = list.size() - 1; i >= n ; i--){
                list1.add(list.get(i));
            }
            productAdapter.setList(list1);
            productAdapter.notifyDataSetChanged();
        }
    }




    private void initDataCategory(){
        categoryList = new ArrayList<>();
        categoryList.add(new Category
                (R.drawable.ic_clothes,"Áo",0,"c"));
        categoryList.add(new Category
                (R.drawable.ic_trousers,"Quần",0,"t"));
        categoryList.add(new Category
                (R.drawable.ic_bag,"Túi sách",0,"b"));
        categoryList.add(new Category
                (R.drawable.ic_sandal,"Dép",0,"sd"));
        categoryList.add(new Category
                (R.drawable.ic_shoes,"Giày",0,"s"));


    }

    private void setAmountForCategory(String category){
        for (int i = 0 ; i < categoryList.size(); i++){
            if (categoryList.get(i).getId().equals(category)){
                categoryList.get(i).setItemAmount(categoryList.get(i).getItemAmount() + 1);
                categoryAdapter.notifyItemChanged(i);
            }
        }

    }



    @Override
    public void onclickItem(Category category) {
        List<Product> listProductByCategory = new ArrayList<>();
        String id = category.getId();
        for(int i = 0; i < rootProducts.size();i++){
            if(rootProducts.get(i).getCategory().equals(id)){
                listProductByCategory.add(rootProducts.get(i));
            }
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_CATEGORY, (Serializable) listProductByCategory);
        Intent intent = new Intent(getActivity(), ListProductCategoryActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public void onItemClick(Product product) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_PRODUCT,product);
        Intent intent = new Intent(getActivity(), ItemProductDetailsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public void onItemLongClick(Product product) {

    }
    public void onBackPressed() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}