package com.hws.hwsappandroid.util;

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.RecyclerViewType;
import com.hws.hwsappandroid.model.SwipeSectionItemModel;
import com.hws.hwsappandroid.model.SwipeSectionModel;
import com.hws.hwsappandroid.model.UserCartItem;

import java.util.ArrayList;

public class SectionRecyclerViewSwipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View headerView) {
            super(headerView);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        FooterViewHolder(View footerView) {
            super(footerView);
        }
    }

    class SectionViewHolder extends RecyclerView.ViewHolder {
        private CheckBox selectBtn;
        private TextView sectionLabel;
        private RecyclerView itemRecyclerView;

        public SectionViewHolder(View itemView) {
            super(itemView);
            selectBtn = (CheckBox) itemView.findViewById(R.id.radioButton);
            sectionLabel = (TextView) itemView.findViewById(R.id.section_label);
            itemRecyclerView = (RecyclerView) itemView.findViewById(R.id.item_recycler_view);
        }
    }
    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;
    private final int TYPE_FOOTER = 2;

    private Context context;
    private RecyclerViewType recyclerViewType;
    public ArrayList<UserCartItem> models;;
    SwipeController swipeController = null;
    ItemRecyclerViewSwipeAdapter adapter;

    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else if (position == models.size() + 1)
            return TYPE_FOOTER;
        else
            return TYPE_ITEM;
    }

    public void setData(ArrayList<UserCartItem> list) {
        models = list;
        notifyDataSetChanged();
    }

    public SectionRecyclerViewSwipeAdapter(Context context, RecyclerViewType recyclerViewType) {
        this.context = context;
        this.recyclerViewType = recyclerViewType;
        this.models = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View view;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shoppig_cart_header, parent, false);
            holder = new HeaderViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer, parent, false);
            holder = new FooterViewHolder(view);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_swipe_row_layout, parent, false);
            holder = new SectionViewHolder(view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemIndex;
        UserCartItem sectionModel ;

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        }else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
        }else{
            itemIndex = position-1;
            sectionModel = models.get(itemIndex);

            SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
            sectionViewHolder.sectionLabel.setText(sectionModel.shopName);

            //recycler view for items
            sectionViewHolder.itemRecyclerView.setHasFixedSize(true);
            sectionViewHolder.itemRecyclerView.setNestedScrollingEnabled(false);

            /* set layout manager on basis of recyclerview enum type */
            switch (recyclerViewType) {
                case LINEAR_VERTICAL:
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    sectionViewHolder.itemRecyclerView.setLayoutManager(linearLayoutManager);
                    break;
                case LINEAR_HORIZONTAL:
                    LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                    sectionViewHolder.itemRecyclerView.setLayoutManager(linearLayoutManager1);
                    break;
                case GRID:
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
                    sectionViewHolder.itemRecyclerView.setLayoutManager(gridLayoutManager);
                    break;
            }
            adapter = new ItemRecyclerViewSwipeAdapter(context, itemIndex);
            sectionViewHolder.itemRecyclerView.setAdapter(adapter);

            //show toast on click of goto button
            sectionViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "You clicked on Show All of : " + sectionModel.shopName, Toast.LENGTH_SHORT).show();
                }
            });

//            sectionViewHolder.selectBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(sectionViewHolder.selectBtn.isChecked())
//                    {
//                        sectionViewHolder.selectBtn.setChecked(false);
//                        adapter.allSelect = false;
//                        adapter.notifyDataSetChanged();
//                    } else {
//                        sectionViewHolder.selectBtn.setChecked(true);
//                        adapter.allSelect = true;
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//            });

            //swipe menu create
            swipeController = new SwipeController(new SwipeControllerActions() {
                @Override
                public void onRightClicked(int position) {
                    adapter.arrayList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                    Toast.makeText(context, "You clicked "+position, Toast.LENGTH_SHORT).show();
                }
            });
            ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
            itemTouchhelper.attachToRecyclerView(sectionViewHolder.itemRecyclerView);

            sectionViewHolder.itemRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                    swipeController.onDraw(c, "删除", 30, 30);
                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return models.size() + 2;
    }

}
