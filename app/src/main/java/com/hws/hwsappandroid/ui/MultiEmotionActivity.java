package com.hws.hwsappandroid.ui;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.LiveChatContents;
import com.hws.hwsappandroid.model.NewsItemModel;
import com.hws.hwsappandroid.util.ChatContentsAdapter;
import com.hws.hwsappandroid.util.DbOpenHelper;
import com.hws.hwsappandroid.util.JWebSocketClient;
import com.hws.hwsappandroid.util.emoji.EmoJiUtils;
import com.hws.hwsappandroid.util.emoji.EmotionInputDetector;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class MultiEmotionActivity extends AppCompatActivity implements View.OnClickListener, JWebSocketClient.OnNewMessageListener {
    private DbOpenHelper mDbOpenHelper;

    private EditText mInputContainer;
    private Context mContext;
    private EmotionInputDetector mDetector;
    private RecyclerView mChatContent;

    private int currentIndex = 0;
    private int position = 0;


    private RadioButton mWeiBoEmoJi;
    private RadioButton mQQEmoJi;
    private RadioButton mLanXiaoHuaEmoji;
    private TextView mAddView, toolbar_chat;
    private CheckBox mSmileView;
    private ImageButton mSendButton, button_mic, mAttachButton;

    private ChatContentsAdapter mAdapter;
    private ArrayList<LiveChatContents> chatContents = new ArrayList<>();
    String avatar_img;

    private long mNow;
    private Date mDate;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");   //2022年05月09日 10:59
    private SimpleDateFormat hFormat = new SimpleDateFormat("hh:mm");   //10:59
    boolean isNew = true;

    private MediaRecorder mRecorder;

    // creating a variable for mediaplayer class
    private MediaPlayer mPlayer;

    // string variable is created for storing a file name
    private static String mFileName = null;

    // constant for storing audio permission
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    private String ip = "ws://47.108.233.4:9082/businessBackend/WSServer/";
    String shopId="", shopName="", shopLogoPic="";
    String userId, bizClientId, bizOperatorId;
    private JWebSocketClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_emotion);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Intent intent = getIntent();
        shopId = intent.getStringExtra("shopId");
        shopName = intent.getStringExtra("shopName");
        shopLogoPic = intent.getStringExtra("shopLogoPic");
        bizClientId = intent.getStringExtra("bizClientId");
        bizOperatorId = intent.getStringExtra("operatorId");

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        mContext = this;
        SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
        avatar_img = pref.getString("avatar_img", "");
        userId = pref.getString("pkId", "");

//        AuthToken = "eyJhbGciOiJIUzI1NiIsInppcCI6IkRFRiJ9.eNqqViouTVKyUjI0NrQ0MDA2NrA0V9JRKi1OLfJMAQqbmSSlGJsbmxoaWSaapBmbJhmZpKWYWJoZpZoZGgIRUG1eZnK2X2JuKlD18ykrnnVsh5qRWFqSkV-UWZKZWgyUSiwoCAUaChTPKskkxuBaAAAAAP__.5L-46ckuNEKXpe9XGwPoD65caw3TsxfLzV0_VYjIrg4";

        client = JWebSocketClient.getInstance(getApplicationContext());

        if (client == null) finish();
        JWebSocketClient.addMessageListener(this);

        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        toolbar_chat = findViewById(R.id.toolbar_chat);
        toolbar_chat.setText(shopName);

        initView();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        mChatContent.setLayoutManager(linearLayoutManager);
        mAdapter = new ChatContentsAdapter(this);
        mChatContent.setAdapter(mAdapter);
        mAdapter.setData(chatContents);
        mChatContent.scrollToPosition(0);

        mChatContent.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int l, int t, int r, int b, int ol, int ot, int or, int ob) {
                if (b < ob) {
                    mChatContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mChatContent.smoothScrollToPosition(0);
                        }
                    }, 100);
                }
            }
        });

        button_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording();
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time = "";
                String htime = "";
//                if(isNew){
                mNow = System.currentTimeMillis();
                mDate = new Date(mNow);
                time = mFormat.format(mDate);
                htime = hFormat.format(mDate);
//                }

                String msg = mInputContainer.getText().toString();
                LiveChatContents chatMsg = new LiveChatContents();

                Cursor mCursor = mDbOpenHelper.selectColumn(bizOperatorId);
                if(mCursor.getCount() < 1)  //
                    mDbOpenHelper.insertColumn(bizOperatorId, shopName, shopLogoPic, msg, mNow, 1);
                else {
                    mCursor.moveToNext();
                    int msg_num = mCursor.getInt(6);
                    mDbOpenHelper.updateColumn(bizOperatorId, shopName, shopLogoPic, msg, mNow, msg_num);
                }

                chatMsg.avatar = avatar_img;
                chatMsg.msg = msg;
                chatMsg.SoR = 1;
                chatMsg.time = time;
                chatMsg.reading = 0;
                chatMsg.from_id = "";
                chatMsg.to_id = "";
                mDbOpenHelper.insertChatHistory(avatar_img, userId, bizOperatorId,"1", msg, time, "0");

                chatContents.add(0, chatMsg);

                JSONObject jsonParams = new JSONObject();
                try {
//                    jsonParams.put("from", userId);
//                    jsonParams.put("to", bizClientId);
                    jsonParams.put("to", bizOperatorId);
//                    jsonParams.put("time", time);
                    jsonParams.put("platform", "mobile");
                    jsonParams.put("data", msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (client != null && client.isOpen()) {
                    Log.d("Send Message", jsonParams.toString());
                    client.send(jsonParams.toString());
                }

                mAdapter.setData(chatContents);
                mChatContent.scrollToPosition(0);
                mInputContainer.setText("");
                isNew = false;
            }
        });
    }

    @Override
    public void onNewMessage(LiveChatContents chatMsg, int unreadCount) {
        if (!chatMsg.from_id.equals(bizOperatorId)) return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatMsg.avatar = shopLogoPic;
                chatContents.add(0, chatMsg);

                mAdapter.setData(chatContents);
                // maybe need to check if the previous position is bottom
                mChatContent.scrollToPosition(0);

                // and we need to handle read action somewhere
            }
        });
    }

    private void initView() {
        mChatContent = (RecyclerView) findViewById(R.id.listView_Content);
        mSmileView = (CheckBox) findViewById(R.id.cb_smile);
        button_mic = (ImageButton) findViewById(R.id.button_mic);
        mSendButton = (ImageButton) findViewById(R.id.btn_send);
        mAttachButton = (ImageButton) findViewById(R.id.button_attach);
        mInputContainer = (EditText) findViewById(R.id.et_input_container);
        LinearLayout mEmoJiContainer = (LinearLayout) findViewById(R.id.ll_face_container);

        mWeiBoEmoJi = (RadioButton) findViewById(R.id.rb_weibo_emoji);
        mQQEmoJi = (RadioButton) findViewById(R.id.rb_emoji);
        mLanXiaoHuaEmoji = (RadioButton) findViewById(R.id.rb_lanxiaohua);
        mAddView = (TextView) findViewById(R.id.tv_add);
        mAddView.setOnClickListener(this);

        mDetector = EmotionInputDetector.with(this)
                .bindSendButton(mSendButton)
                .bindAttachButton(mAttachButton)
                .bindToEditText(mInputContainer)
                .setEmotionView(mEmoJiContainer)
                .bindToContent(mChatContent)
                .bindToEmotionButton(mSmileView);

        getDatabase(bizOperatorId);

        initPageView();

    }


    private void initPageView() {
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        EmoJiFragment emoJiFragment = new EmoJiFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("EmoJiType", currentIndex);
        emoJiFragment.setArguments(bundle);
        trx.replace(R.id.fl_emoji_contanier, emoJiFragment).commitAllowingStateLoss();
    }

    public void clickFace(View view) {
        switch (view.getId()) {
            case R.id.rb_weibo_emoji:
                position = 0;
                break;
            case R.id.rb_lanxiaohua:
                position = 1;
                break;
            case R.id.rb_emoji:
                position = 2;
                break;

        }
        switchEmoJiBg(position);
    }

    private void switchEmoJiBg(int position) {
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        EmoJiFragment emoJiFragment = new EmoJiFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("EmoJiType", position);
        emoJiFragment.setArguments(bundle);
        trx.replace(R.id.fl_emoji_contanier, emoJiFragment).commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (!mDetector.interceptBackPress()) {
            super.onBackPressed();
        }
    }

    public EditText getEt_input_container() {
        return mInputContainer;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add:
                Toast.makeText(mContext, "add", Toast.LENGTH_SHORT).show();
                position = 0;
                break;
        }
    }

    public void getDatabase(String userId){
        Cursor iCursor = mDbOpenHelper.getChatHistory(userId);
        while(iCursor.moveToNext()){
            LiveChatContents chatMsg = new LiveChatContents();
            chatMsg.from_id = iCursor.getString(2);
            chatMsg.to_id = iCursor.getString(3);
            try {
                chatMsg.SoR = Integer.parseInt(iCursor.getString(4));
            }catch (Exception e){}

            chatMsg.avatar = chatMsg.SoR == 1 ? avatar_img : shopLogoPic;
            chatMsg.msg = iCursor.getString(5);
            chatMsg.time = iCursor.getString(6);

            try {
                chatMsg.reading = Integer.parseInt(iCursor.getString(7));
            }catch (Exception e){}

            chatContents.add(chatMsg);
        }

        mDbOpenHelper.updateChatHistory(bizOperatorId, null, null, null, null, null, "1");
    }

    @Override
    protected void onStop() {
        super.onStop();
        JWebSocketClient.removeMessageListener(this);
        // maybe we need to update unread count at this stage...
    }


    private void startRecording() {
        // check permission method is used to check
        // that the user has granted permission
        // to record nd store the audio.
        if (CheckPermissions()) {
            // we are here initializing our filename variable
            // with the path of the recorded audio file.
            mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFileName += "/AudioRecording.3gp";

            // below method is used to initialize
            // the media recorder class
            mRecorder = new MediaRecorder();

            // below method is used to set the audio
            // source which we are using a mic.
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            // below method is used to set
            // the output format of the audio.
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            // below method is used to set the
            // audio encoder for our recorded audio.
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            // below method is used to set the
            // output file location for our recorded audio
            mRecorder.setOutputFile(mFileName);
            try {
                // below method will prepare
                // our audio recorder class
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("TAG", "prepare() failed");
            }
            // start method will start
            // the audio recording.
            mRecorder.start();
        } else {
            // if audio recording permissions are
            // not granted by user below method will
            // ask for runtime permission for mic and storage.
            RequestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // this method is called when user will
        // grant the permission for audio recording.
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean CheckPermissions() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(MultiEmotionActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    public void playAudio() {
        // for playing our recorded audio
        // we are using media player class.
        mPlayer = new MediaPlayer();
        try {
            // below method is used to set the
            // data source which will be our file name
            mPlayer.setDataSource(mFileName);

            // below method will prepare our media player
            mPlayer.prepare();

            // below method will start our media player.
            mPlayer.start();

        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
    }

    public void pauseRecording() {
//        stopTV.setBackgroundColor(getResources().getColor(R.color.gray));
//        startTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
//        playTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
//        stopplayTV.setBackgroundColor(getResources().getColor(R.color.purple_200));

        // below method will stop
        // the audio recording.
        mRecorder.stop();

        // below method will release
        // the media recorder class.
        mRecorder.release();
        mRecorder = null;
//        statusTV.setText("Recording Stopped");
    }

    public void pausePlaying() {
        // this method will release the media player
        // class and pause the playing of our recorded audio.
        mPlayer.release();
        mPlayer = null;
//        stopTV.setBackgroundColor(getResources().getColor(R.color.gray));
//        startTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
//        playTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
//        stopplayTV.setBackgroundColor(getResources().getColor(R.color.gray));
//        statusTV.setText("Recording Play Stopped");
    }

}