package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.databinding.ActivityLiveChatBinding;
import com.hws.hwsappandroid.util.SoftKeyboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class LiveChatActivity extends AppCompatActivity {

    private ActivityLiveChatBinding binding;

    private Handler mHandler;
    InetAddress serverAddr;
    Socket socket;
    PrintWriter sendWriter;
    private String ip = "ws://47.108.233.4:9082/businessBackend/WSServer/";
    private int port = 9082;

    TextView textView;
    String UserID;
    Button connectbutton;
    ImageButton button_send;
    TextView chatView;
    EditText message;
    String sendmsg;
    String read;

    private int originHeight = -1;
    View rootView;
    int rootHeight;

    @Override
    protected void onStop() {
        super.onStop();
        try {
            sendWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);

        SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
        String AuthToken = pref.getString("token", "11");
        ip = ip + AuthToken;

        rootView = findViewById(R.id.live_chatting);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            getKeyboardHeight(rootView);
        });

        mHandler = new Handler();
        textView = (TextView) findViewById(R.id.textView);
        chatView = (TextView) findViewById(R.id.chatView);
        message = (EditText) findViewById(R.id.edit_text);
        Intent intent = getIntent();
        UserID = intent.getStringExtra("username");
        UserID = "Client1";
        textView.setText(UserID);
        button_send = (ImageButton) findViewById(R.id.button_send);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow(); // in Activity's onCreate() for instance
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }//
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.purple_500));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);

//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding = ActivityLiveChatBinding.inflate(getLayoutInflater());

        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        new Thread() {
            public void run() {
                try {
                    InetAddress serverAddr = InetAddress.getByName(ip);
                    socket = new Socket(serverAddr, port);
                    sendWriter = new PrintWriter(socket.getOutputStream());
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while(true){
                        read = input.readLine();

                        System.out.println("TTTTTTTT"+read);
                        if(read!=null){
                            mHandler.post(new msgUpdate(read));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } }}.start();

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmsg = message.getText().toString();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sendWriter.println(UserID +">"+ sendmsg);
                            sendWriter.flush();
                            message.setText("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });



        InputMethodManager controlManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        SoftKeyboard softKeyboard = new SoftKeyboard(binding.liveChatting, controlManager);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // 원하는 동작
                    }

                });
            }

            @Override
            public void onSoftKeyboardShow() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        rootView.getLayoutParams().width = rootHeight;
                        rootView.requestLayout();
                    }

                });
            }
        });

    }

    private int getKeyboardHeight(View targetView) {
        if (targetView.getHeight() > originHeight) {
            originHeight = targetView.getHeight();
        }

        Rect visibleFrameSize = new Rect();
        rootView.getWindowVisibleDisplayFrame(visibleFrameSize);

        int visibleFrameHeight = visibleFrameSize.bottom - visibleFrameSize.top;
        int keyboardHeight = originHeight - visibleFrameHeight;
        rootHeight = keyboardHeight;
        return keyboardHeight;
    }


    class msgUpdate implements Runnable{
        private String msg;
        public msgUpdate(String str) {this.msg=str;}

        @Override
        public void run() {
            chatView.setText(chatView.getText().toString()+msg+"\n");
        }
    }
}