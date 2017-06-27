package com.lazarogarciacruz.bitagora.utilidades;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by lazarogarciacruz on 2/6/17.
 */

public class MyApp extends Application {

    private static MyApp instance;
    private float widthPixels;
    private float heightPixels;
    private float widthDP;
    private float heightDP;
    private boolean isSmallScreen;

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

    public void configureScreenSettings(Display display) {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        heightDP = displayMetrics.heightPixels / displayMetrics.density;
        widthDP = displayMetrics.widthPixels / displayMetrics.density;

        /*WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();*/
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        // since SDK_INT = 1;
        MyApp.getInstance().setWidthPixels(metrics.widthPixels);
        MyApp.getInstance().setHeightPixels(metrics.heightPixels);
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                MyApp.getInstance().setWidthPixels((Integer) Display.class.getMethod("getRawWidth").invoke(display));
                MyApp.getInstance().setHeightPixels((Integer) Display.class.getMethod("getRawHeight").invoke(display));
            } catch (Exception ignored) {
            }
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
                MyApp.getInstance().setWidthPixels(realSize.x);
                MyApp.getInstance().setHeightPixels(realSize.y);
            } catch (Exception ignored) {
            }

        isSmallScreen = false;
        if (heightDP < 600) {
            isSmallScreen = true;
        }

        Toast toast = Toast.makeText(MyApp.getContext(), Math.round(heightPixels) + "x" + Math.round(widthPixels) + " - " + Math.round(heightDP) + "x" + Math.round(widthDP) , Toast.LENGTH_LONG);
        //toast.show();

    }

    public float getWidthPixels() {
        return widthPixels;
    }

    public void setWidthPixels(float widthPixels) {
        this.widthPixels = widthPixels;
    }

    public float getHeightPixels() {
        return heightPixels;
    }

    public void setHeightPixels(float heightPixels) {
        this.heightPixels = heightPixels;
    }

    public boolean isSmallScreen() {
        return isSmallScreen;
    }

}