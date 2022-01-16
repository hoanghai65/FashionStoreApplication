package com.hoanghai.fashionstoreapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hoanghai.fashionstoreapplication.DatabaseManager.DatabaseProductManager;
import com.hoanghai.fashionstoreapplication.R;
import com.hoanghai.fashionstoreapplication.model.Category;
import com.hoanghai.fashionstoreapplication.model.Order;
import com.hoanghai.fashionstoreapplication.model.Product;

import java.util.Collections;
import java.util.List;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context context;
    private int layoutRes;
    private List<Order> orders;
    private OnItemClickListener onItemClickListener;

    public OrderAdapter(Context context, int layoutRes, List<Order> orders, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.layoutRes = layoutRes;
        this.orders = orders;
        this.onItemClickListener = onItemClickListener;
    }

    public void setOrders(List<Order> orders){
        this.orders = orders;
        notifyDataSetChanged();
    }

    public void addItem(Order order){
        orders.add(0,order);
        notifyItemChanged(0);
    }
    public void removeItem(Order order){
        int position = orders.indexOf(order);
        orders.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutRes,parent,false);

        return new OrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        String productKey = order.getProductKey();
        holder.txtTotalPrice.setText(order.getTotalPrice());
        holder.txtStatus.setText(order.getStatus());
        DatabaseProductManager
                .getInstance()
                .getInfoProduct(productKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Product product = snapshot.getValue(Product.class);
                        if(product != null) {
                            holder.txtName.setText(product.getName());
                            holder.txtItemAmount.setText("x" + order.getAmount());
                            Glide.with(holder.itemView)
                                    .load(product.getImage())
                                    .placeholder(R.drawable.ic_uploadimage)
                                    .centerCrop()
                                    .into(holder.ivRes);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(order);

                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(onItemClickListener != null){
                    onItemClickListener.onRemoveOrder(order);

                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivRes;
        private TextView txtName;
        private TextView txtItemAmount;
        private TextView txtTotalPrice;
        private TextView txtStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRes = itemView.findViewById(R.id.ivProduct);
            txtName = itemView.findViewById(R.id.txtProductName);
            txtItemAmount = itemView.findViewById(R.id.txtItemAmount);
            txtTotalPrice = itemView.findViewById(R.id.txtTotalPrice);
            txtStatus = itemView.findViewById(R.id.txtStatusProduct);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Order order);
        void onRemoveOrder(Order order);
    }
}
