package com.hws.hwsappandroid.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.RecyclerViewType;
import com.hws.hwsappandroid.model.UserCartItem;
import com.hws.hwsappandroid.ui.StoreDetailsActivity;
import com.hws.hwsappandroid.ui.cart.ShoppingCartModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShoppingCartListAdapter extends RecyclerView.Adapter<ShoppingCartListAdapter.ViewHolder> {
    private Context context;
    private ShoppingCartModel mShoppingCartModel = null;
    public ArrayList<UserCartItem> models;
    SwipeController swipeController = null;
    ItemRecyclerViewSwipeAdapter adapter;

    public void setData(ArrayList<UserCartItem> list) {
        models = list;
        notifyDataSetChanged();
    }

    public void setShoppingCartModel(ShoppingCartModel shoppingCartModel) {
        mShoppingCartModel = shoppingCartModel;
    }
//    public void deleteItem(int position) {
//        setData(MyGlobals.getInstance().getMyShoppingCart());
////        models.remove(position);
////        MyGlobals.getInstance().delMyShoppingCartSection(position);
//        notifyDataSetChanged();
//    }

    public ShoppingCartListAdapter(Context context) {
        this.context = context;
        this.models = new ArrayList<>();
    }

    @Override
    public ShoppingCartListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_swipe_row_layout, parent, false);
        return new ShoppingCartListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingCartListAdapter.ViewHolder holder, int position) {
        ShoppingCartListAdapter.ViewHolder viewHolder = (ShoppingCartListAdapter.ViewHolder) holder;

        int itemIndex = position;;
        UserCartItem sectionModel ;

        sectionModel = models.get(itemIndex);

        viewHolder.sectionLabel.setText(sectionModel.shopName);
        viewHolder.selectBtn.setChecked(sectionModel.selected);

        viewHolder.section_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), StoreDetailsActivity.class);
                i.putExtra("shopId", sectionModel.shopId);
                if(!sectionModel.shopId.equals(""))
                    view.getContext().startActivity(i);
            }
        });

        //recycler view for items
        viewHolder.itemRecyclerView.setHasFixedSize(true);
        viewHolder.itemRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        viewHolder.itemRecyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ItemRecyclerViewSwipeAdapter(context, itemIndex);
        viewHolder.itemRecyclerView.setAdapter(adapter);
        adapter.setData(sectionModel.goods);
        adapter.setItemUpdatedListener(new CartItemStatusUpdatedListener() {
            @Override
            public void OnChecked(String pkId, boolean isChecked) {
                setData(models);
                ArrayList<String> pkIds = new ArrayList<>();
                pkIds.add(pkId);
                mShoppingCartModel.updateCheckStatus(pkIds, isChecked);
            }

            @Override
            public void OnNumberChanged(String pkId, int num) {
                setData(models);
                mShoppingCartModel.calcAutoValues(models);
            }
        });

        models = MyGlobals.getInstance().getMyShoppingCart();
        viewHolder.selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> pkIds = new ArrayList<>();
                if(viewHolder.selectBtn.isChecked())
                {
                    models.get(itemIndex).selected = true;
                    for(int i=0; i<models.get(itemIndex).goods.size(); i++){
                        models.get(itemIndex).goods.get(i).selected = true;
                        pkIds.add(models.get(itemIndex).goods.get(i).pkId);
                    }

                } else {
                    models.get(itemIndex).selected = false;
                    for(int i=0; i<models.get(itemIndex).goods.size(); i++){
                        models.get(itemIndex).goods.get(i).selected = false;
                        pkIds.add(models.get(itemIndex).goods.get(i).pkId);
                    }
                }
                MyGlobals.getInstance().setMyShoppingCart(models);

                if (mShoppingCartModel == null) {
                    float t_price = 0.0f;
                    int t_num = 0;
                    for(int i=0; i<models.size(); i++){
                        for(int j=0; j<models.get(i).goods.size(); j++){
                            if(models.get(i).goods.get(j).selected){
                                t_price =  t_price+ Float.parseFloat(models.get(i).goods.get(j).goodsPrice)*models.get(i).goods.get(j).goodsNum;
                                t_num = t_num + models.get(i).goods.get(j).goodsNum;
                            }
                        }
                    }
                    setData(models);
                    MyGlobals.getInstance().set_Total_price(t_price);
                    MyGlobals.getInstance().set_Total_num(t_num);
                    TextView tPrice = (TextView) v.getRootView().findViewById(R.id.totalPrice);
                    tPrice.setText(String.format("%.2f", MyGlobals.getInstance().getTotal_price()));

                    Button toSettleBtn = (Button) v.getRootView().findViewById(R.id.toSettleBtn);
                    toSettleBtn.setText(context.getResources().getString(R.string.to_settle) + "(" + MyGlobals.getInstance().getTotal_num() + ")");
                } else {
                    setData(models);
                    mShoppingCartModel.updateCheckStatus(pkIds, viewHolder.selectBtn.isChecked());
                }
            }
        });

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout section_header;
        private CheckBox selectBtn;
        private TextView sectionLabel;
        private RecyclerView itemRecyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            section_header = (LinearLayout) itemView.findViewById(R.id.section_header);
            selectBtn = (CheckBox) itemView.findViewById(R.id.radioButton);
            sectionLabel = (TextView) itemView.findViewById(R.id.section_label);
            itemRecyclerView = (RecyclerView) itemView.findViewById(R.id.item_recycler_view);
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public interface CartItemStatusUpdatedListener {
        void OnChecked(String pkId, boolean isChecked);
        void OnNumberChanged(String pkId, int num);
    }
}
