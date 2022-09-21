package com.hws.hwsappandroid.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.GoodOfShoppingCart;
import com.hws.hwsappandroid.model.MyOrderModel;
import com.hws.hwsappandroid.ui.OrderCompletedActivity;
import com.hws.hwsappandroid.ui.PendingReceiptActivity;
import com.hws.hwsappandroid.ui.PendingShipmentActivity;
import com.hws.hwsappandroid.ui.TransactionClosedActivity;
import com.hws.hwsappandroid.ui.WaitingPaymentActivity;
import com.hws.hwsappandroid.ui.me.main.MyOrderActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyOrderItemListAdapter extends RecyclerView.Adapter<MyOrderItemListAdapter.ItemViewHolder>{
    public Context context;
    public MyOrderModel myOrder;
    public ArrayList<GoodOfShoppingCart> arrayList;
    public String sectionLabel;
    public int orderStatus;

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private TextView itemLabel;
        private TextView itemDetail;
        private TextView itemPrice;
        private TextView itemAmount;


        public ItemViewHolder(View itemView) {
            super(itemView);
            itemImage = (ImageView) itemView.findViewById(R.id.image_product);
            itemLabel = (TextView) itemView.findViewById(R.id.text_product_info);
            itemDetail = (TextView) itemView.findViewById(R.id.product_option);
            itemPrice = (TextView) itemView.findViewById(R.id.text_price);
            itemAmount = (TextView) itemView.findViewById(R.id.text_amount);
        }
    }

    public MyOrderItemListAdapter(Context context, MyOrderModel arrayList, String sectionLabel){
        this.context = context;
        this.arrayList = arrayList.myGoodsList;
        this.sectionLabel = sectionLabel;
        this.myOrder = arrayList;
        this.orderStatus = arrayList.orderStatus;
    }

    @Override
    public MyOrderItemListAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_complete_item, parent, false);
        return new MyOrderItemListAdapter.ItemViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onBindViewHolder(MyOrderItemListAdapter.ItemViewHolder holder, int position) {
        GoodOfShoppingCart myOrderItem = arrayList.get(position);
        int itemIndex = position;

        String itemImage = arrayList.get(position).goodsPic;
        String itemLabel = arrayList.get(position).goodsName;
        String itemDetail = arrayList.get(position).goodsSpec;
        String itemPrice = arrayList.get(position).goodsPrice;
        int itemAmount = arrayList.get(position).goodsNum;

        try {
            Picasso.get()
                    .load(itemImage)
                    .resize(600, 600)
                    .into(holder.itemImage);
            holder.itemLabel.setText(itemLabel);
            holder.itemDetail.setText(itemDetail);
            holder.itemPrice.setText(itemPrice);
            holder.itemAmount.setText("x" + String.valueOf(itemAmount));
        }catch (Exception e){}

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent pd = new Intent(v.getContext(), ProductDetailActivity.class);
//                String pkId = arrayList.get(itemIndex).goodsId;
//                pd.putExtra("pkId", pkId);
//                v.getContext().startActivity(pd);

                if(orderStatus == 0){
                    Intent i = new Intent(v.getContext(), WaitingPaymentActivity.class);
                    i.putExtra("orderId", myOrder.pkId);
                    ((MyOrderActivity) context).startActivityForResult(i, 0);
                }
                if(orderStatus == 3){
                    Intent i = new Intent(v.getContext(), PendingShipmentActivity.class);
                    i.putExtra("orderId", myOrder.pkId);
                    ((MyOrderActivity) context).startActivityForResult(i, 0);
                }
                if(orderStatus == 4){
                    Intent i = new Intent(v.getContext(), PendingReceiptActivity.class);
                    i.putExtra("orderId", myOrder.pkId);
                    ((MyOrderActivity) context).startActivityForResult(i, 0);
                }
                if(orderStatus == 5){
                    Intent i = new Intent(v.getContext(), OrderCompletedActivity.class);
                    i.putExtra("orderId", myOrder.pkId);
                    v.getContext().startActivity(i);
                }
                if(orderStatus == 99){
                    Intent i = new Intent(v.getContext(), TransactionClosedActivity.class);
                    i.putExtra("orderId", myOrder.pkId);
                    v.getContext().startActivity(i);
                }


            }
        });

    }
}
