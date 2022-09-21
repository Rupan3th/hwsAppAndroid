package com.hws.hwsappandroid.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.GoodClass;

import java.util.ArrayList;

public class ClassesListAdapter extends RecyclerView.Adapter<ClassesListAdapter.ViewHolder> {
    private Context context;
    public ArrayList<GoodClass> models;
    private HN_ItemClickListener clickListener;

    public ClassesListAdapter(Context context) {
        this.context = context;
        this.models = new ArrayList<>();
    }

    public void setData(ArrayList<GoodClass> list) {
        models = list;
        notifyDataSetChanged();
    }

    @Override
    public ClassesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_class_item, parent, false);
//        view.setLayoutParams(new ViewGroup.LayoutParams((int) (200),ViewGroup.LayoutParams.MATCH_PARENT));

        return new ClassesListAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ClassesListAdapter.ViewHolder holder, int position) {
        int pos= position;
        ClassesListAdapter.ViewHolder viewHolder = (ClassesListAdapter.ViewHolder) holder;
        viewHolder.class_name.setText(models.get(position).categoryName);
        if(models.get(pos).selected == true){
            viewHolder.class_name.setTypeface(Typeface.DEFAULT_BOLD);
            viewHolder.class_name.setTextSize(16);
            viewHolder.select_ring.setVisibility(View.VISIBLE);
        }else{
            viewHolder.class_name.setTypeface(Typeface.DEFAULT);
            viewHolder.class_name.setTextSize(14);
            viewHolder.select_ring.setVisibility(View.INVISIBLE);
        }
//        viewHolder.class_name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                for(int i=0; i<models.size(); i++){
//                    models.get(i).selected = false;
//                }
//                models.get(pos).selected = true;
//                notifyDataSetChanged();
//            }
//        });
    }

    public void setClickListener(HN_ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        TextView class_name;
        ImageView select_ring;

        public ViewHolder(View itemView) {
            super(itemView);
            class_name = (TextView) itemView.findViewById(R.id.className);
            select_ring = (ImageView) itemView.findViewById(R.id.select_ring);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getPosition()); // call the onClick in the OnItemClickListener
        }

    }
}
