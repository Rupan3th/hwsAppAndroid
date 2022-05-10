package com.hws.hwsappandroid.ui.cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.RecyclerViewType;
import com.hws.hwsappandroid.model.SwipeSectionItemModel;
import com.hws.hwsappandroid.model.SwipeSectionModel;
import com.hws.hwsappandroid.util.SectionRecyclerViewSwipeAdapter;

import java.io.File;
import java.util.ArrayList;

public class ShoppingCartAssist extends AppCompatActivity {

    private RecyclerViewType recyclerViewType = RecyclerViewType.LINEAR_VERTICAL;
    RecyclerView recyclerView;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator + "USBCamera/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_shopping_cart);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TextView toolbar_shoppingCart = findViewById(R.id.toolbar_shoppingCart);
        toolbar_shoppingCart.setVisibility(View.VISIBLE);

        LinearLayout bottomCtr = findViewById(R.id.bottomCtr);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) bottomCtr.getLayoutParams();
        layoutParams.setMargins( 0 , 0 , 0 , 0 ) ;

        ImageButton backBtn = findViewById(R.id.button_back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setUpRecyclerView();
        populateRecyclerView();
    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.sectioned_recycler_swipe);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
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
        SectionRecyclerViewSwipeAdapter adapter = new SectionRecyclerViewSwipeAdapter(getApplicationContext(), recyclerViewType, sectionModelArrayList);
        recyclerView.setAdapter(adapter);
    }

}