package com.android.tvvideo.base;

import android.app.Activity;
import android.app.Service;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by yangfengyuan on 16/7/22.
 */
public class BaseActivity extends Activity {

    AudioManager audio;

    int maxVolume;

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

    protected void showToast(String msg){

        Message message=new Message();

        message.what=100;

        message.obj=msg;

        baseHandler.sendMessage(message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        audio = (AudioManager) getSystemService(Service.AUDIO_SERVICE);

        //maxVolume=getMaxVolume();

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
                break;
            case KeyEvent.KEYCODE_1:
                break;
            case KeyEvent.KEYCODE_2:
                break;
            case KeyEvent.KEYCODE_3:
                break;
            case KeyEvent.KEYCODE_4:
                break;
            case KeyEvent.KEYCODE_5:
                break;
            case KeyEvent.KEYCODE_6:
                break;
            case KeyEvent.KEYCODE_7:
                break;
            case KeyEvent.KEYCODE_8:
                break;
            case KeyEvent.KEYCODE_9:
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

}
