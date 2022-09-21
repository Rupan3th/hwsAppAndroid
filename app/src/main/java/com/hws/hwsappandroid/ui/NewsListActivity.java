package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.LiveChatContents;
import com.hws.hwsappandroid.model.NewsItemModel;
import com.hws.hwsappandroid.util.DbOpenHelper;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.JWebSocketClient;
import com.hws.hwsappandroid.util.NewsListAdapter;

import java.util.ArrayList;

public class NewsListActivity extends AppCompatActivity implements ItemClickListener, JWebSocketClient.OnNewMessageListener {

    private DbOpenHelper mDbOpenHelper;
    String sort ="time";
    ArrayList<NewsItemModel> NewsList = new ArrayList<>();
    RecyclerView recyclerView;
    ImageView no_message;
    private NewsListAdapter mAdapter;
    EditText edit_text;
    int unReadNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_news_list);



        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

//        mDbOpenHelper.deleteAllColumns();
//        mDbOpenHelper.deleteAllChatHistory();
//        mDbOpenHelper.insertColumn("001", "浑南区珀莱雅旗舰店", "https://huawushang.oss-cn-beijing.aliyuncs.com/hws/REFUND/P202207151140479712.jpg",
//                "消息的聊天内容", "09:00", 12);
//        mDbOpenHelper.insertColumn("002", "服务消息", "https://huawushang.oss-cn-beijing.aliyuncs.com/hws/REFUND/P202207201645328279.jpg",
//                "订单签收，订单发货，退款申请通过、拒绝。", "11:00", 5);

        recyclerView = findViewById(R.id.news_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new NewsListAdapter(this);
        mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);

        getDatabase(sort);
//        getSearchData(sort, "南");

        no_message = findViewById(R.id.no_message);
        if(NewsList.size()>0){
            no_message.setVisibility(View.GONE);
        }

        edit_text = findViewById(R.id.edit_text);
        edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String keyword = edit_text.getText().toString();
                if (!keyword.equals("")) {
                    getSearchData(sort, keyword);
                    mAdapter.setData(NewsList);
                }else {
                    getDatabase(sort);
                    mAdapter.setData(NewsList);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDatabase(sort);
    }

    @Override
    public void onClick(View view, int position) {
        Intent LC = new Intent(NewsListActivity.this, MultiEmotionActivity.class);
        LC.putExtra("shopId", NewsList.get(position).partner_id);
        LC.putExtra("shopName", NewsList.get(position).partner_name);
        LC.putExtra("shopLogoPic", NewsList.get(position).partner_pic);
        LC.putExtra("operatorId", NewsList.get(position).partner_id);
        // operatorId
        startActivity(LC);

        if(NewsList.get(position).msg_num > 0)
            updateDatabase(NewsList.get(position), 0);
    }

    @Override
    public void onNewMessage(LiveChatContents chatMsg, int unreadCount) {
        getDatabase(sort);
    }

    public void getDatabase(String sort){
        Cursor iCursor = mDbOpenHelper.sortColumn(sort);
//        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        NewsList.clear();
//        arrayIndex.clear();
        while(iCursor.moveToNext()){
            NewsItemModel newsItem = new NewsItemModel();
            newsItem.partner_id = iCursor.getString(1);
            newsItem.partner_name = iCursor.getString(2);
            newsItem.partner_pic = iCursor.getString(3);
            newsItem.message = iCursor.getString(4);
            newsItem.receive_time = iCursor.getLong(5);

            try{
//                newsItem.msg_num = Integer.parseInt(iCursor.getString(6));
                newsItem.msg_num = mDbOpenHelper.getUnreadMsgPerShop(newsItem.partner_id);
                unReadNum = unReadNum + unReadNum;
            }catch (Exception e){}

            NewsList.add(newsItem);
        }
//        mDbOpenHelper.close();

        mAdapter.setData(NewsList);
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        if(unReadNum > 0)   toolbar_title.setText(getResources().getString(R.string.news) + "(" + unReadNum + ")");
        else toolbar_title.setText(getResources().getString(R.string.news));
    }

    public void updateDatabase(NewsItemModel newsItem, int num){

        boolean result = mDbOpenHelper.updateColumn(newsItem.partner_id, newsItem.partner_name, newsItem.partner_pic, newsItem.message, newsItem.receive_time, num);

        if(result){
            getDatabase(sort);
        }
    }

    public void getSearchData(String sort, String word){
        Cursor jCursor = mDbOpenHelper.sortColumnWording(sort, word);
//        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        NewsList.clear();
//        arrayIndex.clear();
        while(jCursor.moveToNext()){
            NewsItemModel newsItem = new NewsItemModel();
            newsItem.partner_id = jCursor.getString(1);
            newsItem.partner_name = jCursor.getString(2);
            newsItem.partner_pic = jCursor.getString(3);
            newsItem.message = jCursor.getString(4);
            newsItem.receive_time = jCursor.getLong(5);

            try{
//                newsItem.msg_num = Integer.parseInt(jCursor.getString(6));
                newsItem.msg_num = mDbOpenHelper.getUnreadMsgPerShop(newsItem.partner_id);
            }catch (Exception e){}

            NewsList.add(newsItem);
        }
//        mDbOpenHelper.close();
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}