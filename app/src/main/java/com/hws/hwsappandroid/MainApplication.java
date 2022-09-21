package com.hws.hwsappandroid;

import android.app.Application;
import android.content.SharedPreferences;

import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.util.JWebSocketClient;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

//        String default_token = "eyJhbGciOiJIUzI1NiIsInppcCI6IkRFRiJ9.eNqMjEEOgyAQRe8y6y4YBjB4AzddNPEAQ8FImyoRWBnv7hyhyV-9__JOqD3ACEjolSJSfoAH9JqOKQp2JkQayKL2bBayQZslGu90cogycbf8_j75l8R-9cIbtVUo97buR245VTm4lFmSwj8t_5O9bgAAAP__.7nYBAhvMAyL8N4mIlB-YqVGVFqekKXH2RMPu16t0zXI";
        String default_token = "";

        SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
        String AuthToken = pref.getString("token", default_token);
//        AuthToken = default_token;

        APIManager.setContext(getApplicationContext());
        APIManager.setAuthToken(AuthToken);

        // try connect websocket
        JWebSocketClient.getInstance(getApplicationContext());
    }
}

