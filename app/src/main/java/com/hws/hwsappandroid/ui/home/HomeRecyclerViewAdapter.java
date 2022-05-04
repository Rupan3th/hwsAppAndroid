package com.hws.hwsappandroid.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Good> models;
    Context context;
    boolean stateHeader;
    private ItemClickListener clickListener;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;
    private final int TYPE_FOOTER = 2;

    // Constructor for initialization
    public HomeRecyclerViewAdapter(Context context, boolean stateHeader) {
        this.context = context;
        this.models = new ArrayList<>();
        this.stateHeader = stateHeader;
    }

    public void setData(ArrayList<Good> list) {
        models = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(stateHeader){
            if (position == models.size())
                return TYPE_FOOTER;
            else
                return TYPE_ITEM;
        }
        else{
            if (position == 0)
                return TYPE_HEADER;
            else if (position == models.size() + 1)
                return TYPE_FOOTER;
            else
                return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        // Returns number of items currently available in Adapter
        if(stateHeader){
            return models.size() + 1;
        }
        else{
            return models.size() + 2;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        RecyclerView.ViewHolder viewHolder;
        View view;
        if(stateHeader){
            if (viewType == TYPE_FOOTER) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer, parent, false);
                final ViewGroup.LayoutParams lp = view.getLayoutParams();
                StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
                sglp.setFullSpan(true);
                viewHolder = new FooterViewHolder(view);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, parent, false);
                viewHolder = new ViewHolder(view);
            }
        }
        else{
            if (viewType == TYPE_HEADER) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
                final ViewGroup.LayoutParams lp = view.getLayoutParams();
                StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
                sglp.setFullSpan(true);
                viewHolder = new HeaderViewHolder(view);
            } else if (viewType == TYPE_FOOTER) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer, parent, false);
                final ViewGroup.LayoutParams lp = view.getLayoutParams();
                StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
                sglp.setFullSpan(true);
                viewHolder = new FooterViewHolder(view);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, parent, false);
                viewHolder = new ViewHolder(view);
            }
        }

        return viewHolder;
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // TypeCast Object to int type
        if(stateHeader){
            if (holder instanceof FooterViewHolder) {
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            } else {
                ViewHolder viewHolder = (ViewHolder) holder;
                Picasso.get().load(models.get(position).goodsPic).into(viewHolder.images);
                viewHolder.product_info.setText(models.get(position).goodsName);
                viewHolder.price.setText("￥"+models.get(position).price);
            }
        }
        else{
            if (holder instanceof HeaderViewHolder) {
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            } else if (holder instanceof FooterViewHolder) {
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            } else {
                ViewHolder viewHolder = (ViewHolder) holder;
                Picasso.get().load(models.get(position-1).goodsPic).into(viewHolder.images);
                viewHolder.product_info.setText(models.get(position-1).goodsName);
                viewHolder.price.setText("￥"+models.get(position-1).price);
            }
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // Initializing the Views
    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View headerView) {
            super(headerView);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View footerView) {
            super(footerView);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        ImageView images;
        TextView product_info;
        TextView price;

        public ViewHolder(View itemView) {
            super(itemView);
            images = (ImageView) itemView.findViewById(R.id.image_product);
            product_info = (TextView) itemView.findViewById(R.id.text_product_info);
            price = (TextView) itemView.findViewById(R.id.text_price);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getPosition()); // call the onClick in the OnItemClickListener
        }

    }

}
