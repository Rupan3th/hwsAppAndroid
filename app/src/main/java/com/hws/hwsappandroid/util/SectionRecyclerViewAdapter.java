package com.hws.hwsappandroid.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.Category_level_1;
import com.hws.hwsappandroid.model.Category_level_2;
import com.hws.hwsappandroid.model.Category_level_3;
import com.hws.hwsappandroid.model.Children_level_1;
import com.hws.hwsappandroid.model.Children_level_2;
import com.hws.hwsappandroid.model.RecyclerViewType;
import com.hws.hwsappandroid.model.SectionModel;
import com.hws.hwsappandroid.ui.classification.ClassificationViewModel;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SectionRecyclerViewAdapter extends RecyclerView.Adapter<SectionRecyclerViewAdapter.SectionViewHolder> {

    class SectionViewHolder extends RecyclerView.ViewHolder {
        private TextView sectionLabel;
        private RecyclerView itemRecyclerView;

        public SectionViewHolder(View itemView) {
            super(itemView);
            sectionLabel = (TextView) itemView.findViewById(R.id.section_label);
            itemRecyclerView = (RecyclerView) itemView.findViewById(R.id.item_recycler_view);
        }
    }

    private Context context;
    private RecyclerViewType recyclerViewType;
    private ArrayList<Children_level_1> sectionModelArrayList = new ArrayList<>();
    private ArrayList<Children_level_2> itemArrayList = new ArrayList<>();
    ItemRecyclerViewAdapter adapter;

    public SectionRecyclerViewAdapter(Context context, RecyclerViewType recyclerViewType) {
        this.context = context;
        this.recyclerViewType = recyclerViewType;
    }

    public void setData(ArrayList<Children_level_1> list) {
        sectionModelArrayList = list;
        notifyDataSetChanged();
    }

    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_custom_row_layout, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position) {
        holder.sectionLabel.setText(sectionModelArrayList.get(position).categoryName);

        //recycler view for items
        holder.itemRecyclerView.setHasFixedSize(true);
        holder.itemRecyclerView.setNestedScrollingEnabled(false);

        /* set layout manager on basis of recyclerview enum type */
        switch (recyclerViewType) {
            case LINEAR_VERTICAL:
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                holder.itemRecyclerView.setLayoutManager(linearLayoutManager);
                break;
            case LINEAR_HORIZONTAL:
                LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                holder.itemRecyclerView.setLayoutManager(linearLayoutManager1);
                break;
            case GRID:
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
                holder.itemRecyclerView.setLayoutManager(gridLayoutManager);
                break;
        }
        adapter = new ItemRecyclerViewAdapter(context, sectionModelArrayList.get(position).categoryName, sectionModelArrayList.get(position).childrenList);
        holder.itemRecyclerView.setAdapter(adapter);
//        adapter.setData(sectionModelArrayList.get(position).category_level3);


    }

    @Override
    public int getItemCount() {
        return sectionModelArrayList.size();
    }


}
