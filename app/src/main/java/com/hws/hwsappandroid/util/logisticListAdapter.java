package com.hws.hwsappandroid.util;

import static java.lang.String.format;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.LogisticsStateList;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class logisticListAdapter extends RecyclerView.Adapter<logisticListAdapter.ViewHolder> {
    private Context context;
    public ArrayList<LogisticsStateList> models;

    public logisticListAdapter(Context context) {
        this.context = context;
        this.models = new ArrayList<>();
    }

    public void setData(ArrayList<LogisticsStateList> list) {
        models = list;
        notifyDataSetChanged();
    }

    @Override
    public logisticListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.logistics_progress_card, parent, false);
        return new logisticListAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public void onBindViewHolder(@NonNull logisticListAdapter.ViewHolder holder, int position) {
        logisticListAdapter.ViewHolder viewHolder = (logisticListAdapter.ViewHolder) holder;
        int pos = position;

        String date_time = models.get(position).time;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try{
            Date date = formatter.parse(date_time);
            String formatDate = "M月dd日";
            String formatTime = "HH:mm";

            SimpleDateFormat format_t = new SimpleDateFormat(formatDate);
            String mdate = format_t.format(date);
            viewHolder.date.setText(mdate);

            format_t = new SimpleDateFormat(formatTime);
            String times = format_t.format(date);
            viewHolder.time.setText(times);
        }catch (Exception e){}

        viewHolder.progress_txt.setText(models.get(position).status);
//        viewHolder.progress_time.setText(models.get(position).time);

        if(pos == 0){
            viewHolder.circle_shape.setImageResource(R.drawable.circle_red_stroke_2);
            viewHolder.progress_txt.setTextColor(context.getResources().getColor(R.color.text_main));
            viewHolder.time.setTextColor(context.getResources().getColor(R.color.text_main));
            viewHolder.date.setTextColor(context.getResources().getColor(R.color.text_main));

        }else viewHolder.circle_shape.setImageResource(R.drawable.circle_gray_stroke_2);

        if(models.size() > 0 && pos == models.size()-1){
            viewHolder.dotted_bar.setVisibility(View.INVISIBLE);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView progress_txt, progress_time, time, date;
        ImageView circle_shape, dotted_bar;

        public ViewHolder(View itemView) {
            super(itemView);
            progress_txt = (TextView) itemView.findViewById(R.id.progress_txt);
            time = (TextView) itemView.findViewById(R.id.time);
            date = (TextView) itemView.findViewById(R.id.date);
            progress_time = (TextView) itemView.findViewById(R.id.progress_time);
            circle_shape = (ImageView) itemView.findViewById(R.id.circle_shape);
            dotted_bar = (ImageView) itemView.findViewById(R.id.dotted_bar);
        }
    }

}
