package com.android.tvvideo.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import com.android.tvvideo.tools.PushService;
import com.android.tvvideo.tools.SystemUtil;
import com.android.tvvideo.video.VideoPlayerActivity;
import com.android.tvvideo.view.RemindDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.VLCApplication;

import java.util.List;

/**
 * Created by yangfengyuan on 16/7/22.
 */
public class BaseActivity extends Activity {

    AudioManager audio;

    int maxVolume;

    double maxVolumePercent=-1;

    String activityName;

    PushMsgListener pushMsgListener;

    RemindDialog remindDialog;

    public interface PushMsgListener{
        void onMsgReceive(Intent data);
    }

    protected void setOnPushMsgListener(PushMsgListener pushMsgListener){
        this.pushMsgListener=pushMsgListener;
    }

    protected void setActivityName(String activityName){
        this.activityName=activityName;
    }


    Handler baseHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case 100:
                    Toast.makeText(BaseActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    BroadcastReceiver pushReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String data=intent.getStringExtra("data");

            try {
                JSONObject jsonObject=new JSONObject(data);

                String kind=jsonObject.getString("kind");

                switch(kind){
                    case "remind":
                        if(isTopActivity(context,activityName)){
                            showRemindDialog(jsonObject.getString("data"));
                        }
                        break;
                    case "playvideo":
                        playVideo(jsonObject.getString("data"));
                        break;
                    case "volumechange":
                        resetVolumePercent();
                        break;
                    case "onoff":
                        resetOnOffTime();
                        break;
                    case "shutdown":
                        SystemUtil.shutDown(BaseActivity.this);
                        break;
                    case "msgchange":
                    case "adchange":
                        if(pushMsgListener!=null){
                            pushMsgListener.onMsgReceive(intent);
                        }
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private void resetVolumePercent() {

        VLCApplication vlcApplication= (VLCApplication) getApplication();

        vlcApplication.setMaxVolumeTimer();

    }

    private void resetOnOffTime() {

        VLCApplication.getInstance().resertOnOffTime();

    }

    private void registerPushReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(PushService.BROAD_CAST_ACTION);
        registerReceiver(pushReceiver, filter);
    }

    private void unregisterPushReceiver(){
        unregisterReceiver(pushReceiver);
    }

    private void playVideo(String url){

        Intent intent=new Intent(BaseActivity.this,VideoPlayerActivity.class);

        intent.putExtra("data",url);

        startActivity(intent);

    }

    private void showRemindDialog(String remind){

        if(remindDialog.isShowing()){
            remindDialog.dismiss();
        }

        remindDialog.setData(remind);

        remindDialog.show();

    }

    protected void showToast(String msg){

        Message message=new Message();

        message.what=100;

        message.obj=msg;

        baseHandler.sendMessage(message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

    }

    private void initData() {

        remindDialog=new RemindDialog(this);

        registerPushReceiver();

        audio = (AudioManager) getSystemService(Service.AUDIO_SERVICE);

        maxVolume=getMaxVolume();

        maxVolumePercent=VLCApplication.getInstance().getMaxVolume();

        resetCurrentVolume(maxVolumePercent);

        VLCApplication vlcApplication= (VLCApplication) getApplication();

        vlcApplication.setMaxVolumeListener(new VLCApplication.MaxVolumeChangeListener() {

            @Override
            public void onMaxVolumeChange() {

                maxVolumePercent=VLCApplication.getInstance().getMaxVolume();

                resetCurrentVolume(maxVolumePercent);

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterPushReceiver();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:

                if(maxVolumePercent<0){
                    return false;
                }

                if (getCurrentVolume() < (maxVolume*maxVolumePercent)) {
                    audio.adjustStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE,
                            AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);

                }

                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audio.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);

                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void resetCurrentVolume(double percent){

        int volume= (int)(maxVolume*percent);

        if(getCurrentVolume()>volume){

            setCurrentVolume(volume);

        }

    }

    private void setCurrentVolume(int tempVolume){
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, tempVolume, 0);
    }

    protected int getMaxVolume(){
        return audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    protected int getCurrentVolume(){
        return audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    private static boolean isTopActivity(Context context, String className)
    {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }

}
