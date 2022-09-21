package com.hws.hwsappandroid.util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.GoodOfShoppingCart;
import com.hws.hwsappandroid.model.MyOrderModel;
import com.hws.hwsappandroid.model.OrderDetailModel;
import com.hws.hwsappandroid.model.OrderGoods;
import com.hws.hwsappandroid.ui.ProductDetailActivity;
import com.hws.hwsappandroid.ui.RequestRefundActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrderGoodsListAdapter extends RecyclerView.Adapter<OrderGoodsListAdapter.ItemViewHolder>{
    public Context context;
    public ArrayList<OrderGoods> arrayList;
    public boolean refund_mode = false;
    public String orderId, phone, totalMoney;

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private TextView itemLabel;
        private TextView itemDetail;
        private TextView itemPrice;
        private TextView itemAmount;
        private Button request_refundBtn;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemImage = (ImageView) itemView.findViewById(R.id.image_product);
            itemLabel = (TextView) itemView.findViewById(R.id.text_product_info);
            itemDetail = (TextView) itemView.findViewById(R.id.product_option);
            itemPrice = (TextView) itemView.findViewById(R.id.text_price);
            itemAmount = (TextView) itemView.findViewById(R.id.text_amount);
            request_refundBtn = (Button) itemView.findViewById(R.id.request_refundBtn);
        }
    }

    public void setData(OrderDetailModel OrderDetail) {
        arrayList = OrderDetail.goodsInfoList;
        orderId = OrderDetail.pkId;
        phone = OrderDetail.phone;
        totalMoney = OrderDetail.totalMoney;
        notifyDataSetChanged();
    }


    public OrderGoodsListAdapter(Context context, boolean refund_mode){
        this.context = context;
        this.refund_mode = refund_mode;
    }

    @Override
    public OrderGoodsListAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_complete_item, parent, false);
        return new OrderGoodsListAdapter.ItemViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onBindViewHolder(OrderGoodsListAdapter.ItemViewHolder holder, int position) {
        OrderGoods myOrderItem = arrayList.get(position);
        int itemIndex = position;

        String itemImage = arrayList.get(position).goodsPic;
        String itemLabel = arrayList.get(position).goodsName;
        String itemDetail = arrayList.get(position).goodsSpec;
        String itemPrice = arrayList.get(position).goodsPrice;
        int itemAmount = arrayList.get(position).goodsNum;

        try{
            Picasso.get()
                    .load(itemImage)
                    .resize(600, 600)
                    .into(holder.itemImage);
            holder.itemLabel.setText(itemLabel);
            holder.itemDetail.setText(itemDetail);
            holder.itemPrice.setText(itemPrice);
            holder.itemAmount.setText("x" + String.valueOf(itemAmount));
        }catch (Exception e){}

        if(refund_mode) {
            holder.request_refundBtn.setVisibility(View.VISIBLE);
            if(arrayList.get(itemIndex).refundApplyStatus != 0){
                holder.request_refundBtn.setBackgroundResource(R.drawable.round_gray_disable_36);
                holder.request_refundBtn.setTextColor(context.getResources().getColor(R.color.text_hint));
            }
            else {
                holder.request_refundBtn.setBackgroundResource(R.drawable.round_gray_soft_36);
                holder.request_refundBtn.setTextColor(context.getResources().getColor(R.color.text_btn));
                holder.request_refundBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent RR = new Intent(view.getContext(), RequestRefundActivity.class);
                        String goodsId = arrayList.get(itemIndex).goodsId;
                        String goodsSpecId = arrayList.get(itemIndex).goodsSpecId;
                        String goodsPic = arrayList.get(itemIndex).goodsPic;
                        String goodsName = arrayList.get(itemIndex).goodsName;
                        String goodsSpec = arrayList.get(itemIndex).goodsSpec;
                        String goodsPrice = arrayList.get(itemIndex).goodsPrice;
                        int goodsNum = arrayList.get(itemIndex).goodsNum;
                        RR.putExtra("goodsId", goodsId);
                        RR.putExtra("goodsSpecId", goodsSpecId);
                        RR.putExtra("goodsPic", goodsPic);
                        RR.putExtra("goodsName", goodsName);
                        RR.putExtra("goodsSpec", goodsSpec);
                        RR.putExtra("goodsPrice", goodsPrice);
                        RR.putExtra("goodsNum", goodsNum);
                        RR.putExtra("orderId", orderId);
                        RR.putExtra("phone", phone);
                        RR.putExtra("totalMoney", totalMoney);
                        view.getContext().startActivity(RR);
                    }
                });
            }
        }
        else holder.request_refundBtn.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "You clicked "+itemLabel, Toast.LENGTH_SHORT).show();

                Intent pd = new Intent(v.getContext(), ProductDetailActivity.class);
                String pkId = arrayList.get(itemIndex).goodsId;
                pd.putExtra("pkId", pkId);
                v.getContext().startActivity(pd);

            }
        });

    }
}
