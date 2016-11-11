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

    public static final int START_INT_1=0;

    public static final int START_INT_2=500000000;

    public static final int START_INT_3=1000000000;

    public static final int START_INT_4=1500000000;

    AlarmManager am;

    String broadcastAction="com.android.tvvideo.alarmtimer.";

    Context context;

    int startInt=0;

    int alarmId=0;

    boolean isRegister=false;

    //List<TimeModel> timeModels=new ArrayList<>();

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

    public int getStartInt() {
        return startInt;
    }

    public void setStartInt(int startInt) {
        this.startInt = startInt;

        alarmId=startInt;
    }

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

        addTimer(timeModels);

    }

    public void startAndListener(String receiverAction,OnStartOrEndListener onStartOrEndListener){
        broadcastAction+=receiverAction;
        this.onStartOrEndListener=onStartOrEndListener;
        registerReceiver();
    }

    public void stopAndRemove(){
        unregisterReceiver();
        removeTimer();
    }

    public void removeAllTimers(){
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

            if(timeModels.get(y).startTime!=null){

                Calendar calendar=formatToCanlendar(timeModels.get(y).startTime);

                Intent intent = new Intent();
                intent.putExtra("index",alarmId-startInt);
                intent.putExtra("kind", "start");
                intent.setAction(broadcastAction);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,alarmId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                am.set(AlarmManager.RTC,calendar.getTimeInMillis(), pendingIntent);

                alarmId++;

            }

            if(timeModels.get(y).endTime!=null){

                Calendar calendar=formatToCanlendar(timeModels.get(y).endTime);

                Intent intent = new Intent();
                intent.putExtra("index",alarmId-startInt);
                intent.putExtra("kind", "end");
                intent.setAction(broadcastAction);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,alarmId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                am.set(AlarmManager.RTC,calendar.getTimeInMillis(), pendingIntent);

                alarmId++;

            }

        }

    }

    private void removeTimer(){

        for(int v=startInt;v<alarmId;v++){
            Intent intent = new Intent();
            intent.setAction(broadcastAction);
            PendingIntent pi=PendingIntent.getBroadcast(context,alarmId, intent,PendingIntent.FLAG_UPDATE_CURRENT);
            am.cancel(pi);
        }

        //this.timeModels.clear();

        alarmId=startInt;

    }

}
