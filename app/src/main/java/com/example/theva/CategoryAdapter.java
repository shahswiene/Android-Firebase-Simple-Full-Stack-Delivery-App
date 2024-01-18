package com.example.theva;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class CategoryAdapter extends ArrayAdapter<CategoryItem> {
    public CategoryAdapter(Context context, ArrayList<CategoryItem> categoryList) {
        super(context, 0, categoryList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.category_spinner_row, parent, false
            );
        }

        ImageView imageViewCategory = convertView.findViewById(R.id.image_view_category);
        TextView textViewName = convertView.findViewById(R.id.text_view_category);

        CategoryItem currentItem = getItem(position);

        if (currentItem != null) {
            Glide.with(imageViewCategory.getContext())
                    .load(currentItem.getImageUrl())
                    .into(imageViewCategory);
            textViewName.setText(currentItem.getCategoryName());
        }

        return convertView;
    }



}

