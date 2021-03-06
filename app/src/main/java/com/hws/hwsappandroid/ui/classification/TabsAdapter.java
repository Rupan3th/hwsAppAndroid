package com.hws.hwsappandroid.ui.classification;


import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.Category;
import com.hws.hwsappandroid.model.Good;

import java.util.ArrayList;

public class TabsAdapter extends RecyclerView.Adapter<TabsAdapter.ViewHolder> {
        OnTabClick onTabClick;
    public ArrayList<Category> CategoryTree;
    private RecyclerView recyclerView;
    private int currentPage = 0;

    /**建構子*/
    public TabsAdapter(RecyclerView recyclerView) {
        this.CategoryTree = new ArrayList<>();
        this.recyclerView = recyclerView;
    }

    public void setData(ArrayList<Category> list) {
        CategoryTree = list;
        notifyDataSetChanged();
    }
    /**提供一個接口給外部以綁定ViewPager的頁數及Tab顯示*/
    public void setCurrentPage(int position){
        if (currentPage >= 0){
        deSelect(currentPage);
        }
        /**從position中找到itemView*/
        View targetView = recyclerView.getLayoutManager().findViewByPosition(position);
        TextView tvTab = targetView.findViewById(R.id.textView_TabNumber);
        View divider = targetView.findViewById(R.id.divider);
        setTextViewSelected(tvTab, divider);
        /**保證RecyclerViewAdapter中的即時頁碼與被滑到的位置相同*/
        currentPage = position;
        }
    /**使其他沒被滑到會點選到的頁面Tab回復原本的顏色*/
    private void deSelect(int position){
        if (recyclerView.getLayoutManager().findViewByPosition(position)!= null){
        View targetView = recyclerView.getLayoutManager().findViewByPosition(position);
        TextView tvTab = targetView.findViewById(R.id.textView_TabNumber);
        View divider = targetView.findViewById(R.id.divider);
        setTextViewUnSelected(tvTab, divider);
        }
        currentPage = -1;
        }
    /**設置被選到時的UI變化*/
    private void setTextViewSelected(TextView tvTab, View divider){
        tvTab.setTextColor(Color.BLUE);
        tvTab.setTypeface(null, Typeface.BOLD);
        divider.setVisibility(View.VISIBLE);
        }
    /**設置被未被選到時的UI變化*/
    private void setTextViewUnSelected(TextView tvTab, View divider){
        tvTab.setTextColor(Color.BLACK);
        tvTab.setTypeface(null, Typeface.NORMAL);
        divider.setVisibility(View.INVISIBLE);
        }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTab;
        View divider;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTab = itemView.findViewById(R.id.textView_TabNumber);
            divider = itemView.findViewById(R.id.divider);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tabs_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.tvTab.setText(CategoryTree.get(position).categoryName);
        if (position == currentPage) setTextViewSelected(holder.tvTab, holder.divider);
        else setTextViewUnSelected(holder.tvTab, holder.divider);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTabClick.onTabClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return CategoryTree.size();
    }

    interface OnTabClick{
        void onTabClick(int position);
    }
}
