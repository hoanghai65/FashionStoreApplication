package com.hoanghai.fashionstoreapplication.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hoanghai.fashionstoreapplication.R;
import com.hoanghai.fashionstoreapplication.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{
    private Context context;
    private List<Product> products;
    private int layoutRes;
    private OnItemClickListener onItemClickListener;

    public ProductAdapter(Context context, List<Product> products, int layoutRes) {
        this.context = context;
        this.products = products;
        this.layoutRes = layoutRes;

    }
    public ProductAdapter(Context context, int layoutRes, List<Product> products
            , OnItemClickListener onItemClickListener) {
        this.context = context;
        this.products = products;
        this.layoutRes = layoutRes;
        this.onItemClickListener = onItemClickListener;
    }
    public void setList(List<Product> list) {
        this.products = list;
        notifyDataSetChanged();
    }
    public List<Product> getList(){
        return this.products;
    }

    public void addProduct(Product product){
        products.add(0,product);
        notifyItemChanged(0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutRes, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        Log.d("HDH","product = " + product.toString() + product.getPrice());
        holder.txtPrice.setText(product.getPrice()+" đ");
        holder.txtName.setText(product.getName());
        holder.txtAmount.setText("x"+String.valueOf(product.getAmount()));
        Glide.with(holder.itemView)
                .load(product.getImage())
                .placeholder(R.drawable.ic_uploadimage)
                .centerCrop()
                .into(holder.imvIcon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(product);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemLongClick(product);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imvIcon;
        private TextView txtPrice;
        private TextView txtName;
        private TextView txtAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imvIcon = itemView.findViewById(R.id.ivProduct);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtName = itemView.findViewById(R.id.txtProductName);
            txtAmount = itemView.findViewById(R.id.txtItemAmount);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
        void onItemLongClick(Product product);
    }
}
