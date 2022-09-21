package com.hws.hwsappandroid.ui.classification;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.Category;

import java.util.ArrayList;

public class TabsAdapter extends RecyclerView.Adapter<TabsAdapter.ViewHolder> {
    OnTabClick onTabClick;
    public ArrayList<Category> CategoryTree;
    final private RecyclerView recyclerView;
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
        int prevPage = currentPage;
        currentPage = position;

        if (prevPage >= 0){
            deSelect(prevPage);
        }
        /**從position中找到itemView*/
        try{
            View targetView = recyclerView.getLayoutManager().findViewByPosition(position);
            TextView tvTab = targetView.findViewById(R.id.textView_TabNumber);
            View divider = targetView.findViewById(R.id.divider);
            ConstraintLayout select_pan = targetView.findViewById(R.id.select_pan);
            setTextViewSelected(tvTab, divider, select_pan);
        }catch (Exception e){

        }
        /**保證RecyclerViewAdapter中的即時頁碼與被滑到的位置相同*/
    }
    /**使其他沒被滑到會點選到的頁面Tab回復原本的顏色*/
    private void deSelect(int position){
        int pos = position;
        if (recyclerView.getLayoutManager().findViewByPosition(position)!= null){
            View targetView = recyclerView.getLayoutManager().findViewByPosition(position);
            TextView tvTab = targetView.findViewById(R.id.textView_TabNumber);
            View divider = targetView.findViewById(R.id.divider);
            ConstraintLayout select_pan = targetView.findViewById(R.id.select_pan);
            setTextViewUnSelected(position, tvTab, divider, select_pan);
        }
        notifyDataSetChanged();
    }
    /**設置被選到時的UI變化*/
    private void setTextViewSelected(TextView tvTab, View divider, ConstraintLayout select_pan){
//        tvTab.setTextColor(Color.parseColor("#F53F3F"));
        tvTab.setTypeface(null, Typeface.BOLD);
        tvTab.setTextSize(16);
        Rect realSize = new Rect();
        tvTab.getPaint().getTextBounds(tvTab.getText().toString(), 0, tvTab.getText().length(), realSize);
        divider.getLayoutParams().width = realSize.width();
        divider.requestLayout();
        divider.setVisibility(View.VISIBLE);
        select_pan.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }
    /**設置被未被選到時的UI變化*/
    private void setTextViewUnSelected(int pos, TextView tvTab, View divider, ConstraintLayout select_pan){
//        tvTab.setTextColor(Color.parseColor("#4E5969"));
        tvTab.setTypeface(null, Typeface.NORMAL);
        tvTab.setTextSize(14);
        divider.setVisibility(View.INVISIBLE);
        select_pan.setBackgroundColor(Color.parseColor("#F4F4F4"));

        if (pos == currentPage - 1) {
            select_pan.setBackgroundResource(R.drawable.selec_pan_prev_bg);
        } else if (pos == currentPage + 1) {
            select_pan.setBackgroundResource(R.drawable.select_pan_next_bg);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTab;
        View divider;
        ConstraintLayout select_pan;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTab = itemView.findViewById(R.id.textView_TabNumber);
            divider = itemView.findViewById(R.id.divider);
            select_pan = itemView.findViewById(R.id.select_pan);
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
        int pos = holder.getAdapterPosition();
        holder.tvTab.setText(CategoryTree.get(pos).categoryName);
        if (pos == currentPage) setTextViewSelected(holder.tvTab, holder.divider, holder.select_pan);
        else setTextViewUnSelected(pos, holder.tvTab, holder.divider, holder.select_pan);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTabClick.onTabClick(pos);
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
