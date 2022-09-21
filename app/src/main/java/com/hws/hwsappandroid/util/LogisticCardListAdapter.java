package com.hws.hwsappandroid.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.DeliveryResultListModel;
import com.hws.hwsappandroid.model.LogisticsStateList;
import com.hws.hwsappandroid.ui.LogisticsInformationActivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LogisticCardListAdapter extends RecyclerView.Adapter<LogisticCardListAdapter.ViewHolder> {
    private Context context;
    public ArrayList<DeliveryResultListModel> models;
    public logisticListAdapter logisticsProgressAdapter;
    boolean open_state;

    public LogisticCardListAdapter(Context context) {
        this.context = context;
        this.models = new ArrayList<>();
    }

    public void setData(ArrayList<DeliveryResultListModel> list) {
        models = list;
        notifyDataSetChanged();
    }

    @Override
    public LogisticCardListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_status_card, parent, false);
        return new LogisticCardListAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public void onBindViewHolder(@NonNull LogisticCardListAdapter.ViewHolder holder, int position) {
        LogisticCardListAdapter.ViewHolder viewHolder = (LogisticCardListAdapter.ViewHolder) holder;
        int pos = position;
        open_state = false;

        if(models.size()>1){
            holder.logistics_info.setVisibility(View.GONE);
            holder.status_progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!open_state){
                        holder.logistics_info.setVisibility(View.VISIBLE);
                        open_state = true;
                    }else {
                        holder.logistics_info.setVisibility(View.GONE);
                        open_state = false;
                    }

                }
            });

        }else holder.logistics_info.setVisibility(View.VISIBLE);
        int i = 0;
        try{
            i = Integer.parseInt(models.get(pos).deliverystatus);
            if(i==1){
                holder.state_one.setTextColor(context.getResources().getColor(R.color.purple_500));
                holder.state_one.setBackgroundResource(R.drawable.logistic_status_removebg);
                holder.stateImg_one.setImageResource(R.drawable.logistic_st1);
            }else if(i==2){
                holder.state_two.setTextColor(context.getResources().getColor(R.color.purple_500));
                holder.state_two.setBackgroundResource(R.drawable.logistic_status_removebg);
                holder.stateImg_two.setImageResource(R.drawable.logistic_st2);
            }else if(i==3){
                holder.state_three.setTextColor(context.getResources().getColor(R.color.purple_500));
                holder.state_three.setBackgroundResource(R.drawable.logistic_status_removebg);
                holder.stateImg_three.setImageResource(R.drawable.logistic_st3);
            }else if(i==4){
                holder.state_four.setTextColor(context.getResources().getColor(R.color.purple_500));
                holder.state_four.setBackgroundResource(R.drawable.logistic_status_removebg);
                holder.stateImg_four.setImageResource(R.drawable.logistic_st4);
            }else holder.status_progress.setVisibility(View.GONE);

            Picasso.get()
                    .load(models.get(pos).logo)
                    .resize(500, 500)
                    .into(holder.expressLogo);

            holder.expressName.setText(models.get(pos).expName);
            String express_Code = models.get(pos).number;
            holder.expressCode.setText(express_Code);
        }catch (Exception e){}
        if(i==0) holder.status_progress.setVisibility(View.GONE);

        holder.copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClipboard(context, holder.expressCode.getText().toString());
                Toast.makeText(context, "copied "+ holder.expressCode.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        logisticsProgressAdapter = new logisticListAdapter(context);
        holder.logistic_progress.setLayoutManager(new LinearLayoutManager(context));
        holder.logistic_progress.setAdapter(logisticsProgressAdapter);
        logisticsProgressAdapter.setData(models.get(pos).list);
        if(models.get(pos).list.size() == 0) {
            holder.logistics_info.setVisibility(View.GONE);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView status_progress, logistics_info;
        LinearLayout single_logistics, sn_panel;
        ImageView expressLogo,stateImg_one, stateImg_two, stateImg_three, stateImg_four;
        TextView expressName, expressCode, state_one, state_two, state_three, state_four ;
        Button copyBtn;
        RecyclerView logistic_progress;

        public ViewHolder(View itemView) {
            super(itemView);
            status_progress = (CardView) itemView.findViewById(R.id.status_progress);
            logistics_info = (CardView) itemView.findViewById(R.id.logistics_info);
            single_logistics = (LinearLayout) itemView.findViewById(R.id.single_logistics);
            sn_panel = (LinearLayout) itemView.findViewById(R.id.sn_panel);
            expressLogo = (ImageView) itemView.findViewById(R.id.expressLogo);
            stateImg_one = (ImageView) itemView.findViewById(R.id.stateImg_one);
            stateImg_two = (ImageView) itemView.findViewById(R.id.stateImg_two);
            stateImg_three = (ImageView) itemView.findViewById(R.id.stateImg_three);
            stateImg_four = (ImageView) itemView.findViewById(R.id.stateImg_four);

            expressName = (TextView) itemView.findViewById(R.id.expressName);
            expressCode = (TextView) itemView.findViewById(R.id.expressCode);
            state_one = (TextView) itemView.findViewById(R.id.state_one);
            state_two = (TextView) itemView.findViewById(R.id.state_two);
            state_three = (TextView) itemView.findViewById(R.id.state_three);
            state_four = (TextView) itemView.findViewById(R.id.state_four);
            copyBtn = (Button) itemView.findViewById(R.id.copyBtn);
            logistic_progress = (RecyclerView) itemView.findViewById(R.id.logistic_progress);
        }
    }

    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }
}
