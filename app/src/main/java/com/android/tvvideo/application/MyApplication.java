package com.android.tvvideo.application;

import android.app.Application;


/**
 * Created by yfykisssky on 16/7/23.
 */
public class MyApplication extends Application {

    public void initData(){



    }

    @Override
    public void onCreate() {
        super.onCreate();

        initData();
    }
}
