package com.android.tvvideo.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.videolan.libvlc.VLCApplication;

import java.util.List;

/**
 * Created by yangfengyuan on 16/8/18.
 */
public class TimerTaskHelper {

    String broadcastAction="com.android.tvvideo.alarmtimer.";

    String kind;

    Context context;

    int startInt=0;

    int alarmId=0;

    boolean isRegister=false;

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
    }

    public void setData(List<TimeModel> timeModels){

        addTimer(timeModels);

    }

    public void startAndListener(String receiverAction,OnStartOrEndListener onStartOrEndListener){
        broadcastAction+=receiverAction;
        kind=receiverAction;
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

    private void addTimer(List<TimeModel> timeModels){

        for(int y=0;y<timeModels.size();y++){

            if(timeModels.get(y).startTime!=null){

                VLCApplication.getInstance().getTimeTaskService().addToList(kind,"start",timeModels.get(y).startTime,String.valueOf(alarmId),broadcastAction);

            }

            if(timeModels.get(y).endTime!=null){

                VLCApplication.getInstance().getTimeTaskService().addToList(kind,"end",timeModels.get(y).endTime,String.valueOf(alarmId),broadcastAction);

            }

            alarmId++;

        }

    }

    private void removeTimer(){

        alarmId=0;

        VLCApplication.getInstance().getTimeTaskService().removeFromList(kind);

    }

}
