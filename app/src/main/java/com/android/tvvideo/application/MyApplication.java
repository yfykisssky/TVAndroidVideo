package com.android.tvvideo.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.android.tvvideo.tools.PushService;


/**
 * Created by yfykisssky on 16/7/23.
 */
public class MyApplication extends Application {

    private void initPushService(Context context){

        Intent intent=new Intent(this,PushService.class);

        ServiceConnection conn=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        context.bindService(intent,conn,Context.BIND_AUTO_CREATE);

    }

    public void initData(){

        initPushService(this);

    }

    @Override
    public void onCreate() {
        super.onCreate();

        initData();
    }
}
