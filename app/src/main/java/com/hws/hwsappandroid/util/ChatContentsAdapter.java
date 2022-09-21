package com.hws.hwsappandroid.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.LiveChatContents;
import com.hws.hwsappandroid.model.NewsItemModel;
import com.hws.hwsappandroid.util.emoji.EmoJiUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatContentsAdapter extends RecyclerView.Adapter<ChatContentsAdapter.ViewHolder> {

    private Context context;
    public ArrayList<LiveChatContents> models;
    private ItemClickListener mClickListener;
    private String[] read_state = {"未读", "已读"};

    public ChatContentsAdapter(Context context) {
        this.context = context;
        this.models = new ArrayList<>();
    }

    public void setData(ArrayList<LiveChatContents> list) {
        models = list;
        notifyDataSetChanged();
    }

    @Override
    public ChatContentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_chat_item_card, parent, false);
        return new ChatContentsAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void onBindViewHolder(@NonNull ChatContentsAdapter.ViewHolder holder, int position) {
        ChatContentsAdapter.ViewHolder viewHolder = (ChatContentsAdapter.ViewHolder) holder;
        int pos = position;

        if(!models.get(position).time.equals("")){
            viewHolder.msg_time.setVisibility(View.VISIBLE);
            viewHolder.time.setText(models.get(position).time);
        }else {
            viewHolder.msg_time.setVisibility(View.GONE);
        }

        if(models.get(position).SoR == 1){
            viewHolder.receive_msg.setVisibility(View.GONE);
            viewHolder.send_msg.setVisibility(View.VISIBLE);
            try{
                Picasso.get()
                        .load(models.get(position).avatar)
                        .resize(500, 500)
                        .centerCrop()
                        .into(viewHolder.image_me);
            }catch (Exception e){}

            StringBuilder stringBuilder = new StringBuilder(models.get(position).msg);
            viewHolder.message_send.setText(EmoJiUtils.parseEmoJi(0, context, stringBuilder.toString()));
//            viewHolder.message_send.setText(models.get(position).msg);

            if(models.get(position).reading == 0){
                viewHolder.reading_send.setTextColor(Color.parseColor("#FFC9CDD4"));
            }else viewHolder.reading_send.setVisibility(View.INVISIBLE);
            viewHolder.reading_send.setText(read_state[models.get(position).reading]);

        }else{
            viewHolder.receive_msg.setVisibility(View.VISIBLE);
            viewHolder.send_msg.setVisibility(View.GONE);
            Picasso.get()
                    .load(models.get(position).avatar)
                    .resize(500, 500)
                    .centerCrop()
                    .into(viewHolder.image_profile);

            StringBuilder stringBuilder = new StringBuilder(models.get(position).msg);
            viewHolder.message_receive.setText(EmoJiUtils.parseEmoJi(0, context, stringBuilder.toString()));
//            viewHolder.message_receive.setText(models.get(position).msg);

            if(models.get(position).reading == 0){
                viewHolder.reading_receive.setTextColor(Color.parseColor("#FFC9CDD4"));
            }else viewHolder.reading_receive.setVisibility(View.INVISIBLE);
            viewHolder.reading_receive.setText(read_state[models.get(position).reading]);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        ImageView image_profile, image_me;
        LinearLayout msg_time, receive_msg, send_msg;
        TextView time, message_receive, reading_receive, message_send, reading_send;

        public ViewHolder(View itemView) {
            super(itemView);
            image_profile = (ImageView) itemView.findViewById(R.id.image_profile);
            image_me = (ImageView) itemView.findViewById(R.id.image_me);
            msg_time = (LinearLayout) itemView.findViewById(R.id.msg_time);
            receive_msg = (LinearLayout) itemView.findViewById(R.id.receive_msg);
            send_msg = (LinearLayout) itemView.findViewById(R.id.send_msg);
            time = (TextView) itemView.findViewById(R.id.time);
            message_receive = (TextView) itemView.findViewById(R.id.message_receive);
            reading_receive = (TextView) itemView.findViewById(R.id.reading_receive);
            message_send = (TextView) itemView.findViewById(R.id.message_send);
            reading_send = (TextView) itemView.findViewById(R.id.reading_send);

//            itemView.setOnClickListener(this);

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
