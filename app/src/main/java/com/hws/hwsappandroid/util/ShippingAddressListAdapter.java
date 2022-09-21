package com.hws.hwsappandroid.util;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.Params;
import com.hws.hwsappandroid.model.shippingAdr;
import com.hws.hwsappandroid.ui.ChooseCategoryActivity;
import com.hws.hwsappandroid.ui.EditShippingAddressActivity;
import com.hws.hwsappandroid.ui.ShippingAddressActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ShippingAddressListAdapter extends RecyclerView.Adapter<ShippingAddressListAdapter.ViewHolder> {
    private Context context;
    public ArrayList<shippingAdr> models;
    private ItemClickListener mClickListener;
    public boolean isDel;
    String default_Status, activityPurpose;

    public ShippingAddressListAdapter(Context context, String activityPurpose) {
        this.context = context;
        this.models = new ArrayList<>();
        this.activityPurpose = activityPurpose;
    }

    public void setData(ArrayList<shippingAdr> list) {
        models = list;
        notifyDataSetChanged();
    }

    public void setDefaultAdr(int position) {
        for(int i=0; i<models.size(); i++){
            models.get(i).addressDefault = "0";
            if(i == position) models.get(i).addressDefault = "1";
        }

        notifyDataSetChanged();
    }

    @Override
    public ShippingAddressListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shipping_address_card, parent, false);
        return new ShippingAddressListAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ShippingAddressListAdapter.ViewHolder holder, int position) {
        ShippingAddressListAdapter.ViewHolder viewHolder = (ShippingAddressListAdapter.ViewHolder) holder;
        int pos = position;
        if(models.get(position).addressDefault.equals("1")){
//            viewHolder.address_card.setBackgroundResource(R.drawable.card_view_border);
            viewHolder.confirm_mark.setVisibility(View.VISIBLE);
            //save shared data
//            SharedPreferences pref = context.getSharedPreferences("user_info",MODE_PRIVATE);
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putString("default_consignee", models.get(position).consignee);
//            editor.putString("default_phone", models.get(position).phone);
//            editor.putString("default_adr", models.get(position).province +
//                    models.get(position).city + models.get(position).district + models.get(position).address);
//            editor.commit();
        }
        else{
//            viewHolder.address_card.setBackground(null);
            viewHolder.confirm_mark.setVisibility(View.GONE);
        }
        viewHolder.client_Name.setText(models.get(position).consignee);

        String phone = models.get(position).phone;
        try{
            viewHolder.phone_Number.setText(phone.substring(0,3)+"****"+phone.substring(7));
        }catch (Exception e){}

        viewHolder.client_Address.setText(models.get(position).province
                + models.get(position).city
                + models.get(position).district
                + models.get(position).address);

        viewHolder.goto_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), EditShippingAddressActivity.class);
                i.putExtra("address", models.get(pos).address);
                i.putExtra("addressDefault", models.get(pos).addressDefault);
                i.putExtra("city", models.get(pos).city);
                i.putExtra("consignee", models.get(pos).consignee);
                i.putExtra("consigneeXb", models.get(pos).consigneeXb);
                i.putExtra("country", models.get(pos).country);
                i.putExtra("district", models.get(pos).district);
                i.putExtra("gmtCreate", models.get(pos).gmtCreate);
                i.putExtra("gmtModified", models.get(pos).gmtModified);
                i.putExtra("operatorId", models.get(pos).operatorId);
                i.putExtra("userId", models.get(pos).userId);
                i.putExtra("phone", models.get(pos).phone);
                i.putExtra("pkId", models.get(pos).pkId);
                i.putExtra("province", models.get(pos).province);
                i.putExtra("addNew", false);

//                context.startActivity(i);

                ShippingAddressActivity SA =  (ShippingAddressActivity) ShippingAddressActivity.SAActivity;
                SA.startActivityForResult(i, 0);
//                SA.finish();
            }
        });

        viewHolder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog(0, pos);
            }
        });

        if (activityPurpose != null && activityPurpose.equals("choose")) {
            viewHolder.recipient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shippingAdr addr = models.get(pos);
                    Intent i = new Intent();
                    i.putExtra("address", addr.address);
                    i.putExtra("addressDefault", addr.addressDefault);
                    i.putExtra("city", addr.city);
                    i.putExtra("consignee", addr.consignee);
                    i.putExtra("consigneeXb", addr.consigneeXb);
                    i.putExtra("country", addr.country);
                    i.putExtra("district", addr.district);
                    i.putExtra("gmtCreate", addr.gmtCreate);
                    i.putExtra("gmtModified", addr.gmtModified);
                    i.putExtra("operatorId", addr.operatorId);
                    i.putExtra("userId", addr.userId);
                    i.putExtra("phone", addr.phone);
                    i.putExtra("pkId", addr.pkId);
                    i.putExtra("province", addr.province);
                    ((Activity) context).setResult(RESULT_OK, i);
                    ((Activity) context).finish();
                }
            });
            viewHolder.client_Address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shippingAdr addr = models.get(pos);
                    Intent i = new Intent();
                    i.putExtra("address", addr.address);
                    i.putExtra("addressDefault", addr.addressDefault);
                    i.putExtra("city", addr.city);
                    i.putExtra("consignee", addr.consignee);
                    i.putExtra("consigneeXb", addr.consigneeXb);
                    i.putExtra("country", addr.country);
                    i.putExtra("district", addr.district);
                    i.putExtra("gmtCreate", addr.gmtCreate);
                    i.putExtra("gmtModified", addr.gmtModified);
                    i.putExtra("operatorId", addr.operatorId);
                    i.putExtra("userId", addr.userId);
                    i.putExtra("phone", addr.phone);
                    i.putExtra("pkId", addr.pkId);
                    i.putExtra("province", addr.province);
                    ((Activity) context).setResult(RESULT_OK, i);
                    ((Activity) context).finish();
                }
            });
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        LinearLayout address_card;
        TextView confirm_mark;
        ImageView goto_Btn;
        LinearLayout client_info_area, recipient;
        TextView client_Name;
        TextView phone_Number;
        TextView client_Address;
        private RelativeLayout delete_button;

        public ViewHolder(View itemView) {
            super(itemView);
            address_card = (LinearLayout) itemView.findViewById(R.id.address_card);
            client_info_area = (LinearLayout) itemView.findViewById(R.id.client_info_area);
            recipient = (LinearLayout) itemView.findViewById(R.id.recipient);
            confirm_mark = (TextView) itemView.findViewById(R.id.confirm_mark);
            goto_Btn = (ImageView) itemView.findViewById(R.id.goto_Btn);
            client_Name = (TextView) itemView.findViewById(R.id.client_Name);
            phone_Number = (TextView) itemView.findViewById(R.id.phone_Number);
            client_Address = (TextView) itemView.findViewById(R.id.client_Address);
            delete_button = (RelativeLayout) itemView.findViewById(R.id.delete_button);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onClick(view, getPosition()); // call the onClick in the OnItemClickListener
        }

    }

    public shippingAdr getItem(int position) {
        return models.get(position);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    private void showConfirmDialog(int confirmType, int position) {
        final Dialog confirmDialog = new Dialog(context);
        View view = new KindTipsView(context, confirmType);

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
                isDel = false;
                default_Status = models.get(position).addressDefault;
                delAddress(models.get(position).pkId, position);
                confirmDialog.dismiss();
            }
        });

        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmDialog.setContentView(view);
        confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Rect displayRectangle = new Rect();
        Window window = confirmDialog.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        confirmDialog.getWindow().setLayout(CommonUtils.getPixelValue(context, 286), ViewGroup.LayoutParams.WRAP_CONTENT);

        confirmDialog.show();
//        confirmDialog.getWindow().setGravity(Gravity.BOTTOM);
    }


    public void delAddress(String pkId, int position) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();

                String url = "/appUserAddress/deleteById/" + pkId;
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                if(!isDel) {
                                    models.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, getItemCount());

                                    if(models.size() == 0 ){
                                        LinearLayout no_result_area = ((Activity) context).findViewById(R.id.no_result_area);
                                        no_result_area.setVisibility(View.VISIBLE);
                                        SharedPreferences pref = context.getSharedPreferences("user_info",MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("default_consignee", "");
                                        editor.putString("default_phone", "");
                                        editor.putString("default_adr", "");
                                        editor.commit();
                                    }

                                    if(default_Status.equals("1")){
                                        SharedPreferences pref = context.getSharedPreferences("user_info",MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("default_consignee", "");
                                        editor.putString("default_phone", "");
                                        editor.putString("default_adr", "");
                                        editor.commit();
                                    }
                                    isDel = true;
                                }

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
}
