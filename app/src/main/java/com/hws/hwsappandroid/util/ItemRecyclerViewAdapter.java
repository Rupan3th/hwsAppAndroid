package com.hws.hwsappandroid.util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.Children_level_2;
import com.hws.hwsappandroid.ui.ImageDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ItemViewHolder> {
    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView itemLabel;
        private ImageView itemImage;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemLabel = (TextView) itemView.findViewById(R.id.item_label);
            itemImage = (ImageView) itemView.findViewById(R.id.item_image);
        }
    }

    private Context context;
    private ArrayList<Children_level_2> arrayList;
    private String sectionLabel;

    public ItemRecyclerViewAdapter(Context context, ArrayList<Children_level_2> arrayList, String sectionLabel) {
        this.context = context;
        this.arrayList = arrayList;
        this.sectionLabel = sectionLabel;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_row_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        String itemLabel = arrayList.get(position).categoryName;
        holder.itemLabel.setText(itemLabel);
        String itemImage = arrayList.get(position).categoryImg;
        if(itemImage != ""){
            Picasso.get().load(arrayList.get(position).categoryImg).into(holder.itemImage);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You clicked "+ itemLabel +" of " + sectionLabel, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


}