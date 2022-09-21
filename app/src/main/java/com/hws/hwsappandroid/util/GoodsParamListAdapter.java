package com.hws.hwsappandroid.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.Params;

import java.util.ArrayList;

public class GoodsParamListAdapter extends RecyclerView.Adapter<GoodsParamListAdapter.ViewHolder> {
    private Context context;
    public ArrayList<Params> models;

    public GoodsParamListAdapter(Context context) {
        this.context = context;
        this.models = new ArrayList<>();
    }

    public void setData(ArrayList<Params> list) {
        models = list;
        notifyDataSetChanged();
    }

    @Override
    public GoodsParamListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_param_item, parent, false);
        return new GoodsParamListAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsParamListAdapter.ViewHolder holder, int position) {
        GoodsParamListAdapter.ViewHolder viewHolder = (GoodsParamListAdapter.ViewHolder) holder;
        viewHolder.param_key.setText(models.get(position).key);
        viewHolder.param_value.setText(models.get(position).value);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        TextView param_key;
        TextView param_value;

        public ViewHolder(View itemView) {
            super(itemView);
            param_key = (TextView) itemView.findViewById(R.id.param_key);
            param_value = (TextView) itemView.findViewById(R.id.param_value);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
//            if (clickListener != null) clickListener.onClick(view, getPosition()); // call the onClick in the OnItemClickListener
        }

    }

}
