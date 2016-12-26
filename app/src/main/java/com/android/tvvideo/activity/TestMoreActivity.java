package com.android.tvvideo.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.tools.SystemUtil;

import org.videolan.libvlc.VLCApplication;

/**
 * Created by yangfengyuan on 2016/12/13.
 */

public class TestMoreActivity extends BaseActivity{

    TextView log;

    String logStr=new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_test_more);

        log=(TextView)findViewById(R.id.log);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        int voll=SystemUtil.getCurrentVolume(VLCApplication.getInstance().getAudio());

        String volTex="音量"+String.valueOf(voll);

        logStr+="按钮"+String.valueOf(keyCode)+volTex+"\n";

        log.setText(logStr);

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_VOLUME_UP:

                int volume=SystemUtil.getCurrentVolume(VLCApplication.getInstance().getAudio());

                String logTex="音量加"+String.valueOf(volume)+"\n";

                logStr+=logTex;

                log.setText(logStr);

                return false;
            case KeyEvent.KEYCODE_VOLUME_DOWN:

                int vol=SystemUtil.getCurrentVolume(VLCApplication.getInstance().getAudio());

                String logT="音量加"+String.valueOf(vol)+"\n";

                logStr+=logT;

                log.setText(logStr);

                return false;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

}
