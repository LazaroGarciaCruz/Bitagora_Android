package com.lazarogarciacruz.bitagora.utilidades;

import android.app.Application;
import android.content.Context;

/**
 * Created by lazarogarciacruz on 2/6/17.
 */

public class MyApp extends Application {

    private static MyApp instance;

    public static MyApp getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

}