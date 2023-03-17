package com.sbr.sms;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

public class ac extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static Context context1;
    private static boolean load = false;
    public void onCreate() {
        super.onCreate();
        ac.context = getApplicationContext();
        ac.context1 = this;
    }
    public static Context getAppContext() {
        return ac.context;
    }
    public static Context this1() {
        return ac.context1;
    }
    public static boolean getload(){
        return load;
    }
    public static void setload(Boolean load2){
        load = load2;
    }
    public static void hideNav(){
        String tag = "service ";
        Log.v(tag, "hideNav");


    }
}
