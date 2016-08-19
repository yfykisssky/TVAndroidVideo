package com.android.tvvideo.tools;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by yangfengyuan on 16/8/18.
 */
public class TimerTaskHelper {

    AlarmManager am;

    String broadcastAction="com.android.tvvideo.alarmtimer.";

    Context context;

    int alarmId=0;

    boolean isRegister=false;

    List<TimeModel> timeModels;

    OnStartOrEndListener onStartOrEndListener;

    BroadcastReceiver timeReceiver =new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {

            String kind=intent.getStringExtra("kind");

            int index= intent.getIntExtra("index",0);

            if(kind.equals("start")){
                onStartOrEndListener.onStartOrEnd(true,index);
            }else if(kind.equals("end")){
                onStartOrEndListener.onStartOrEnd(false,index);
            }

        }
    };

    public static class TimeModel{
        public String startTime;
        public String endTime;

        public TimeModel(){

        }

        public TimeModel(String startTime, String endTime){
            this.startTime=startTime;
            this.endTime=endTime;
        }
    }

    public interface OnStartOrEndListener{
        void onStartOrEnd(boolean startOrEnd, int index);
    }

    public TimerTaskHelper(Context context){
        this.context=context;
        am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
    }

    public void setData(List<TimeModel> timeModels){

        this.timeModels=timeModels;

    }

    public void startAndListener(String receiverAction,OnStartOrEndListener onStartOrEndListener){
        broadcastAction+=receiverAction;
        this.onStartOrEndListener=onStartOrEndListener;
        registerReceiver();
        addTimer(timeModels);
    }

    public void stopAndRemove(){
        unregisterReceiver();
        removeTimer();
    }

    private void registerReceiver(){

        if(!isRegister){

            IntentFilter filter = new IntentFilter();
            filter.addAction(broadcastAction);
            context.registerReceiver(timeReceiver,filter);

            isRegister=true;

        }

    }

    private void unregisterReceiver(){

        if(isRegister){

            context.unregisterReceiver(timeReceiver);

            isRegister=false;

        }

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

    private void addTimer(List<TimeModel> timeModels){

        for(int y=0;y<timeModels.size();y++){

            Calendar calendar=formatToCanlendar(timeModels.get(y).startTime);

            Intent intent = new Intent();
            intent.putExtra("index",alarmId);
            intent.putExtra("kind", "start");
            intent.setAction(broadcastAction);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,alarmId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            am.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pendingIntent);

            alarmId++;

            calendar=formatToCanlendar(timeModels.get(y).endTime);

            intent = new Intent();
            intent.putExtra("index",alarmId);
            intent.putExtra("kind", "end");
            intent.setAction(broadcastAction);

            pendingIntent = PendingIntent.getBroadcast(context,alarmId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            am.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pendingIntent);

            alarmId++;

        }

    }

    private void removeTimer(){

        for(int v=0;v<alarmId;v++){
            Intent intent = new Intent();
            intent.setAction(broadcastAction);
            PendingIntent pi=PendingIntent.getBroadcast(context,alarmId, intent,PendingIntent.FLAG_UPDATE_CURRENT);
            am.cancel(pi);
        }

        alarmId=0;

    }

}
