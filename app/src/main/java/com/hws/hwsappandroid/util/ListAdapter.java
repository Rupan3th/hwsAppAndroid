package com.hws.hwsappandroid.util;

import static com.hws.hwsappandroid.ui.CartSettlementActivity.CSActivity;

import android.app.Activity;
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
import com.hws.hwsappandroid.model.CourseModel;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.GoodOfShoppingCart;
import com.hws.hwsappandroid.model.UserCartItem;
import com.hws.hwsappandroid.model.shippingAdr;
import com.hws.hwsappandroid.ui.CartSettlementActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<shippingAdr> models;
    public ArrayList<GoodOfShoppingCart> models_settle;

    Context context;
    boolean stateHeader;
    int itemLayoutMode, amount;
    private ItemClickListener clickListener;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;
    private final int TYPE_FOOTER = 2;

    // Constructor for initialization
    public ListAdapter(Context context, boolean stateHeader, int itemLayoutMode) {
        this.context = context;
        this.models = new ArrayList<>();
        this.models_settle = new ArrayList<>();
        this.stateHeader = stateHeader;
        this.itemLayoutMode = itemLayoutMode;
    }

    public void setData(ArrayList<shippingAdr> list) {
        models = list;
        notifyDataSetChanged();
    }

    public void setDataSettle(ArrayList<GoodOfShoppingCart> list) {
        models_settle = list;
        notifyDataSetChanged();
    }

    public shippingAdr getShippingAdr(int position){
        return models.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if(stateHeader){
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
            if(itemLayoutMode == 3){
                return models_settle.size() ;
            }
            else
                return models.size() ;
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shipping_address_card, parent, false);
            if(itemLayoutMode == 1)
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.logistics_info_card, parent, false);
            else if (itemLayoutMode == 2)
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.logistics_progress_card, parent, false);
            else if (itemLayoutMode == 3)
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settlement_list_item, parent, false);

            viewHolder = new ListAdapter.ViewHolder(view);
        }
        else{
            if (viewType == TYPE_HEADER) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
                final ViewGroup.LayoutParams lp = view.getLayoutParams();
                StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
                sglp.setFullSpan(true);
                viewHolder = new ListAdapter.HeaderViewHolder(view);
            } else if (viewType == TYPE_FOOTER) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer, parent, false);
                final ViewGroup.LayoutParams lp = view.getLayoutParams();
                StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
                sglp.setFullSpan(true);
                viewHolder = new ListAdapter.FooterViewHolder(view);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shipping_address_card, parent, false);
                if(itemLayoutMode == 1)
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.logistics_info_card, parent, false);
                else if (itemLayoutMode == 2)
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.logistics_progress_card, parent, false);
                else if (itemLayoutMode == 3)
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settlement_list_item, parent, false);

                viewHolder = new ListAdapter.ViewHolder(view);
            }
        }

        return viewHolder;
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // TypeCast Object to int type
        int pos = position;
        if(stateHeader){
            ListAdapter.ViewHolder viewHolder = (ListAdapter.ViewHolder) holder;
            try{
                Picasso.get().load(models_settle.get(position).goodsPic).resize(500,500).into(viewHolder.image_product);
            }catch (Exception e){}
            viewHolder.shop_name.setText(models_settle.get(position).shopName);
            viewHolder.text_product_info.setText(models_settle.get(position).goodsName);
            viewHolder.product_option.setText(models_settle.get(position).goodsSpec);
            viewHolder.text_price.setText(models_settle.get(position).goodsPrice);
            viewHolder.text_amount.setText(""+ models_settle.get(position).goodsNum);

            float item_price = Float.parseFloat(models_settle.get(position).goodsPrice) * models_settle.get(position).goodsNum;
            viewHolder.sum_price.setText(String.format("%.2f", item_price));

            if(models_settle.get(position).goodsNum > 1)  viewHolder.minus_btn.setTextColor(context.getResources().getColor(R.color.text_main));
            else viewHolder.minus_btn.setTextColor(context.getResources().getColor(R.color.text_soft));

            viewHolder.plus_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    amount = models_settle.get(pos).goodsNum;
                    amount = amount+1;
                    viewHolder.text_amount.setText("" + amount);
                    if(amount > 1) viewHolder.minus_btn.setTextColor(context.getResources().getColor(R.color.text_main));
//                setSumPrice();
                    models_settle.get(pos).goodsNum = amount;
                    ((CartSettlementActivity) CSActivity).models_settle.get(pos).goodsNum = amount;
                    ((CartSettlementActivity) CSActivity).submitOrder();
                }
            });

            viewHolder.minus_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    amount = models_settle.get(pos).goodsNum;
                    if(amount > 1){
                        amount = amount - 1;
                        viewHolder.text_amount.setText("" + amount);
                        if(amount == 1) viewHolder.minus_btn.setTextColor(context.getResources().getColor(R.color.text_soft));
//                    setSumPrice();
                        models_settle.get(pos).goodsNum = amount;
                        ((CartSettlementActivity) CSActivity).models_settle.get(pos).goodsNum = amount;
                        ((CartSettlementActivity) CSActivity).submitOrder();
                    }
                }
            });
        }
        else{
            if (holder instanceof ListAdapter.HeaderViewHolder) {
                ListAdapter.HeaderViewHolder headerViewHolder = (ListAdapter.HeaderViewHolder) holder;
            } else if (holder instanceof ListAdapter.FooterViewHolder) {
                ListAdapter.FooterViewHolder footerViewHolder = (ListAdapter.FooterViewHolder) holder;
            } else {
                ListAdapter.ViewHolder viewHolder = (ListAdapter.ViewHolder) holder;
//                Picasso.get().load(models.get(position-1).goodsPic).into(viewHolder.images);
//                viewHolder.product_info.setText(models.get(position-1).goodsName);
//                viewHolder.price.setText("￥"+models.get(position-1).price);
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

        TextView shop_name,text_product_info,product_option,text_price,text_amount,sum_price;
        ImageView image_product;
        TextView minus_btn, plus_btn;

        public ViewHolder(View itemView) {
            super(itemView);
//            images = (ImageView) itemView.findViewById(R.id.image_product);
//            product_info = (TextView) itemView.findViewById(R.id.text_product_info);
//            price = (TextView) itemView.findViewById(R.id.text_price);
            itemView.setOnClickListener(this);

            if(itemLayoutMode == 3){
                shop_name = (TextView) itemView.findViewById(R.id.shop_name);
                text_product_info = (TextView) itemView.findViewById(R.id.text_product_info);
                product_option = (TextView) itemView.findViewById(R.id.product_option);
                text_price = (TextView) itemView.findViewById(R.id.text_price);
                text_amount = (TextView) itemView.findViewById(R.id.text_amount);
                sum_price = (TextView) itemView.findViewById(R.id.sum_price);
                image_product = (ImageView) itemView.findViewById(R.id.image_product);
                minus_btn = (TextView) itemView.findViewById(R.id.minus_btn);
                plus_btn = (TextView) itemView.findViewById(R.id.plus_btn);
            }
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getPosition()); // call the onClick in the OnItemClickListener
        }

    }
}
