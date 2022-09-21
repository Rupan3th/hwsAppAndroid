package com.hws.hwsappandroid.util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.FavoriteGood;
import com.hws.hwsappandroid.ui.MyCollectionActivity;
import com.hws.hwsappandroid.ui.ProductDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyCollectionListAdapter extends RecyclerView.Adapter<MyCollectionListAdapter.ViewHolder> {
    private Context context;
    public ArrayList<FavoriteGood> models;
    private ItemClickListener mClickListener;

    public MyCollectionListAdapter(Context context) {
        this.context = context;
        this.models = new ArrayList<>();
    }

    public void setData(ArrayList<FavoriteGood> list) {
        models = list;
        notifyDataSetChanged();
    }

    @Override
    public MyCollectionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item_mode3, parent, false);
        return new MyCollectionListAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void onBindViewHolder(@NonNull MyCollectionListAdapter.ViewHolder holder, int position) {
        MyCollectionListAdapter.ViewHolder viewHolder = (MyCollectionListAdapter.ViewHolder) holder;
        int pos = position;

        try {
            Picasso.get()
                    .load(models.get(position).goodsSpecImg)
                    .resize(1000, 1000)
                    .centerCrop()
                    .into(viewHolder.images);
        }catch (Exception e){}

        try {
            viewHolder.product_info.setText(models.get(position).goodsName);
            String fPrice = models.get(position).price;
            int id = fPrice.indexOf(".");
            viewHolder.price.setText(fPrice.substring(0, id)+".");
            viewHolder.price_decimal_places.setText(fPrice.substring(id+1));
            viewHolder.text_product_option.setText(models.get(position).goodsSpec);
            viewHolder.text_address.setText("辽宁·沈阳");
        }catch (Exception e){}


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailProduct = new Intent(context, ProductDetailActivity.class);
                String pkId = models.get(pos).goodsId;
                detailProduct.putExtra("pkId", pkId);
                context.startActivity(detailProduct);
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        ImageView images;
        TextView product_info;
        TextView price;
        TextView text_product_option;
        TextView text_address;
        TextView price_decimal_places;

        public ViewHolder(View itemView) {
            super(itemView);
            images = (ImageView) itemView.findViewById(R.id.image_product);
            product_info = (TextView) itemView.findViewById(R.id.text_product_info);
            price = (TextView) itemView.findViewById(R.id.text_price);
            price_decimal_places = (TextView) itemView.findViewById(R.id.price_decimal_places);
            text_product_option = (TextView) itemView.findViewById(R.id.text_product_option);
            text_address = (TextView) itemView.findViewById(R.id.text_address);

//            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onClick(view, getPosition()); // call the onClick in the OnItemClickListener
        }

    }

//    public void setClickListener(ItemClickListener itemClickListener) {
//        this.mClickListener = itemClickListener;
//    }
}
