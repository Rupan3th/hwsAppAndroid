package com.hws.hwsappandroid.ui.cart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.databinding.FragmentShoppingCartBinding;
import com.hws.hwsappandroid.model.RecyclerViewType;
import com.hws.hwsappandroid.model.SwipeSectionItemModel;
import com.hws.hwsappandroid.ui.ChooseCategoryActivity;
import com.hws.hwsappandroid.ui.OrderDetailsActivity;
import com.hws.hwsappandroid.ui.SearchActivity;
import com.hws.hwsappandroid.util.SectionRecyclerViewSwipeAdapter;
import com.hws.hwsappandroid.model.SwipeSectionModel;
import com.hws.hwsappandroid.util.SwipeController;
import com.hws.hwsappandroid.util.SwipeControllerActions;

import java.io.File;
import java.util.ArrayList;

public class ShoppingCartFragment extends Fragment {

    private FragmentShoppingCartBinding binding;
    private RecyclerViewType recyclerViewType = RecyclerViewType.LINEAR_VERTICAL;
    private RecyclerView recyclerView;
    TextView toolbar;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator + "USBCamera/";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ShoppingCartModel shoppingCartViewModel = new ViewModelProvider(this).get(ShoppingCartModel.class);

        binding = FragmentShoppingCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        toolbar = binding.toolbarShoppingCart;
//        final TextView textView = binding.txtShoppingCart;
//        shoppingCartViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        setUpRecyclerView();
        populateRecyclerView();

        Button toSettleBtn = binding.toSettleBtn;
        toSettleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), OrderDetailsActivity.class);
//                i.putExtra("searchWord", s);
                startActivity(i);
            }
        });
        return root;
    }

    private void setUpRecyclerView() {
        recyclerView = binding.sectionedRecyclerSwipe;
//        recyclerView = (RecyclerView) findViewById(R.id.sectioned_recycler_swipe);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void populateRecyclerView() {
        ArrayList<SwipeSectionModel> sectionModelArrayList = new ArrayList<>();
        //for loop for sections
        for (int i = 1; i <= 3; i++) {
            ArrayList<SwipeSectionItemModel> itemArrayList = new ArrayList<>();
            //for loop for items
            for (int j = 1; j <= i; j++) {
                File dir_image = new File(path + "images");
                String image_name = path + "images/" + dir_image.listFiles()[1].getName();
                Bitmap img = BitmapFactory.decodeFile(image_name);
                SwipeSectionItemModel SwipeSectionItem = new SwipeSectionItemModel(false, img,"Item " + j, "冬季/19寸", "300", 3 );
                itemArrayList.add(SwipeSectionItem);
            }

            //add the section and items to array list
            sectionModelArrayList.add(new SwipeSectionModel(false, "Section " + i, itemArrayList));
        }

        SectionRecyclerViewSwipeAdapter adapter = new SectionRecyclerViewSwipeAdapter(getContext(), recyclerViewType, sectionModelArrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(-1)) {
                    toolbar.setVisibility(View.INVISIBLE);
                } else if (!recyclerView.canScrollVertically(1)) {

                } else {
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });

    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
////                finish();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
