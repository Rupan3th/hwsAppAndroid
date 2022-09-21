package com.hws.hwsappandroid.util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.FavoriteGood;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.Params;
import com.hws.hwsappandroid.ui.AfterSalesDetailActivity;
import com.hws.hwsappandroid.ui.StoreDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<Good> models;
    Context context;
    boolean stateHeader;
    int itemLayoutMode;
    private ItemClickListener clickListener;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;
    private final int TYPE_FOOTER = 2;

    // Constructor for initialization
    public RecyclerViewAdapter(Context context, boolean stateHeader, int itemLayoutMode) {
        this.context = context;
        this.models = new ArrayList<>();
        this.stateHeader = stateHeader;
        this.itemLayoutMode = itemLayoutMode;
    }

    public void setData(ArrayList<Good> list) {
        models = list;
        notifyDataSetChanged();
    }

    public Good getGoodInfo(int position){
        return models.get(position);
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
                if(itemLayoutMode == 1)
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item_mode1, parent, false);
                else if (itemLayoutMode == 2)
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item_mode2, parent, false);
                else if (itemLayoutMode == 3)
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item_mode3, parent, false);
                else if (itemLayoutMode == 4)
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item_mode4, parent, false);

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
                if(itemLayoutMode == 1)
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item_mode1, parent, false);
                else if (itemLayoutMode == 2)
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item_mode2, parent, false);
                else if (itemLayoutMode == 3)
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item_mode3, parent, false);
                else if (itemLayoutMode == 4)
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item_mode4, parent, false);
                viewHolder = new ViewHolder(view);
            }
        }

        return viewHolder;
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int pos = position;
        // TypeCast Object to int type
        if(stateHeader){
            if (holder instanceof FooterViewHolder) {
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            } else {
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.product_info.setText(models.get(position).goodsName);
                String mPrice = models.get(position).price;
                if(!mPrice.equals("")){
                    int idx = mPrice.indexOf(".");
                    viewHolder.price.setText(mPrice.substring(0, idx)+".");
                    viewHolder.price_decimal_places.setText(mPrice.substring(idx+1));
                }
                else{
                    viewHolder.price.setText("");
                    viewHolder.price_decimal_places.setText("");
                }

                viewHolder.text_address.setText("辽宁·沈阳");
                Picasso.get().load(models.get(position).goodsPic).into(viewHolder.images);

                if(itemLayoutMode == 1 ) {
                    viewHolder.tvStoreName.setText(models.get(position).shopName);
                    viewHolder.storeName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(context, StoreDetailsActivity.class);
                            i.putExtra("shopId", models.get(pos).shopId);
                            if(!models.get(pos).shopId.equals(""))
                                context.startActivity(i);
                        }
                    });
                }

                if(itemLayoutMode == 2 ){
                    if(!models.get(position).goodsPic.equals("")){
                        Picasso.get()
                                .load(models.get(position).goodsPic)
                                .resize(1000, 1000)
                                .centerCrop()
                                .into(viewHolder.images);
                    }

                    String p_options = "";
                    try {
                        for(int i=0; i<models.get(position).goodsParam.size(); i++)
                        {
                            Params goodParam = models.get(position).goodsParam.get(i);
                            p_options = p_options + goodParam.value + " ";
                        }
                    }catch (Exception e){}

                    viewHolder.text_product_option.setText(p_options);
                }

                if(itemLayoutMode==4){
                    Picasso.get()
                            .load(models.get(position).goodsPic)
                            .resize(100, 100)
                            .into(viewHolder.images);
                    viewHolder.price.setText("￥"+models.get(position).price);
                }
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
                String mPrice = models.get(position-1).price;
                int idx = mPrice.indexOf(".");

                viewHolder.price.setText("￥" + mPrice.substring(0, idx)+".");
                viewHolder.price_decimal_places.setText(mPrice.substring(idx+1));
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
        TextView text_product_option;
        TextView text_address;
        TextView price_decimal_places, tvStoreName;
        LinearLayout storeName;

        public ViewHolder(View itemView) {
            super(itemView);
            images = (ImageView) itemView.findViewById(R.id.image_product);
            product_info = (TextView) itemView.findViewById(R.id.text_product_info);
            price = (TextView) itemView.findViewById(R.id.text_price);
            price_decimal_places = (TextView) itemView.findViewById(R.id.price_decimal_places);
            text_address = (TextView) itemView.findViewById(R.id.text_address);

            if(itemLayoutMode == 2){
                text_product_option = (TextView) itemView.findViewById(R.id.text_product_option);
            }
            if(itemLayoutMode == 1){
                storeName = (LinearLayout) itemView.findViewById(R.id.storeName);
                tvStoreName = (TextView) itemView.findViewById(R.id.tvStoreName);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getPosition()); // call the onClick in the OnItemClickListener
        }

    }

}
