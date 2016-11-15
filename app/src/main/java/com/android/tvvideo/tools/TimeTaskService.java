package com.android.tvvideo.tools;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangfengyuan on 16/8/11.
 */
public class TimeTaskService extends Service {

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            synchronized (mapList){

                for(int v=0;v<mapList.size();v++){

                    Map<String,String> map=mapList.get(v);

                    String useTime=map.get("time");

                    Calendar cal=formatToCanlendar(useTime);

                    if(cal.getTimeInMillis()<=nowTime){

                        Log.e("check",String.valueOf(cal.getTimeInMillis()));

                        sendTimeBroadcast(map.get("timekind"),Integer.parseInt(map.get("index")),map.get("action"));

                        mapList.remove(map);

                    }

                }

            }

        }
    }

    MyHandler myHandler=new MyHandler();

    boolean isRun=true;

    List<Map<String,String>> mapList=new ArrayList<>();

    long nowTime=0;

    Runnable timeRunnable=new Runnable() {
        @Override
        public void run() {

            while(isRun){

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                nowTime+=500;

                myHandler.sendEmptyMessage(0);

            }

        }
    };

    private void sendTimeBroadcast(String kind,int index,String action) {

        Intent intent=new Intent();

        intent.putExtra("kind",kind);

        intent.putExtra("index",index);

        intent.setAction(action);

        sendBroadcast(intent);

    }


    private Calendar formatToCanlendar(String time){

        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar;

    }

    public void addToList(String kind,String timekind,String time,String index,String action){

        Map<String,String> map=new HashMap<>();

        map.put("kind",kind);

        map.put("timekind",timekind);

        map.put("time",time);

        map.put("index",index);

        map.put("action",action);

        synchronized (mapList) {

            mapList.add(map);

        }

    }

    public void removeFromList(String kind){

        synchronized (mapList){

            for(int v=0;v<mapList.size();v++){

                Map<String,String> map=mapList.get(v);

                if(map.get("kind").equals(kind)){
                    mapList.remove(map);
                }

            }

        }

    }

    private LocalBinder localBinder = new LocalBinder();
    public class LocalBinder extends Binder {

        public TimeTaskService getService() {

            return TimeTaskService.this;

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return localBinder;
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

    public void startTimeTask(String startTime){

        Calendar calendar=formatToCanlendar(startTime);

        checkTime(calendar.getTimeInMillis());

        nowTime=calendar.getTimeInMillis();

        new Thread(timeRunnable).start();

    }

    private void checkTime(long time){

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long checkTime=sdf.parse("2017-3-1 00:00:00").getTime();
            if(checkTime<time){
                throw new IllegalStateException("unknown error,restart again");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


}

