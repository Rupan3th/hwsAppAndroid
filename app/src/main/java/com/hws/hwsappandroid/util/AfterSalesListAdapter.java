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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.RefundModel;
import com.hws.hwsappandroid.model.shippingAdr;
import com.hws.hwsappandroid.ui.AfterSalesDetailActivity;
import com.hws.hwsappandroid.ui.AfterSalesListActivity;
import com.hws.hwsappandroid.ui.StoreDetailsActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class AfterSalesListAdapter extends RecyclerView.Adapter<AfterSalesListAdapter.ViewHolder> {
    private Context context;
    public ArrayList<RefundModel> models;
    private ItemClickListener mClickListener;

    private String[] refund_status = {"未通过", "处理中", "已通过" , "已通过", "已通过"};

    public AfterSalesListAdapter(Context context) {
        this.context = context;
        this.models = new ArrayList<>();
    }

    public void setData(ArrayList<RefundModel> list) {
        models = list;
        notifyDataSetChanged();
    }

    @Override
    public AfterSalesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item_mode4, parent, false);
        return new AfterSalesListAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public void onBindViewHolder(@NonNull AfterSalesListAdapter.ViewHolder holder, int position) {
        AfterSalesListAdapter.ViewHolder viewHolder = (AfterSalesListAdapter.ViewHolder) holder;
        int pos = position;

        viewHolder.shop_name.setText(models.get(position).shopName);
        viewHolder.product_status.setText(refund_status[models.get(position).refundApplyStatus]);
        Picasso.get()
                .load(models.get(position).goodsPic)
                .resize(600, 600)
                .into(viewHolder.image_product);
        viewHolder.text_product_info.setText(models.get(position).goodsName);
        viewHolder.product_option.setText(models.get(position).goodsSpec);
        viewHolder.text_price.setText(models.get(position).goodsPrice);
        viewHolder.text_amount.setText("x" + models.get(position).goodsNum);

        viewHolder.shop_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, StoreDetailsActivity.class);
                i.putExtra("shopId", models.get(pos).shopId);
                if(!models.get(pos).shopId.equals(""))
                    context.startActivity(i);
            }
        });

        viewHolder.delete_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog(3, pos);

            }
        });

        viewHolder.show_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailAfterSales = new Intent(context, AfterSalesDetailActivity.class);
                String pkId = models.get(pos).pkId;
                detailAfterSales.putExtra("pkId", pkId);
                context.startActivity(detailAfterSales);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        LinearLayout shop_area;
        TextView shop_name, product_status, text_product_info, product_option, text_price, text_amount;
        ImageButton goto_Btn;
        ImageView image_product;
        Button delete_rec, show_detail;

        public ViewHolder(View itemView) {
            super(itemView);
            shop_area = (LinearLayout) itemView.findViewById(R.id.shop_area);
            image_product = (ImageView) itemView.findViewById(R.id.image_product);
            goto_Btn = (ImageButton) itemView.findViewById(R.id.goto_Btn);
            shop_name = (TextView) itemView.findViewById(R.id.shop_name);
            product_status = (TextView) itemView.findViewById(R.id.product_status);
            text_product_info = (TextView) itemView.findViewById(R.id.text_product_info);
            product_option = (TextView) itemView.findViewById(R.id.product_option);
            text_price = (TextView) itemView.findViewById(R.id.text_price);
            text_amount = (TextView) itemView.findViewById(R.id.text_amount);
            delete_rec = (Button) itemView.findViewById(R.id.delete_rec);
            show_detail = (Button) itemView.findViewById(R.id.show_detail);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onClick(view, getPosition()); // call the onClick in the OnItemClickListener
        }

    }

    public RefundModel getItem(int position) {
        return models.get(position);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
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
                RemoveRecode(models.get(position).pkId, position);
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

    public void RemoveRecode(String pkId, int position){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();

                String url = "/bizGoodsRefund/deleteById/" + pkId;
                APIManager.post(url, params, null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
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

}
