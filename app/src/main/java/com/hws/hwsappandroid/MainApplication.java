package com.hws.hwsappandroid;

import android.app.Application;

import com.hws.hwsappandroid.api.APIManager;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        APIManager.setContext(getApplicationContext());

        // for test
        APIManager.setAuthToken("eyJhbGciOiJIUzI1NiIsInppcCI6IkRFRiJ9.eNqMjLkNgDAMAHdxTQHBgoQNaOgYwA6RMIhHJKkQu2M2oLzT6W6ImaEDmjbZoYAcw9VPKkzFZMnUlkuLNbfOIaJB8q0z7Buv7S5-HWgLWleKlNN8XJIkxG94nqO-1C9J_vyeFwAA__8.e-MkyREIonx5ypcCRni9mMmW72j09AJzrMpD4h-IFAY");
    }
}
