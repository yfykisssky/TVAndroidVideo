package com.android.tvvideo.base;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

import com.android.tvvideo.tools.SystemUtil;
import com.android.tvvideo.video.VideoPlayerActivity;
import com.android.tvvideo.view.RemindDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.VLCApplication;

/**
 * Created by yangfengyuan on 16/7/22.
 */
public class BaseActivity extends Activity {

    public static final String BROAD_CAST_ACTION_UI="com.teachvideo.msg.push.ui";

    AudioManager audio;

    double maxVolumePercent=-1;

    String activityName;

    PushMsgListener pushMsgListener;

    RemindDialog remindDialog;

    public interface PushMsgListener{
        void onMsgReceive(JSONObject jsonObject);
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
                        if(SystemUtil.isTopActivity(BaseActivity.this,activityName)){
                            showRemindDialog(jsonObject.getString("data"));
                        }
                        break;
                    case "playvideo":

                        if((!activityName.equals("TVPlayerActivity"))&&(!activityName.equals("VideoPlayerActivity"))){
                            playVideo(jsonObject.getString("data"));
                        }

                        break;
                    case "shutdown":
                        if(SystemUtil.isTopActivity(BaseActivity.this,activityName)) {
                            SystemUtil.shutDown(BaseActivity.this);
                        }
                        break;
                    case "msgchange":
                    case "adchange":
                        if(pushMsgListener!=null){
                            pushMsgListener.onMsgReceive(jsonObject);
                        }
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private void playVideo(String url){

        Intent intent=new Intent(this,VideoPlayerActivity.class);

        intent.putExtra("data",url);

        startActivity(intent);

    }

    private void registerPushReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROAD_CAST_ACTION_UI);
        registerReceiver(pushReceiver, filter);
    }

    private void unregisterPushReceiver(){
        unregisterReceiver(pushReceiver);
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

        audio = (AudioManager) getSystemService(Service.AUDIO_SERVICE);

        registerPushReceiver();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterPushReceiver();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        maxVolumePercent=VLCApplication.getInstance().getMaxVolume();

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_VOLUME_UP:

                if(maxVolumePercent<0){
                    return false;
                }

                if (SystemUtil.getCurrentVolume(VLCApplication.getInstance().getAudio()) < (SystemUtil.getMaxVolume(this)*maxVolumePercent)) {
                    return false;
                }

                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:

                return false;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

}
