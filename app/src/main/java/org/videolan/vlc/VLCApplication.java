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
package org.videolan.vlc;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.tools.SystemUtil;
import com.android.tvvideo.tools.TimerTaskHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.vlc.util.Strings;

import java.util.ArrayList;
import java.util.List;

public class VLCApplication extends Application {

    private static VLCApplication instance;

    public final static String SLEEP_INTENT = Strings.buildPkgString("SleepIntent");

    TimerTaskHelper onOffTaskHelper;

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

        instance = this;

    }

    public void getOnOffTime(){

        JSONObject postData = new JSONObject();
        try {
            postData.put("ipaddress", SystemUtil.getLocalHostIp());

            new NetDataTool(this).sendPost(NetDataConstants.GET_ONOFF_TIME, postData.toString(), new NetDataTool.IResponse() {
                @Override
                public void onSuccess(String data) {

                    try {
                        JSONObject jsonObject=new JSONObject(data);

                        TimerTaskHelper.TimeModel timeModel=new TimerTaskHelper.TimeModel();

                        timeModel.startTime=jsonObject.getString("startTime");

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

                }

            }
        });

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
