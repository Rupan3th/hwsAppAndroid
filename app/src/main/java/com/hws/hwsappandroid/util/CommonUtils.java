package com.hws.hwsappandroid.util;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.util.TypedValue;

import com.hws.hwsappandroid.R;
import com.walnutlabs.android.ProgressHUD;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {

    public static String getLeftTime(Context context, int seconds) {
        int m = seconds / 60;
        int mod = seconds % 60;

        if(m == 0) {
            return Integer.toString(mod) + context.getResources().getString(R.string.str_second);
        }else{
            return Integer.toString(m) + context.getResources().getString(R.string.str_minute) + " " + Integer.toString(mod) + context.getResources().getString(R.string.str_second);
        }
    }

    public static String getLeftHour(Context context, int seconds) {
        int hour = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int mod = seconds % 60;

        String string_min = Integer.toString(m);
        if(m < 10) string_min =  "0" + Integer.toString(m);

        String string_sec = Integer.toString(mod);
        if(mod < 10) string_sec =  "0" + Integer.toString(mod);

//        return Integer.toString(hour) + context.getResources().getString(R.string.str_hour) + " " +
//                string_min + context.getResources().getString(R.string.str_minute) + " " +
//                string_sec + context.getResources().getString(R.string.str_second) + "钟";

        return Integer.toString(hour) + ":" + string_min + ":" + string_sec;
    }

    public static int getPixelValue(Context context, float dipValue) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dipValue,
                resources.getDisplayMetrics()
        );
    }

    public static int getDpValue(Context context, int px) {
        Resources resources = context.getResources();
        return (int) (px / resources.getDisplayMetrics().density);
    }

    public static String getUrlEncoded(String url) {
        String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
        return Uri.encode(url, ALLOWED_URI_CHARS);
    }

    public static boolean isDateValid(int y, int m, int d){
        String dateToValidate = Integer.toString(y) + "-" + Integer.toString(m + 1) + "-" + Integer.toString(d);
        String dateFormat = "yyyy-MM-dd";

        if(dateToValidate == null){
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);

        try {
            //if not valid, it will throw ParseException
            Date date = sdf.parse(dateToValidate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Boolean isValidInteger(String value) {
        try {
            Integer val = Integer.valueOf(value);
            if (val != null)
                return true;
            else
                return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int getMonthFromString(String m) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int result = 0;
        for(int i=0; i<12; i++) {
            if (months[i].equals(m)) {
                result = i + 1;
                break;
            }
        }
        return result;
    }

    public static void dismissProgress(final ProgressHUD progressHUD) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressHUD.dismiss();
            }
        }, 400);
    }
}
