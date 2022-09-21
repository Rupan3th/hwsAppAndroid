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
import com.hws.hwsappandroid.model.Category_level_2;
import com.hws.hwsappandroid.model.Category_level_3;
import com.hws.hwsappandroid.model.Children_level_2;
import com.hws.hwsappandroid.ui.ChooseCategoryActivity;
import com.hws.hwsappandroid.ui.ImageDetailActivity;
import com.hws.hwsappandroid.ui.cart.ShoppingCartAssist;
import com.hws.hwsappandroid.ui.classification.ClassificationFragment;
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
    private ArrayList<Children_level_2> arrayList = new ArrayList<>();
    private String sectionLabel;

    public ItemRecyclerViewAdapter(Context context, String sectionLabel, ArrayList<Children_level_2> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.sectionLabel = sectionLabel;
    }

    public void setData(ArrayList<Children_level_2> list) {
        arrayList = list;
        notifyDataSetChanged();
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
//        String itemImage = "https://huawushang.oss-cn-beijing.aliyuncs.com/hws/category/P202206221131225814.png";
        String itemImage = arrayList.get(position).categoryImg;
        if(itemImage.equals("")) itemImage = "https://huawushang.oss-cn-beijing.aliyuncs.com/hws/category/P202206221131225814.png";

        try{
            Picasso.get()
                    .load(itemImage)
                    .resize(100, 100)
//                    .placeholder(R.drawable.cart)
//                    .centerCrop()
//                    .fit()
                    .into(holder.itemImage);
        }
        catch (Exception e){}

        int pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "You clicked "+ itemLabel +" of " + sectionLabel, Toast.LENGTH_SHORT).show();
                String categoryCode = arrayList.get(pos).categoryCode;
                int level = arrayList.get(pos).level;
                String categoryName = arrayList.get(pos).categoryName;
                Intent i = new Intent(v.getContext(), ChooseCategoryActivity.class);
                i.putExtra("level", level);
                i.putExtra("categoryCode",categoryCode);
                i.putExtra("categoryName",categoryName);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


}