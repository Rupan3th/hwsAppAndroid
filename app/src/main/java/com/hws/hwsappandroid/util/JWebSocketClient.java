package com.hws.hwsappandroid.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.hws.hwsappandroid.model.LiveChatContents;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLParameters;

public class JWebSocketClient extends WebSocketClient {
    private static JWebSocketClient singleton_instance = null;

    private static final String WEB_SOCKET_URL = "ws://47.108.233.4:9082/businessBackend/WSServer/";
    private Context context;
    private DbOpenHelper mDbOpenHelper;
    private static List<OnNewMessageListener> messageListeners = new ArrayList<>();

    private JWebSocketClient(URI serverUri, Context ctx) {
        super(serverUri, new Draft_6455());
        context = ctx;
        mDbOpenHelper = new DbOpenHelper(ctx);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
    }

    public static JWebSocketClient getInstance(Context ctx) {

        if (singleton_instance == null) {
            SharedPreferences pref = ctx.getSharedPreferences("user_info", MODE_PRIVATE);
            String AuthToken = pref.getString("token", "");
            if (AuthToken.length() > 0) {
                URI uri = URI.create(WEB_SOCKET_URL + AuthToken);
                singleton_instance = new JWebSocketClient(uri, ctx);

                try {
                    singleton_instance.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return singleton_instance;
    }

    @Override
    protected void onSetSSLParameters(SSLParameters sslParameters) {
//        super.onSetSSLParameters(sslParameters);
    }

    @Override
    public void onOpen(ServerHandshake handShakeData) {//在webSocket连接开启时调用
        Log.d("JWebSClientService", "Websocket opened");
    }

    @Override
    public void onMessage(String message) {//接收到消息时调用
        Log.d("JWebSClientService", message);
        LiveChatContents chatMsg = new LiveChatContents();
        chatMsg.SoR = 2;
        try {
            JSONObject jsonMessage = new JSONObject(message.substring(1, message.length()-1).replace("\\\"", "\""));
            String from = jsonMessage.optString("from", "");
            if (from.length() == 0) {
                // system message
            } else {
                chatMsg.from_id = from;
                chatMsg.time = jsonMessage.optString("time", "");
                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(chatMsg.time);
                chatMsg.time = new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(date);
                chatMsg.msg = jsonMessage.optString("data", "");
                chatMsg.reading = 0;

                SharedPreferences pref = context.getSharedPreferences("user_info", MODE_PRIVATE);
                String userId = pref.getString("pkId", "");
                mDbOpenHelper.insertChatHistory("", from, userId,
                        "2", chatMsg.msg, chatMsg.time, "0");

//                Cursor mCursor = mDbOpenHelper.selectColumn(chatMsg.from_id);
//                if(mCursor.getCount() < 1)  //
//                    mDbOpenHelper.insertColumn(chatMsg.from_id, shopName, shopLogoPic, chatMsg.msg, date.getTime(), 1);
//                else {
//                    mCursor.moveToNext();
//                    int msg_num = mCursor.getInt(6);
//                    mDbOpenHelper.updateColumn(chatMsg.from_id, null, null, chatMsg.msg, date.getTime(), msg_num);
//                }

                mDbOpenHelper.updateColumn(chatMsg.from_id, null, null, chatMsg.msg, date.getTime(), 0);

                int unreadCount = mDbOpenHelper.getUnreadMessageCount();
                for (OnNewMessageListener listener: messageListeners) {
                    listener.onNewMessage(chatMsg, unreadCount);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getUnreadCount () {
        return mDbOpenHelper.getUnreadMessageCount();
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {//在连接断开时调用
        Log.d("JWebSClientService", "Websocket closed");
    }

    @Override
    public void onError(Exception ex) {//在连接出错时调用
        ex.printStackTrace();
    }

    public interface OnNewMessageListener {
        void onNewMessage(LiveChatContents chatMsg, int unreadCount);
    }

    public static void addMessageListener(OnNewMessageListener listener) {
        messageListeners.add(listener);
    }

    public static void removeMessageListener(OnNewMessageListener listener) {
        messageListeners.remove(listener);
    }
}