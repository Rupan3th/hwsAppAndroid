package com.hws.hwsappandroid.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.MainActivity;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.GoodOfShoppingCart;
import com.hws.hwsappandroid.ui.BuyNowActivity;
import com.hws.hwsappandroid.ui.MyCollectionActivity;
import com.hws.hwsappandroid.ui.ProductDetailActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.walnutlabs.android.ProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ItemRecyclerViewSwipeAdapter extends RecyclerView.Adapter<ItemRecyclerViewSwipeAdapter.ItemViewHolder> implements DialogInterface.OnCancelListener{
    class ItemViewHolder extends RecyclerView.ViewHolder {
        private CheckBox itemRadioBtn;
        private ImageView itemImage;
        private TextView itemLabel;
        private TextView itemDetail;
        private TextView itemPrice;
        private TextView itemAmount, tvAmount;
        private TextView itemPlusBtn;
        private TextView itemMinusBtn;
        private ConstraintLayout swipe_item;
        private RelativeLayout delete_button;
        private RelativeLayout favorite_button;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemRadioBtn = (CheckBox) itemView.findViewById(R.id.radioButton);
            itemImage = (ImageView) itemView.findViewById(R.id.imgItem);
            itemLabel = (TextView) itemView.findViewById(R.id.item_label);
            itemDetail = (TextView) itemView.findViewById(R.id.tvItemDetail);
            itemPrice = (TextView) itemView.findViewById(R.id.tvItemPrice);
            tvAmount = (TextView) itemView.findViewById(R.id.amount);
            itemAmount = (TextView) itemView.findViewById(R.id.tvItemAmount);
            itemPlusBtn = (TextView) itemView.findViewById(R.id.btnPlus);
            itemMinusBtn = (TextView) itemView.findViewById(R.id.btnMinus);
            swipe_item = (ConstraintLayout) itemView.findViewById(R.id.swipe_item);
            delete_button = (RelativeLayout) itemView.findViewById(R.id.delete_button);
            favorite_button = (RelativeLayout) itemView.findViewById(R.id.favorite_button);
        }
    }

    public Context context;
    public ArrayList<GoodOfShoppingCart> arrayList;
    public int sectionIndex;
    public int Amount, itemAmount, product_stock;
    public boolean allSelect;
    public int total_num = 0;
    private ShoppingCartListAdapter.CartItemStatusUpdatedListener mItemUpdatedlistener = null;

    public ItemRecyclerViewSwipeAdapter(Context context,int sectionIndex) {
        this.context = context;
        this.arrayList = arrayList;
        this.sectionIndex = sectionIndex;
        this.allSelect = false;
    }

    public void setData(ArrayList<GoodOfShoppingCart> list) {
        arrayList = list;
        notifyDataSetChanged();
    }

    public void setItemUpdatedListener(ShoppingCartListAdapter.CartItemStatusUpdatedListener listener) {
        mItemUpdatedlistener = listener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_swipe_recycler_item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemRecyclerViewSwipeAdapter.ItemViewHolder holder, int position) {
        GoodOfShoppingCart good = arrayList.get(position);
        int itemIndex = position;

//        if(itemIndex% 2 == 1)
//            holder.swipe_item.setBackgroundColor(Color.parseColor("#FFFFFF"));
//        else{
//            holder.swipe_item.setBackgroundColor(Color.parseColor("#48E7E9EC"));
//        }

        String itemPkId = good.goodsId;
        String itemSelected = good.isCheck;
        String itemImage = good.goodsPic;
        String itemLabel = good.goodsName;
        String itemDetail = good.goodsSpec;
        String itemPrice = good.goodsPrice;
        itemAmount = good.goodsNum;
        product_stock = Integer.parseInt(good.stock);

        try {
            Picasso.get()
                    .load(itemImage)
                    .resize(500, 500)
                    .into(holder.itemImage);
        }catch (Exception e){}

        holder.itemLabel.setText(itemLabel);
        holder.itemDetail.setText(itemDetail);
        holder.itemPrice.setText(itemPrice);
        holder.itemAmount.setText(String.valueOf(itemAmount));
        holder.tvAmount.setText(String.valueOf(itemAmount));

        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailProduct = new Intent(view.getContext(), ProductDetailActivity.class);
                detailProduct.putExtra("pkId", itemPkId);
                view.getContext().startActivity(detailProduct);
            }
        });
        holder.itemLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailProduct = new Intent(view.getContext(), ProductDetailActivity.class);
                detailProduct.putExtra("pkId", itemPkId);
                view.getContext().startActivity(detailProduct);
            }
        });
        holder.itemDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailProduct = new Intent(view.getContext(), ProductDetailActivity.class);
                detailProduct.putExtra("pkId", itemPkId);
                view.getContext().startActivity(detailProduct);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "You clicked "+itemLabel +" of " + sectionLabel, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(v.getContext(), BuyNowActivity.class);
                i.putExtra("goodsId", good.goodsId);
                i.putExtra("goodsPic", good.goodsPic);
                i.putExtra("goodsName", good.goodsName);
                i.putExtra("amount", good.goodsNum);
                i.putExtra("price", good.goodsPrice);
                i.putExtra("goodsSpec", good.goodsSpec);
                i.putExtra("goodsSpecId", good.goodsSpecId);
                i.putExtra("pkId", good.pkId);
                i.putExtra("shopId", good.shopId);
                i.putExtra("shopName", good.shopName);
                i.putExtra("userId", good.userId);
                context.startActivity(i);
            }
        });

        holder.itemPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayList.get(itemIndex).isOnSale == 0){
                    Toast.makeText(context, context.getResources().getString(R.string.is_currently_unavailable), Toast.LENGTH_SHORT).show();
                }else {
                    Amount = arrayList.get(itemIndex).goodsNum + 1;
                    holder.itemAmount.setText(String.valueOf(Amount));
                    holder.tvAmount.setText(String.valueOf(Amount));
                    arrayList.get(itemIndex).goodsNum = Amount;
                    saveByGoodsNum(arrayList.get(itemIndex));
                    if(holder.itemRadioBtn.isChecked()) {
                        if (mItemUpdatedlistener == null) {
                            MyGlobals.getInstance().addTotal_price(Float.parseFloat(arrayList.get(itemIndex).goodsPrice));
                            TextView tPrice = (TextView) v.getRootView().findViewById(R.id.totalPrice);
                            tPrice.setText(String.format("%.2f", MyGlobals.getInstance().getTotal_price()));

                            total_num = MyGlobals.getInstance().getTotal_num() + 1;
                            MyGlobals.getInstance().set_Total_num(total_num);
                            Button toSettleBtn = (Button) v.getRootView().findViewById(R.id.toSettleBtn);
                            toSettleBtn.setText(context.getResources().getString(R.string.to_settle) + "(" + MyGlobals.getInstance().getTotal_num() + ")");
                        } else {
                            mItemUpdatedlistener.OnNumberChanged(arrayList.get(itemIndex).pkId, Amount);
                        }
                    }
                    if(Amount > 1) holder.itemMinusBtn.setTextColor(Color.parseColor("#ff222222"));
                }
            }
        });
        if(arrayList.get(itemIndex).goodsNum > 1)  holder.itemMinusBtn.setTextColor(Color.parseColor("#FF222222"));
        else   holder.itemMinusBtn.setTextColor(Color.parseColor("#ffcccccc"));

        holder.itemMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayList.get(itemIndex).isOnSale == 0){
                    Toast.makeText(context, context.getResources().getString(R.string.is_currently_unavailable), Toast.LENGTH_SHORT).show();
                }else {
                    if(arrayList.get(itemIndex).goodsNum > 1){
                        Amount = arrayList.get(itemIndex).goodsNum-1;
                        holder.itemAmount.setText(String.valueOf(Amount));
                        holder.tvAmount.setText(String.valueOf(Amount));
                        arrayList.get(itemIndex).goodsNum = Amount;
                        saveByGoodsNum(arrayList.get(itemIndex));
                        if(holder.itemRadioBtn.isChecked()){
                            if (mItemUpdatedlistener == null) {
                                MyGlobals.getInstance().minusTotal_price(Float.parseFloat(arrayList.get(itemIndex).goodsPrice));
                                TextView tPrice = (TextView) v.getRootView().findViewById(R.id.totalPrice);
                                tPrice.setText(String.format("%.2f", MyGlobals.getInstance().getTotal_price()));

                                total_num = MyGlobals.getInstance().getTotal_num() - 1;
                                MyGlobals.getInstance().set_Total_num(total_num);
                                Button toSettleBtn = (Button) v.getRootView().findViewById(R.id.toSettleBtn);
                                toSettleBtn.setText(context.getResources().getString(R.string.to_settle) + "(" + MyGlobals.getInstance().getTotal_num() + ")");
                            } else {
                                mItemUpdatedlistener.OnNumberChanged(arrayList.get(itemIndex).pkId, Amount);
                            }
                        }

                        if(Amount == 1) holder.itemMinusBtn.setTextColor(Color.parseColor("#ffcccccc"));
                    }
                }

            }
        });

        holder.itemRadioBtn.setChecked(good.selected);
        holder.itemRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tPrice = (TextView) v.getRootView().findViewById(R.id.totalPrice);
                float item_price = Float.parseFloat(arrayList.get(itemIndex).goodsPrice) * arrayList.get(itemIndex).goodsNum;
                arrayList.get(itemIndex).selected = holder.itemRadioBtn.isChecked();

                if (mItemUpdatedlistener == null) {
                    if(holder.itemRadioBtn.isChecked())
                    {
//                    holder.itemRadioBtn.setChecked(false);
//                    arrayList.get(itemIndex).isCheck = "1";
                        MyGlobals.getInstance().addTotal_price(item_price);

                        total_num = MyGlobals.getInstance().getTotal_num() + arrayList.get(itemIndex).goodsNum;
                        MyGlobals.getInstance().set_Total_num(total_num);
                        Button toSettleBtn = (Button) v.getRootView().findViewById(R.id.toSettleBtn);
                        toSettleBtn.setText(context.getResources().getString(R.string.to_settle) + "(" + MyGlobals.getInstance().getTotal_num() + ")");
                    } else {
//                    holder.itemRadioBtn.setChecked(true);
//                    arrayList.get(itemIndex).isCheck = "2";
                        MyGlobals.getInstance().minusTotal_price(item_price);

                        total_num = MyGlobals.getInstance().getTotal_num() - arrayList.get(itemIndex).goodsNum;
                        MyGlobals.getInstance().set_Total_num(total_num);
                        Button toSettleBtn = (Button) v.getRootView().findViewById(R.id.toSettleBtn);
                        toSettleBtn.setText(context.getResources().getString(R.string.to_settle) + "(" + MyGlobals.getInstance().getTotal_num() + ")");
                    }
                    tPrice.setText(String.format("%.2f", MyGlobals.getInstance().getTotal_price()));
                } else {
                    mItemUpdatedlistener.OnChecked(arrayList.get(itemIndex).pkId, holder.itemRadioBtn.isChecked());
                }

                MyGlobals.getInstance().setMySelected(arrayList, sectionIndex);
            }
        });

        holder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity MA =  (MainActivity) MainActivity.MActivity;
                int cart_contentsNum = MyGlobals.getInstance().getNotify_cart() - 1;
                if(cart_contentsNum < 0) cart_contentsNum = 0;
                MyGlobals.getInstance().setNotify_cart(cart_contentsNum);
                MA.refresh_badge();

                delFromCart(arrayList.get(itemIndex).pkId, itemIndex);
                MyGlobals.getInstance().delMyShoppingCartItem(sectionIndex, itemIndex);
//                arrayList.remove(itemIndex);
                setData(arrayList);

                RecyclerView rsc = (RecyclerView) v.getRootView().findViewById(R.id.sectioned_recycler_swipe);
                if(arrayList.size() < 1){
                    ShoppingCartListAdapter scAdapter = new ShoppingCartListAdapter(context);
                    rsc.setAdapter(scAdapter);
                    MyGlobals.getInstance().delMyShoppingCartSection(sectionIndex);
                    scAdapter.setData(MyGlobals.getInstance().getMyShoppingCart());

                    LinearLayout no_result_area = ((Activity) context).findViewById(R.id.cart_is_empty);
                    no_result_area.setVisibility(View.VISIBLE);
                    LinearLayout select_line = ((Activity) context).findViewById(R.id.select_line);
                    select_line.setVisibility(View.GONE);
                    LinearLayout bottomCtr = ((Activity) context).findViewById(R.id.bottomCtr);
                    bottomCtr.setVisibility(View.GONE);
                }

            }
        });

        if(arrayList.get(itemIndex).canFavorite.equals("yes")){
            holder.favorite_button.setBackgroundColor(context.getResources().getColor(R.color.goto_favorite_btn));
            holder.favorite_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setFavorite(itemPkId, holder);
                }
            });
        }else holder.favorite_button.setBackgroundColor(context.getResources().getColor(R.color.text_hint));


        holder.itemAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditAmountDialog(itemIndex, holder);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void delFromCart(String pkId, int pos) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();

                final ProgressHUD progressDialog = ProgressHUD.show(context, "", true, false, ItemRecyclerViewSwipeAdapter.this);
                String url = "/appUserCart/deleteById/" + pkId;
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
//                                arrayList.remove(pos);
//                                notifyItemRemoved(pos);
//                                notifyItemRangeChanged(pos, getItemCount());
                                progressDialog.dismiss();
                            } else {
                                Log.d("Home request", response.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("Home request", ""+statusCode);
                        progressDialog.dismiss();//
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("Home request", responseString);
                        progressDialog.dismiss();//
                    }
                });
            }
        });
    }


    public void saveByGoodsNum(GoodOfShoppingCart good){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("pkId", good.pkId);
                    jsonParams.put("goodsId", good.goodsId);
                    jsonParams.put("goodsNum", good.goodsNum);
                    jsonParams.put("goodsSpecId", good.goodsSpecId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final ProgressHUD progressDialog = ProgressHUD.show(context, "", true, false, ItemRecyclerViewSwipeAdapter.this);
                String url = "/appUserCart/saveBySpec";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                progressDialog.dismiss();
                            } else {
                                Log.d("Home request", response.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("Home request", ""+statusCode);
                        progressDialog.dismiss();//
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("Home request", responseString);
                        progressDialog.dismiss();//
                    }
                });
            }
        });
    }

    public void setFavorite(String pkId, ItemRecyclerViewSwipeAdapter.ItemViewHolder holder){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("goodsId", pkId);       //  "goodsId": "商品id",

                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/appUserFavorite/save" ;
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
//                                String r_pkId = response.optString("data", "");
                                holder.favorite_button.setBackgroundColor(context.getResources().getColor(R.color.text_hint));
                                Intent i = new Intent(context, MyCollectionActivity.class);
                                context.startActivity(i);
                                Toast.makeText(context, context.getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();

                            } else {
//                                Log.d("Home request", response.toString());
//                                holder.favorite_button.setBackgroundColor(context.getResources().getColor(R.color.text_hint));
                                Toast.makeText(context, context.getResources().getString(R.string.already_added), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("Home request", ""+statusCode);
//                        progressDialog.dismiss();
//                        Toast.makeText(mContext, res.getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("Home request", responseString);
//                        progressDialog.dismiss();
//                        Toast.makeText(mContext, res.getString(R.string.error_db), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }


    public void showEditAmountDialog(int pos, ItemRecyclerViewSwipeAdapter.ItemViewHolder holder){
        total_num = MyGlobals.getInstance().getTotal_num();
        int initItemNum = itemAmount;

        final Dialog SheetEditDialog = new Dialog(context);
        SheetEditDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        SheetEditDialog.setContentView(R.layout.sheet_edit_amount);
        SheetEditDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageButton plus_Btn = SheetEditDialog.findViewById(R.id.btnPlus);
        ImageButton minus_Btn = SheetEditDialog.findViewById(R.id.btnMinus);

        if(itemAmount > 1){
            minus_Btn.setEnabled(true);
            minus_Btn.setImageResource(R.drawable.btn_minus);
        }else {
            minus_Btn.setEnabled(false);
            minus_Btn.setImageResource(R.drawable.btn_minus_disable);
        }

        if(itemAmount >= product_stock){
            plus_Btn.setEnabled(false);
            plus_Btn.setImageResource(R.drawable.btn_plus_disable);
        }

        EditText edit_amount = SheetEditDialog.findViewById(R.id.edit_mount);
        edit_amount.setText(String.valueOf(itemAmount));

        edit_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    itemAmount = Integer.parseInt(edit_amount.getText().toString());
                }catch (Exception e){}

                if(itemAmount > 1){
                    minus_Btn.setEnabled(true);
                    minus_Btn.setImageResource(R.drawable.btn_minus);
                }else {
                    minus_Btn.setEnabled(false);
                    minus_Btn.setImageResource(R.drawable.btn_minus_disable);
                }

                if(itemAmount < product_stock){
                    plus_Btn.setEnabled(true);
                    plus_Btn.setImageResource(R.drawable.btn_plus);
                }else {
                    plus_Btn.setEnabled(false);
                    plus_Btn.setImageResource(R.drawable.btn_plus_disable);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        TextView explain = SheetEditDialog.findViewById(R.id.explain);
        explain.setText(context.getResources().getString(R.string.notify_amount) + product_stock);

        plus_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemAmount = itemAmount+1;
                arrayList.get(pos).goodsNum = itemAmount;
                edit_amount.setText(String.valueOf(itemAmount));
            }
        });

        minus_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemAmount = itemAmount - 1;
                arrayList.get(pos).goodsNum = itemAmount;
                edit_amount.setText(String.valueOf(itemAmount));
            }
        });

        Button cancel = SheetEditDialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SheetEditDialog.dismiss();
            }
        });

        Button confirm = SheetEditDialog.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Amount = Integer.parseInt(edit_amount.getText().toString());
                    if(arrayList.get(pos).isOnSale == 0){
                        Toast.makeText(context, context.getResources().getString(R.string.is_currently_unavailable), Toast.LENGTH_SHORT).show();
                        SheetEditDialog.dismiss();
                    }else {
                        if(Amount > 0 && Amount <= product_stock){
                            arrayList.get(pos).goodsNum = Amount;
                            saveByGoodsNum(arrayList.get(pos));
                            holder.itemAmount.setText(edit_amount.getText().toString());

                            if (Amount > 1) {
                                holder.itemMinusBtn.setEnabled(true);
                                holder.itemMinusBtn.setTextColor(Color.parseColor("#FF222222"));
                            } else {
                                holder.itemMinusBtn.setEnabled(false);
                                holder.itemMinusBtn.setTextColor(Color.parseColor("#ffcccccc"));
                            }

                            if (Amount < product_stock) {
                                holder.itemPlusBtn.setEnabled(true);
                                holder.itemPlusBtn.setTextColor(Color.parseColor("#FF222222"));
                            } else {
                                holder.itemPlusBtn.setEnabled(false);
                                holder.itemPlusBtn.setTextColor(Color.parseColor("#ffcccccc"));
                            }

                            if(holder.itemRadioBtn.isChecked()) {
                                if (mItemUpdatedlistener == null) {
                                    MyGlobals.getInstance().addTotal_price(Float.parseFloat(arrayList.get(pos).goodsPrice));
                                    TextView tPrice = (TextView) view.getRootView().findViewById(R.id.totalPrice);
                                    tPrice.setText(String.format("%.2f", MyGlobals.getInstance().getTotal_price()));

                                    total_num = total_num + Amount - initItemNum;
                                    MyGlobals.getInstance().set_Total_num(total_num);
                                    Button toSettleBtn = (Button) view.getRootView().findViewById(R.id.toSettleBtn);
                                    toSettleBtn.setText(context.getResources().getString(R.string.to_settle) + "(" + MyGlobals.getInstance().getTotal_num() + ")");
                                } else {
                                    mItemUpdatedlistener.OnNumberChanged(arrayList.get(pos).pkId, Amount);
                                }
                            }

                            SheetEditDialog.dismiss();
                        }
                    }

                }catch (Exception e){}
            }
        });

        Rect displayRectangle = new Rect();
        Window window = SheetEditDialog.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        SheetEditDialog.getWindow().setLayout(CommonUtils.getPixelValue(context, 316), ViewGroup.LayoutParams.WRAP_CONTENT);

        SheetEditDialog.show();
    }
}