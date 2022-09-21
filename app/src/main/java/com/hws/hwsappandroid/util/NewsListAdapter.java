package com.hws.hwsappandroid.util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.NewsItemModel;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {

    private Context context;
    public ArrayList<NewsItemModel> models;
    private ItemClickListener mClickListener;

    public NewsListAdapter(Context context) {
        this.context = context;
        this.models = new ArrayList<>();
    }

    public void setData(ArrayList<NewsItemModel> list) {
        models = list;
//        models.sort(new Comparator<NewsItemModel>() {
//            @Override
//            public int compare(NewsItemModel newsItemModel, NewsItemModel t1) {
//                return newsItemModel.receive_time.comp
//                return 0;
//            }
//        });
        notifyDataSetChanged();
    }

    @Override
    public NewsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_card, parent, false);
        return new NewsListAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void onBindViewHolder(@NonNull NewsListAdapter.ViewHolder holder, int position) {
        NewsListAdapter.ViewHolder viewHolder = (NewsListAdapter.ViewHolder) holder;
        int pos = position;

        Picasso.get()
                .load(models.get(position).partner_pic)
                .resize(500, 500)
                .centerCrop()
                .into(viewHolder.images);

        viewHolder.item_name.setText(models.get(position).partner_name);
        viewHolder.contents.setText(models.get(position).message);
        Date receive_time = new Date(models.get(position).receive_time);
        viewHolder.time.setText(formatDate(receive_time));
        int msg_num = models.get(position).msg_num;
        viewHolder.num.setText("" + msg_num);
        if (msg_num == 0) {
            viewHolder.num.setVisibility(View.GONE);
        }
    }

    private String formatDate(Date date) {
        String formatString = "yyyy年MM月dd日";
        Date now = new Date();
        long diff = now.getTime() - date.getTime();
        if (diff < 86400 * 1000) {
            formatString = "HH:mm";
        } else if (diff < 7 * 86400 * 1000) {
            formatString = "EEE";
        }
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(date);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        ImageView images;
        TextView item_name, contents, time, num;

        public ViewHolder(View itemView) {
            super(itemView);
            images = (ImageView) itemView.findViewById(R.id.image_product);
            item_name = (TextView) itemView.findViewById(R.id.title);
            contents = (TextView) itemView.findViewById(R.id.contents);
            time = (TextView) itemView.findViewById(R.id.time);
            num = (TextView) itemView.findViewById(R.id.notifyCompleted);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onClick(view, getPosition()); // call the onClick in the OnItemClickListener
        }

    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
