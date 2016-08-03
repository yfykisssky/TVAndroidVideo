package com.android.tvvideo.application;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.android.tvvideo.tools.PushService;


/**
 * Created by yfykisssky on 16/7/23.
 */
public class MyApplication{

    static double biggestVolumePercent;

    private static void initPushService(Context context){

        Intent intent=new Intent(context,PushService.class);

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

    public static void initData(Context context){

        initPushService(context);

    }

}
