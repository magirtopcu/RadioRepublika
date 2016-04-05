package com.alchemik.radiorepublika;

import android.app.Application;
import android.util.Log;

/**
 * Created by Leszek Jasek on 29.03.2016.
 */
public class Main extends Application {

    private static final String TAG = Main.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }
}
