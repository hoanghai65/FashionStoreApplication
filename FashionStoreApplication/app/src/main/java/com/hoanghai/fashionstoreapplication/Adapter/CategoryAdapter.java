package com.hoanghai.fashionstoreapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.hoanghai.fashionstoreapplication.R;
import com.hoanghai.fashionstoreapplication.model.Category;
import com.hoanghai.fashionstoreapplication.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context context;
    private int layoutRes;
    private List<Category> categories;
    private onClickCategory onClickCategory;



    public CategoryAdapter(Context context, int layoutRes, List<Category> categories, CategoryAdapter.onClickCategory onClickCategory) {
        this.context = context;
        this.layoutRes = layoutRes;
        this.categories = categories;
        this.onClickCategory = onClickCategory;
    }



    public void addItem(Category category){
        categories.add(category);
        notifyDataSetChanged();
    }
    public void removeItem(Category category){
        int position = categories.indexOf(category);
        categories.remove(position);
        notifyItemRemoved(position);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutRes,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.ivRes.setImageResource(category.getIvRes());
        holder.txtName.setText(category.getName());
        holder.txtItemAmount.setText(category.getItemAmount() + " sản phẩm");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onClickCategory != null){
                    onClickCategory.onclickItem(category);
                }
            }
        });

    }



    @Override
    public int getItemCount() {

        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivRes;
        private TextView txtName;
        private TextView txtItemAmount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRes = itemView.findViewById(R.id.ivCategory);
            txtName = itemView.findViewById(R.id.txtName);
            txtItemAmount = itemView.findViewById(R.id.txtItemAmount);

        }
    }

    public interface onClickCategory{
        void onclickItem(Category category);
    }

}
