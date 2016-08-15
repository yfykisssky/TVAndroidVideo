package com.android.tvvideo.tools;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by yangfengyuan on 16/8/11.
 */
public class TimeTaskService extends Service {

    boolean isRun=true;

   Runnable timeTaskRunnable=new Runnable() {
       @Override
       public void run() {

           while(isRun){

               try {
                   Thread.sleep(500);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }

           }

       }
   };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        startTimeTask();

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRun=false;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void startTimeTask(){

        new Thread(timeTaskRunnable).start();

    }

}

