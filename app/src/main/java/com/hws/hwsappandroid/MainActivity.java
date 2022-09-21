package com.hws.hwsappandroid;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.hws.hwsappandroid.databinding.ActivityMainBinding;
import com.hws.hwsappandroid.service.JWebSocketClientService;
import com.hws.hwsappandroid.ui.cart.ShoppingCartModel;
import com.hws.hwsappandroid.util.JWebSocketClient;
import com.hws.hwsappandroid.util.MyGlobals;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private JWebSocketClientService.JWebSocketClientBinder binder;
    private JWebSocketClientService jWebSClientService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //Service and activity bound successfully
            binder = (JWebSocketClientService.JWebSocketClientBinder) iBinder;
            jWebSClientService = binder.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //Service and activity disconnection
        }
    };

    public static Activity MActivity;
    private ActivityMainBinding binding;
    int total_goods = 0;
    boolean isCounted;
    BottomNavigationView navView;
    BottomNavigationMenuView menuView;
    BottomNavigationItemView itemView;
    View notificationBadge;
    TextView textView;
    Window window;
    View decorView;
    ShoppingCartModel model;
    public Locale locale;

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

        bindService();
        MActivity = MainActivity.this;

//        decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
        String Lang = pref.getString("Lang", "");
        locale = new Locale(Lang);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
        menuView = (BottomNavigationMenuView) navView.getChildAt(0);
        itemView = (BottomNavigationItemView) menuView.getChildAt(3);
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.notification_badge, menuView, false);
        textView = notificationBadge.findViewById(R.id.counter_badge);
        itemView.addView(notificationBadge);

        model = new ViewModelProvider(this).get(ShoppingCartModel.class);
        model.loadData();
        model.getMyCart().observe(this, mCarts -> {
            for(int i=0; i<mCarts.size(); i++){
                total_goods = total_goods + mCarts.get(i).goods.size();
            }
            textView.setText(""+total_goods);

            if(total_goods == 0) textView.setVisibility(View.INVISIBLE);
            else textView.setVisibility(View.VISIBLE);

            MyGlobals.getInstance().setNotify_cart(total_goods);
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_classification, R.id.navigation_lookout, R.id.navigation_shopping_cart, R.id.navigation_me)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void refresh_badge(){
//        menuView = (BottomNavigationMenuView) navView.getChildAt(0);
//        itemView = (BottomNavigationItemView) menuView.getChildAt(3);
//
//        notificationBadge = LayoutInflater.from(this).inflate(R.layout.notification_badge, menuView, false);
//        textView = notificationBadge.findViewById(R.id.counter_badge);
        int cart_contentsNum = MyGlobals.getInstance().getNotify_cart();

        if(cart_contentsNum == 0) {
            textView.setVisibility(View.INVISIBLE);
            textView.setText(""+cart_contentsNum);
        }
        else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(""+cart_contentsNum);
        }
//        itemView.addView(notificationBadge);
    }

    public void reload_badge(){
        total_goods = 0;
        isCounted = false;
//        model.loadData();
        model.getMyCart().observe(this, mCarts -> {
            if(!isCounted){
                for(int i=0; i<mCarts.size(); i++){
                    total_goods = total_goods + mCarts.get(i).goods.size();
                }
                isCounted = true;
            }

            textView.setText(""+total_goods);

            if(total_goods == 0) textView.setVisibility(View.INVISIBLE);
            else textView.setVisibility(View.VISIBLE);

            MyGlobals.getInstance().setNotify_cart(total_goods);
        });
    }

    private void bindService() {
        Intent bindIntent = new Intent(MainActivity.this, JWebSocketClientService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
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