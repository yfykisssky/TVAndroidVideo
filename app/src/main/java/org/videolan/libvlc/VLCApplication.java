/*****************************************************************************
 * VLCApplication.java
 *****************************************************************************
 * Copyright © 2010-2013 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/
package org.videolan.libvlc;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.IBinder;

import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.tools.CrashHandler;
import com.android.tvvideo.tools.ImageLoad;
import com.android.tvvideo.tools.PushService;
import com.android.tvvideo.tools.SystemUtil;
import com.android.tvvideo.tools.TimerTaskHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.android.tvvideo.base.BaseActivity.BROAD_CAST_ACTION_UI;
import static com.android.tvvideo.tools.PushService.BROAD_CAST_ACTION;

public class VLCApplication extends Application {

    private static VLCApplication instance;


    TimerTaskHelper onOffTaskHelper;

    TimerTaskHelper maxVolumeTaskHelper;

    List<String> maxVolumePercents=new ArrayList<>();


    String patientNum;

    String patientPhoneNum;


    private void initAll(){

        onOffTaskHelper=new TimerTaskHelper(this);

        onOffTaskHelper.setStartInt(TimerTaskHelper.START_INT_1);

        initOnOffTimer();

        maxVolumeTaskHelper=new TimerTaskHelper(this);

        maxVolumeTaskHelper.setStartInt(TimerTaskHelper.START_INT_2);

        initMaxVolumeTimer();

        CrashHandler.getInstance().init(this.getApplicationContext());

        ImageLoad.init(this);

        registerPushReceiver();

    }

    public String getPatientNum() {
        return patientNum;
    }

    public void setPatientNum(String patientNum) {
        this.patientNum = patientNum;
    }

    public String getPatientPhoneNum() {
        return patientPhoneNum;
    }

    public void setPatientPhoneNum(String patientPhoneNum) {
        this.patientPhoneNum = patientPhoneNum;
    }

    private static double MAX_VOLUME=-1;

    public double getMaxVolume() {
        return MAX_VOLUME;
    }

    public void setMaxVolume(double maxVolume) {
        MAX_VOLUME = maxVolume;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        initAll();

    }

    public void resetOnOffTimer(){

        JSONObject postData = new JSONObject();
        try {
            postData.put("ipaddress", SystemUtil.getLocalHostIp());

            new NetDataTool(this).sendNoShowPost(NetDataConstants.GET_ONOFF_TIME, postData.toString(), new NetDataTool.IResponse() {
                @Override
                public void onSuccess(String data) {

                    onOffTaskHelper.removeAllTimers();

                    try {

                        JSONArray jsonArray=new JSONArray(data);

                        for(int c=0;c<jsonArray.length();c++){

                            JSONObject jsonObject=jsonArray.getJSONObject(c);

                            TimerTaskHelper.TimeModel timeModel=new TimerTaskHelper.TimeModel();

                            //timeModel.startTime=jsonObject.getString("startTime");

                            timeModel.startTime="2000-1-1 12:00:00";

                            timeModel.endTime=jsonObject.getString("endTime");

                            setOnOffTimer(timeModel);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailed(String error) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    void initOnOffTimer(){

        onOffTaskHelper.startAndListener("onoff", new TimerTaskHelper.OnStartOrEndListener() {
            @Override
            public void onStartOrEnd(boolean startOrEnd, int index) {

                if(!startOrEnd){

                    try {

                        JSONObject jsonObject=new JSONObject();

                        jsonObject.put("kind","shutdown");

                        sendToUIBroadcast(jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

    }

    public void setOnOffTimer(TimerTaskHelper.TimeModel time){

        List<TimerTaskHelper.TimeModel> timeModels=new ArrayList<>();

        timeModels.add(time);

        onOffTaskHelper.setData(timeModels);

    }

    public void resetMaxVolumeTimer(){

        JSONObject postData = new JSONObject();

        try {
            postData.put("ipaddress", SystemUtil.getLocalHostIp());

            new NetDataTool(this).sendNoShowPost(NetDataConstants.GET_MAX_VOLUME,postData.toString(), new NetDataTool.IResponse() {
                @Override
                public void onSuccess(String data) {

                    maxVolumePercents.clear();

                    List<TimerTaskHelper.TimeModel> timeModels=new ArrayList<TimerTaskHelper.TimeModel>();

                    try {

                        JSONArray jsonArray=new JSONArray(data);

                        for(int c=0;c<jsonArray.length();c++){

                            JSONObject jsonObject=jsonArray.getJSONObject(c);

                            TimerTaskHelper.TimeModel timeModel=new TimerTaskHelper.TimeModel();

                            String percent=jsonObject.getString("percent");

                            maxVolumePercents.add(percent);

                            timeModel.startTime=jsonObject.getString("startTime");

                            timeModel.endTime=jsonObject.getString("endTime");

                            timeModels.add(timeModel);

                        }


                        maxVolumeTaskHelper.removeAllTimers();

                        maxVolumeTaskHelper.setData(timeModels);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(String error) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void initMaxVolumeTimer(){

        maxVolumeTaskHelper.startAndListener("maxvolume", new TimerTaskHelper.OnStartOrEndListener() {
            @Override
            public void onStartOrEnd(boolean startOrEnd, int index) {

                if(startOrEnd){

                    resetCurrentVolume(Double.parseDouble(maxVolumePercents.get(index)));

                    setMaxVolume(Double.parseDouble(maxVolumePercents.get(index)));

                }else{

                    setMaxVolume(-1);

                }

            }
        });

    }

    private void resetCurrentVolume(double percent){

        int volume= (int)(SystemUtil.getMaxVolume(this)*percent);

        if(SystemUtil.getCurrentVolume(this)>volume){

            SystemUtil.setCurrentVolume(volume,this);

        }

    }

    public void initPushService(){

        Intent intent=new Intent(this,PushService.class);

        ServiceConnection conn=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        this.bindService(intent,conn,Context.BIND_AUTO_CREATE);

    }

    BroadcastReceiver pushReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String data=intent.getStringExtra("data");

            try {
                JSONObject jsonObject=new JSONObject(data);

                String kind=jsonObject.getString("kind");

                switch(kind){
                    case "remind":
                        sendToUIBroadcast(jsonObject);
                        break;
                    case "playvideo":
                        sendToUIBroadcast(jsonObject);
                        break;
                    case "volumechange":
                        resetMaxVolumeTimer();
                        break;
                    case "onoff":
                        resetOnOffTimer();
                        break;
                    case "shutdown":
                        sendToUIBroadcast(jsonObject);
                        break;
                    case "msgchange":
                        sendToUIBroadcast(jsonObject);
                        break;
                    case "adchange":
                        sendToUIBroadcast(jsonObject);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private void sendToUIBroadcast(JSONObject data) {

        Intent intent=new Intent();

        intent.putExtra("data",data.toString());

        intent.setAction(BROAD_CAST_ACTION_UI);

        sendBroadcast(intent);

    }


    private void registerPushReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROAD_CAST_ACTION);
        registerReceiver(pushReceiver, filter);
    }

    private void unregisterPushReceiver(){
        unregisterReceiver(pushReceiver);
    }

    public static VLCApplication getInstance()
    {
        return instance;
    }

    public static Context getAppContext()
    {
        return instance;
    }

    public static Resources getAppResources()
    {
        return instance.getResources();
    }

}
