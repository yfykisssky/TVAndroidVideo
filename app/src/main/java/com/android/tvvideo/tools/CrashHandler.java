package com.android.tvvideo.tools;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.tvvideo.activity.StartActivity;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;

public class CrashHandler implements UncaughtExceptionHandler {

    private static CrashHandler INSTANCE = new CrashHandler();
    private Context mContext;
    private UncaughtExceptionHandler mDefaultHandler;

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    private void updateLog(){

        SharedPreferences sharedPreferences= mContext.getSharedPreferences("log", Activity.MODE_PRIVATE);
        String data =sharedPreferences.getString("error","");
        String date =sharedPreferences.getString("time","");

        if(!data.equals("")){

            if(date.equals("")){
                date="unkonwn time";
            }

            JSONObject jsonObject=new JSONObject();

            try {
                jsonObject.put("time",date);
                jsonObject.put("content",data);

                new NetDataTool(mContext).sendNoShowPost(NetDataConstants.APP_INFO, jsonObject.toString(), new NetDataTool.IResponse() {
                    @Override
                    public void onSuccess(String data) {
                        clearLog();
                    }

                    @Override
                    public void onFailed(String error) {

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void saveLog(String dump){

        SharedPreferences mySharedPreferences= mContext.getSharedPreferences("log", Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = mySharedPreferences.edit();

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());

        editor.putString("error",dump);
        editor.putString("time",date);

        editor.commit();
    }

    private void clearLog(){

        SharedPreferences sharedPreferences= mContext.getSharedPreferences("log", Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor =sharedPreferences.edit();

        editor.remove("error");
        editor.remove("time");

        editor.commit();
    }

    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

        updateLog();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        if(handleException(ex)){

            restartApp(mContext);

        }else{

            mDefaultHandler.uncaughtException(thread, ex);

        }

    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);
        String dump = info.toString();

        saveLog(dump);

        return true;
    }

    private void restartApp(Context context){
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context,StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("crash", true);
        PendingIntent restartIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis(), restartIntent); // 1秒钟后重启应用

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
        System.gc();
    }
}
