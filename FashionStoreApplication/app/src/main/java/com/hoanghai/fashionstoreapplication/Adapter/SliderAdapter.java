package com.hoanghai.fashionstoreapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.hoanghai.fashionstoreapplication.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.ViewHolder> {

    private Context context;
    private List<SliderItem> mSliderItems = new ArrayList<>();

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public void renewItems(List<SliderItem> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(SliderItem sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_slider, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        SliderItem sliderItem = mSliderItems.get(position);
        viewHolder.iv_slider.setImageResource(sliderItem.getIvRes());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    class ViewHolder extends SliderViewAdapter.ViewHolder {
        private ImageView iv_slider;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_slider = itemView.findViewById(R.id.iv_slider);
        }
    }

    public static class SliderItem {
        private int ivRes;

        public SliderItem(int ivRes) {
            this.ivRes = ivRes;
        }

        public int getIvRes() {
            return ivRes;
        }

        public void setIvRes(int ivRes) {
            this.ivRes = ivRes;
        }

    }
}
