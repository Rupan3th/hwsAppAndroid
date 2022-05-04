package com.hws.hwsappandroid.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.SwipeSectionItemModel;

import java.util.ArrayList;

public class ItemRecyclerViewSwipeAdapter extends RecyclerView.Adapter<ItemRecyclerViewSwipeAdapter.ItemViewHolder>{
    class ItemViewHolder extends RecyclerView.ViewHolder {
        private RadioButton itemRadioBtn;
        private ImageView itemImage;
        private TextView itemLabel;
        private TextView itemDetail;
        private TextView itemPrice;
        private TextView itemAmount;
        private Button itemPlusBtn;
        private Button itemMinusBtn;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemRadioBtn = (RadioButton) itemView.findViewById(R.id.radioButton);
            itemImage = (ImageView) itemView.findViewById(R.id.imgItem);
            itemLabel = (TextView) itemView.findViewById(R.id.item_label);
            itemDetail = (TextView) itemView.findViewById(R.id.tvItemDetail);
            itemPrice = (TextView) itemView.findViewById(R.id.tvItemPrice);
            itemAmount = (TextView) itemView.findViewById(R.id.tvItemAmount);
            itemPlusBtn = (Button) itemView.findViewById(R.id.btnPlus);
            itemMinusBtn = (Button) itemView.findViewById(R.id.btnMinus);
        }
    }

    public Context context;
    public ArrayList<SwipeSectionItemModel> arrayList;
    public String sectionLabel;
    public int Amount;
    public boolean allSelect;

    public ItemRecyclerViewSwipeAdapter(Context context, ArrayList<SwipeSectionItemModel> arrayList, String sectionLabel) {
        this.context = context;
        this.arrayList = arrayList;
        this.sectionLabel = sectionLabel;
        this.allSelect = false;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_swipe_recycler_item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemRecyclerViewSwipeAdapter.ItemViewHolder holder, int position) {
        SwipeSectionItemModel swipeSectionItem = arrayList.get(position);
        int itemIndex = position;
        boolean itemSelected = arrayList.get(position).getSelectedState();
        Bitmap itemImage = arrayList.get(position).getProductImage();
        String itemLabel = arrayList.get(position).getItemLabel();
        String itemDetail = arrayList.get(position).getSpecification();
        String itemPrice = arrayList.get(position).getPrice();
        int itemAmount = arrayList.get(position).getAmount();

        if(allSelect){
            holder.itemRadioBtn.setChecked(true);
            holder.itemRadioBtn.setSelected(true);
        }else {
            holder.itemRadioBtn.setChecked(false);
            holder.itemRadioBtn.setSelected(false);
        }
        holder.itemImage.setImageBitmap(itemImage);
        holder.itemLabel.setText(itemLabel);
        holder.itemDetail.setText(itemDetail);
        holder.itemPrice.setText(itemPrice);
        holder.itemAmount.setText(String.valueOf(itemAmount));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You clicked "+itemLabel +" of " + sectionLabel, Toast.LENGTH_SHORT).show();
            }
        });

        this.Amount = itemAmount;
        holder.itemPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Amount = Amount+1;
                holder.itemAmount.setText(String.valueOf(Amount));
                arrayList.get(itemIndex).setAmount(Amount + 1);
            }
        });
        holder.itemMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Amount > 1){
                    Amount = Amount-1;
                    holder.itemAmount.setText(String.valueOf(Amount));
                    arrayList.get(itemIndex).setAmount(Amount);
                }
            }
        });

        holder.itemRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.itemRadioBtn.isSelected())
                {
                    holder.itemRadioBtn.setChecked(false);
                    holder.itemRadioBtn.setSelected(false);
                    arrayList.get(itemIndex).setSelectedState(false);
                } else {
                    holder.itemRadioBtn.setChecked(true);
                    holder.itemRadioBtn.setSelected(true);
                    arrayList.get(itemIndex).setSelectedState(true);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


}