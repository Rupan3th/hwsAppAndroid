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
import com.hws.hwsappandroid.model.GoodsDetail;
import com.hws.hwsappandroid.model.Params;
import com.hws.hwsappandroid.ui.ImageDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GoodsDetailImagesAdapter extends RecyclerView.Adapter<GoodsDetailImagesAdapter.ViewHolder> {
    private Context context;
    public ArrayList<GoodsDetail> models;
    private ItemClickListener mClickListener;

    public GoodsDetailImagesAdapter(Context context) {
        this.context = context;
        this.models = new ArrayList<>();
    }

    public void setData(ArrayList<GoodsDetail> list) {
        models = list;
        notifyDataSetChanged();
    }

    @Override
    public GoodsDetailImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_detail_image, parent, false);
        return new GoodsDetailImagesAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsDetailImagesAdapter.ViewHolder holder, int position) {
        GoodsDetailImagesAdapter.ViewHolder viewHolder = (GoodsDetailImagesAdapter.ViewHolder) holder;

        try {
            Picasso.get().load(models.get(position).goodsDetailImg).into(viewHolder.goods_detail_image);
        }catch (Exception e){}

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        ImageView goods_detail_image;

        public ViewHolder(View itemView) {
            super(itemView);
            goods_detail_image = (ImageView) itemView.findViewById(R.id.goods_detail);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onClick(view, getPosition()); // call the onClick in the OnItemClickListener
        }

    }

    public GoodsDetail getItem(int id) {
        return models.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

}
