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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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

public class VLCApplication extends Application {

    private static VLCApplication instance;

    TimerTaskHelper onOffTaskHelper;

    TimerTaskHelper maxVolumeTaskHelper;

    MaxVolumeChangeListener maxVolumeListener;

    List<String> maxVolumePercents=new ArrayList<>();

    String patientNum;

    String patientPhoneNum;


    private void initAll(){

        CrashHandler.getInstance().init(this.getApplicationContext());

        ImageLoad.init(this);

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

    public interface MaxVolumeChangeListener{
        void onMaxVolumeChange();
    }

    public void setMaxVolumeListener(MaxVolumeChangeListener maxVolumeListener){
        this.maxVolumeListener=maxVolumeListener;
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

        onOffTaskHelper=new TimerTaskHelper(this);

        onOffTaskHelper.setStartInt(TimerTaskHelper.START_INT_1);

        maxVolumeTaskHelper=new TimerTaskHelper(this);

        maxVolumeTaskHelper.setStartInt(TimerTaskHelper.START_INT_2);

        instance = this;

        initAll();

    }

    public void resertOnOffTime(){

        JSONObject postData = new JSONObject();
        try {
            postData.put("ipaddress", SystemUtil.getLocalHostIp());

            new NetDataTool(this).sendNoShowPost(NetDataConstants.GET_ONOFF_TIME, postData.toString(), new NetDataTool.IResponse() {
                @Override
                public void onSuccess(String data) {

                    try {
                        JSONObject jsonObject=new JSONObject(data);

                        TimerTaskHelper.TimeModel timeModel=new TimerTaskHelper.TimeModel();

                        //timeModel.startTime=jsonObject.getString("startTime");

                        timeModel.startTime="2000-1-1 12:00:00";

                        timeModel.endTime=jsonObject.getString("endTime");

                        setOnOffTimer(timeModel);

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

    public void setOnOffTimer(TimerTaskHelper.TimeModel time){

        onOffTaskHelper.stopAndRemove();

        List<TimerTaskHelper.TimeModel> timeModels=new ArrayList<>();

        timeModels.add(time);

        onOffTaskHelper.setData(timeModels);

        onOffTaskHelper.startAndListener("onoff", new TimerTaskHelper.OnStartOrEndListener() {
            @Override
            public void onStartOrEnd(boolean startOrEnd, int index) {

                if(!startOrEnd){

                    SystemUtil.shutDown(VLCApplication.getAppContext());

                }

            }
        });

    }

    public void setMaxVolumeTimer(){

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


                        maxVolumeTaskHelper.stopAndRemove();

                        maxVolumeTaskHelper.setData(timeModels);

                        maxVolumeTaskHelper.startAndListener("maxvolume", new TimerTaskHelper.OnStartOrEndListener() {
                            @Override
                            public void onStartOrEnd(boolean startOrEnd, int index) {

                                if(startOrEnd){

                                    setMaxVolume(Double.parseDouble(maxVolumePercents.get(index)));

                                    if(maxVolumeListener!=null){
                                        maxVolumeListener.onMaxVolumeChange();
                                    }

                                }else{

                                    setMaxVolume(-1);

                                    if(maxVolumeListener!=null){
                                        maxVolumeListener.onMaxVolumeChange();
                                    }

                                }

                            }
                        });

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
