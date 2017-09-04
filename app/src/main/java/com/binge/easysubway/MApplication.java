package com.binge.easysubway;

import android.app.Application;

/**
 * Created by Binge on 2017/9/4.
 */

public class MApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CoordinateManager.getInstance().init(this);
    }
}
