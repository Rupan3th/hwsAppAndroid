package com.hws.hwsappandroid.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.GoodOfShoppingCart;
import com.hws.hwsappandroid.model.MyOrderModel;
import com.hws.hwsappandroid.ui.LogisticsInformationActivity;
import com.hws.hwsappandroid.ui.MerchantCashierActivity;
import com.hws.hwsappandroid.ui.MultiEmotionActivity;
import com.hws.hwsappandroid.ui.OrderCompletedActivity;
import com.hws.hwsappandroid.ui.RequestRefundActivity;
import com.hws.hwsappandroid.ui.StoreDetailsActivity;
import com.hws.hwsappandroid.ui.WaitingPaymentActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MyOrderSectionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<MyOrderModel> models;
    private Context context;
    MyOrderItemListAdapter adapter;
    public String bizClientId, shopLogoPic, shopName="", operatorId;
    public int mIndex;

    class SectionViewHolder extends RecyclerView.ViewHolder {
        private ImageView store_image;
        private LinearLayout shop_line;
        private TextView store_name, order_status, tv_realPayment, order_cancel, lbl_realPayment;
        private RecyclerView itemRecyclerView;
        private Button button_1, button_2, button_3;

        public SectionViewHolder(View itemView) {
            super(itemView);
            shop_line = (LinearLayout) itemView.findViewById(R.id.shop_line);
            store_image = (ImageView) itemView.findViewById(R.id.store_image);
            store_name = (TextView) itemView.findViewById(R.id.storeName);
            order_status = (TextView) itemView.findViewById(R.id.order_status);
            lbl_realPayment = (TextView) itemView.findViewById(R.id.lbl_realPay);
            tv_realPayment = (TextView) itemView.findViewById(R.id.tv_realPayment);
            itemRecyclerView = (RecyclerView) itemView.findViewById(R.id.item_recyclerView);
            order_cancel = (TextView) itemView.findViewById(R.id.order_cancel);
            button_1 = (Button) itemView.findViewById(R.id.button_1);
            button_2 = (Button) itemView.findViewById(R.id.button_2);
            button_3 = (Button) itemView.findViewById(R.id.button_3);
        }
    }

    public void setData(ArrayList<MyOrderModel> list) {
        models = list;
        notifyDataSetChanged();
    }

    public MyOrderSectionListAdapter(Context context, int mIndex) {
        this.mIndex = mIndex;
        this.context = context;
        this.models = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder holder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_order_per_store, parent, false);
        holder = new MyOrderSectionListAdapter.SectionViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int pos = position;
        MyOrderModel myOrderModel = models.get(position);
        MyOrderSectionListAdapter.SectionViewHolder sectionViewHolder = (MyOrderSectionListAdapter.SectionViewHolder) holder;
        sectionViewHolder.store_name.setText(myOrderModel.shopName);
        sectionViewHolder.tv_realPayment.setText(myOrderModel.totalMoney);

        sectionViewHolder.order_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(view.getContext(), WaitingPaymentActivity.class);
//                i.putExtra("orderId", myOrderModel.pkId);
//                view.getContext().startActivity(i);
                showConfirmDialog(0, pos);
            }
        });

        if(myOrderModel.orderStatus == 0)
        {
            sectionViewHolder.order_status.setText(R.string.be_paid);
            sectionViewHolder.order_status.setTextColor(Color.parseColor("#FFF53F3F"));
//            sectionViewHolder.order_cancel.setVisibility(View.VISIBLE);
            sectionViewHolder.button_3.setVisibility(View.GONE);
            sectionViewHolder.button_2.setText(R.string.cancel_order);
            sectionViewHolder.button_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), MerchantCashierActivity.class);
//                    Intent i = new Intent(view.getContext(), WaitingPaymentActivity.class);
                    i.putExtra("orderId", myOrderModel.pkId);
                    i.putExtra("orderCode", myOrderModel.orderCode);
                    i.putExtra("totalPrice", myOrderModel.totalMoney);
                    view.getContext().startActivity(i);
                }
            });
            sectionViewHolder.button_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showConfirmDialog(0, pos);
                }
            });
            sectionViewHolder.button_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showConfirmDialog(4, pos);
                }
            });
        }
        if(myOrderModel.orderStatus == 3)
        {
            sectionViewHolder.order_status.setText(R.string.be_shipped);
            sectionViewHolder.order_status.setTextColor(Color.parseColor("#FFF53F3F"));
            sectionViewHolder.button_3.setVisibility(View.GONE);
            sectionViewHolder.button_1.setText(R.string.contact_sellor);
            sectionViewHolder.button_2.setText(R.string.cancel_order);
            sectionViewHolder.button_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContactWithShop(myOrderModel.shopId);
                }
            });
            sectionViewHolder.button_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showConfirmDialog(0, pos);
                }
            });
        }
        if(myOrderModel.orderStatus == 4)
        {
            sectionViewHolder.order_status.setText(R.string.pending_delivery);
            sectionViewHolder.order_status.setTextColor(Color.parseColor("#FFF53F3F"));
            sectionViewHolder.button_3.setVisibility(View.GONE);
            sectionViewHolder.button_2.setText(R.string.view_logistics);
            sectionViewHolder.button_1.setText(R.string.contact_sellor);
            sectionViewHolder.button_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent LI = new Intent(view.getContext(), LogisticsInformationActivity.class);
                    LI.putExtra("orderId", myOrderModel.pkId);
                    view.getContext().startActivity(LI);

                }
            });
            sectionViewHolder.button_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContactWithShop(myOrderModel.shopId);
                }
            });
        }
        if(myOrderModel.orderStatus == 5)   {
            sectionViewHolder.order_status.setText(R.string.be_complete);
            sectionViewHolder.order_status.setTextColor(Color.parseColor("#FFF53F3F"));
            sectionViewHolder.button_3.setVisibility(View.GONE);
            sectionViewHolder.button_2.setText(R.string.request_refund);
            sectionViewHolder.button_1.setText(R.string.contact_sellor);
            sectionViewHolder.button_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContactWithShop(myOrderModel.shopId);
                }
            });

            if(myOrderModel.myGoodsList.size()>1){
                sectionViewHolder.button_2.setVisibility(View.GONE);
            }else{
                if(myOrderModel.myGoodsList.get(0).goodsStatus != 0){
                    sectionViewHolder.button_2.setBackgroundResource(R.drawable.round_gray_disable_36);
                    sectionViewHolder.button_2.setTextColor(context.getResources().getColor(R.color.text_hint));
                }else {
                    sectionViewHolder.button_2.setBackgroundResource(R.drawable.round_gray_soft_36);
                    sectionViewHolder.button_2.setTextColor(context.getResources().getColor(R.color.text_btn));
                    sectionViewHolder.button_2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent I = new Intent(view.getContext(), RequestRefundActivity.class);
                            I.putExtra("goodsId", myOrderModel.myGoodsList.get(0).goodsId);
                            I.putExtra("goodsSpecId", myOrderModel.myGoodsList.get(0).goodsSpecId);
                            I.putExtra("goodsPic", myOrderModel.myGoodsList.get(0).goodsPic);
                            I.putExtra("goodsName", myOrderModel.myGoodsList.get(0).goodsName);
                            I.putExtra("goodsSpec", myOrderModel.myGoodsList.get(0).goodsSpec);
                            I.putExtra("goodsPrice", myOrderModel.myGoodsList.get(0).goodsPrice);
                            I.putExtra("goodsNum", myOrderModel.myGoodsList.get(0).goodsNum);
                            I.putExtra("orderId", myOrderModel.pkId);
                            I.putExtra("totalMoney", myOrderModel.totalMoney);
                            view.getContext().startActivity(I);
                        }
                    });
                }
            }

        }
        if(myOrderModel.orderStatus == 99)  {
            sectionViewHolder.order_status.setText(R.string.be_refund);
            sectionViewHolder.order_status.setTextColor(Color.parseColor("#FF999999"));
            sectionViewHolder.button_3.setVisibility(View.GONE);
            sectionViewHolder.button_2.setVisibility(View.GONE);
            sectionViewHolder.button_1.setText(R.string.delete_order);
            sectionViewHolder.button_1.setTextColor(Color.parseColor("#FF555555"));
            sectionViewHolder.button_1.setBackgroundResource(R.drawable.round_gray_soft_36);
            sectionViewHolder.button_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showConfirmDialog(4, pos);
                }
            });

        }
//        if (myOrderModel.totalMoney.length() == 0) {
//            sectionViewHolder.lbl_realPayment.setVisibility(View.INVISIBLE);
//            sectionViewHolder.tv_realPayment.setVisibility(View.INVISIBLE);
//        }

        sectionViewHolder.shop_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), StoreDetailsActivity.class);
                i.putExtra("shopId", myOrderModel.shopId);
                if(!myOrderModel.shopId.equals(""))
                    view.getContext().startActivity(i);
            }
        });

        //recycler view for items
        sectionViewHolder.itemRecyclerView.setHasFixedSize(true);
        sectionViewHolder.itemRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        sectionViewHolder.itemRecyclerView.setLayoutManager(linearLayoutManager);

        adapter = new MyOrderItemListAdapter(context, myOrderModel, myOrderModel.shopName);
        sectionViewHolder.itemRecyclerView.setAdapter(adapter);

//        sectionViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(v.getContext(), WaitingPaymentActivity.class);
//                i.putExtra("orderId", myOrderModel.pkId);
//                v.getContext().startActivity(i);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return models.size() ;
    }

    private void showConfirmDialog(int confirmType, int position) {
        final Dialog confirmDialog = new Dialog(context);
        View view = new ConfirmDialogView(context, confirmType);

        Button cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog.dismiss();
            }
        });

        Button confirm = view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(confirmType == 0){
                    CancelOrder(models.get(position).pkId, position);
                }
                else if(confirmType == 4){
                    DeleteOrderById(models.get(position).pkId, position);
                }
                confirmDialog.dismiss();
            }
        });

        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmDialog.setContentView(view);
        confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Rect displayRectangle = new Rect();
        Window window = confirmDialog.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        confirmDialog.getWindow().setLayout(CommonUtils.getPixelValue(context, 316), ViewGroup.LayoutParams.WRAP_CONTENT);

        confirmDialog.show();
//        confirmDialog.getWindow().setGravity(Gravity.BOTTOM);
    }


    public void ContactWithShop(String shopId){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();
                params.put("shopId", shopId);
                String url = "/bizShop/queryShopInfo";
                APIManager.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray list = obj.getJSONArray("list");
                                obj = list.getJSONObject(0);
                                JSONObject shopJson = obj.getJSONObject("shop");

                                bizClientId = shopJson.optString("bizClientId", "");
                                shopLogoPic = shopJson.optString("shopLogoPic", "");
                                shopName = shopJson.optString("shopName", "");
                                operatorId = shopJson.optString("operatorId", "");

                                Intent i = new Intent(context, MultiEmotionActivity.class);
                                i.putExtra("shopId", shopId);
                                i.putExtra("shopName", shopName);
                                i.putExtra("shopLogoPic", shopLogoPic);
                                i.putExtra("bizClientId", bizClientId);
                                i.putExtra("operatorId", operatorId);
                                context.startActivity(i);

                            } else {
                                Log.d("Home request", response.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                        progressDialog.dismiss();
                        Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        progressDialog.dismiss();
                        Toast.makeText(context, context.getResources().getString(R.string.error_db), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void CancelOrder(String orderId, int position){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("orderId", orderId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/bizOrder/cancelOrder" ;
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
//                                loadData();
                                models.remove(position);
                                setData(models);

                            } else {
                                Log.d("Home request", response.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("Home request", "" + statusCode);
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

    public void loadData() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("pageNum", 1);
                    jsonParams.put("pageSize", "20");
                    if(mIndex == 1) {
                        jsonParams.put("status", "-1");
                    }else if(mIndex == 2){
                        jsonParams.put("status", "0");
                    }else {
                        jsonParams.put("status", mIndex);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String url = "/bizOrder/queryMyOrder";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray list = obj.getJSONArray("list");
                                ArrayList<MyOrderModel> myOrders = new ArrayList<>();
                                for (int i=0; i<list.length(); i++) {
                                    obj = list.getJSONObject(i);
                                    MyOrderModel myOrder = new MyOrderModel();
                                    myOrder.bizClientId = obj.optString("bizClientId", "");
                                    myOrder.bizUserId = obj.optString("bizClientId", "");
                                    myOrder.isChargeback = obj.optInt("isChargeback", 0);
                                    myOrder.orderCode = obj.optString("orderCode", "");
                                    myOrder.orderStatus = obj.optInt("orderStatus", 0);
                                    myOrder.orderTime = obj.optString("orderTime", "");
                                    myOrder.pkId = obj.optString("pkId", "");
                                    myOrder.shopId = obj.optString("shopId", "");
                                    myOrder.shopName = obj.optString("shopName", "");
                                    myOrder.shippingFee = obj.optString("shippingFee", "");
                                    myOrder.totalMoney = obj.optString("totalMoney", "");

                                    JSONArray myGoodsListJson = obj.getJSONArray("myGoodsList");
                                    ArrayList<GoodOfShoppingCart> GoodsList = new ArrayList<>();
                                    for (int j=0; j<myGoodsListJson.length(); j++) {
                                        JSONObject myGoodsJson = myGoodsListJson.getJSONObject(j);
                                        GoodOfShoppingCart myGoods = new GoodOfShoppingCart();
                                        myGoods.goodsId = myGoodsJson.optString("goodsId", "");
                                        myGoods.goodsName = myGoodsJson.optString("goodsName", "");
                                        myGoods.goodsNum = myGoodsJson.optInt("goodsNum", 0);
                                        myGoods.goodsPic = myGoodsJson.optString("goodsPic", "");
                                        myGoods.goodsPrice = myGoodsJson.optString("goodsPrice", "");
                                        myGoods.goodsSpec = myGoodsJson.optString("goodsSpec", "");
                                        myGoods.goodsSpecId = myGoodsJson.optString("goodsSpecId", "");
                                        myGoods.goodsStatus = myGoodsJson.optInt("goodsStatus", 0);

                                        GoodsList.add(myGoods);
                                    }
                                    myOrder.myGoodsList = GoodsList;
                                    myOrders.add(myOrder);
                                }
                                setData(myOrders);

                            } else {
                                Log.d("Home request", response.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                        progressDialog.dismiss();
                        Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        progressDialog.dismiss();
                        Toast.makeText(context, context.getResources().getString(R.string.error_db), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void DeleteOrderById(String pkId, int position){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();
                params.put("pkId", pkId);

                String url = "/bizOrder/deleteById/" ;
                APIManager.post(url, params, null, new JsonHttpResponseHandler()  {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                models.remove(position);
                                setData(models);

                            } else {
                                Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                        progressDialog.dismiss();
                        Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        progressDialog.dismiss();
                        Toast.makeText(context, context.getResources().getString(R.string.error_db), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
