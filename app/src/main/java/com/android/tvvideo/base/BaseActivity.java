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

import com.android.tvvideo.activity.SettingsActivity;
import com.android.tvvideo.tools.PushService;
import com.android.tvvideo.view.RemindDialog;

import java.util.List;

/**
 * Created by yangfengyuan on 16/7/22.
 */
public class BaseActivity extends Activity {

    AudioManager audio;

    int maxVolume;

    String activityName;

    final String toSettingCode="1234567890";

    String toSettingCodeTmp="";

    PushMsgListener pushMsgListener;

    public interface PushMsgListener{
       void onMsgReceive(Intent data);
    }

    protected void setOnPushMsgListener(PushMsgListener pushMsgListener){
        this.pushMsgListener=pushMsgListener;
    }

    protected void setActivityName(String activityName){
        this.activityName=activityName;
    }

    private void validateSettingCode(char code){
        toSettingCodeTmp+=code;

        if(toSettingCodeTmp.indexOf(toSettingCode)!=-1){

            Intent intent=new Intent(this, SettingsActivity.class);

            startActivity(intent);

            toSettingCodeTmp="";

        }
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

            String kind=intent.getStringExtra("kind");

            pushMsgListener.onMsgReceive(intent);

            switch(kind){
                case "remind":
                    if(isTopActivity(context,activityName)){
                        showRemindDialog("");
                    }
                    break;
                case "playvideo":
                    playVideo("");
                    break;
                case "volume":
                    break;
                case "onoff":
                    break;
                case "":
                    break;
            }

        }
    };

    private void registerPushReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(PushService.BROAD_CAST_ACTION);
        registerReceiver(pushReceiver, filter);
    }

    private void unregisterPushReceiver(){
        unregisterReceiver(pushReceiver);
    }

    private void playVideo(String url){

    }

    private void showRemindDialog(String remind){

        RemindDialog remindDialog=new RemindDialog(this);

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

        registerPushReceiver();

        audio = (AudioManager) getSystemService(Service.AUDIO_SERVICE);

        //maxVolume=getMaxVolume();

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

                if(maxVolume>0) {

                    if (getCurrentVolume() < maxVolume) {
                        audio.adjustStreamVolume(
                                AudioManager.STREAM_MUSIC,
                                AudioManager.ADJUST_RAISE,
                                AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);

                    }

                }

                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audio.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);

                return true;
            case KeyEvent.KEYCODE_0:
                validateSettingCode('0');
                break;
            case KeyEvent.KEYCODE_1:
                validateSettingCode('1');
                break;
            case KeyEvent.KEYCODE_2:
                validateSettingCode('2');
                break;
            case KeyEvent.KEYCODE_3:
                validateSettingCode('3');
                break;
            case KeyEvent.KEYCODE_4:
                validateSettingCode('4');
                break;
            case KeyEvent.KEYCODE_5:
                validateSettingCode('5');
                break;
            case KeyEvent.KEYCODE_6:
                validateSettingCode('6');
                break;
            case KeyEvent.KEYCODE_7:
                validateSettingCode('7');
                break;
            case KeyEvent.KEYCODE_8:
                validateSettingCode('8');
                break;
            case KeyEvent.KEYCODE_9:
                validateSettingCode('9');
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected int getMaxVolume(){
        return audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    protected int getCurrentVolume(){
        return audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
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
