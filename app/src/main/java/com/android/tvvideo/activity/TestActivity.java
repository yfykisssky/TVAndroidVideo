package com.android.tvvideo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.tools.ShaPreHelper;
import com.android.tvvideo.video.TVPlayerActivity;


/**
 * Created by yfykisssky on 16/7/25.
 */
public class TestActivity extends BaseActivity {

    private void writeSettings() {

        ShaPreHelper.writeShaPre("settings","server_ip","http://192.168.7.1",this);

        ShaPreHelper.writeShaPre("settings","server_port","8080",this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        Context context=this;

        writeSettings();

        setContentView(R.layout.activity_test);
/*
        VLCApplication.getInstance().initPushService();*/
/*
        ComplainDialog complainDialog=new ComplainDialog(this);

        complainDialog.show();*/

      /*  new NetDataTool(this).sendGet(NetDataConstants.GET_SYS_TIME, new NetDataTool.IResponse() {

            @Override
            public void onSuccess(String data) {

                if(TextUtils.isEmpty(data)){
                    return;
                }

                VLCApplication.getInstance().getTimeTaskService().startTimeTask(data);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent=new Intent(TestActivity.this, FeeActivity.class);

                        startActivity(intent);

                    }
                },5000);


            }

            @Override
            public void onFailed(String error) {

                //Toast.makeText(StartActivity.this,error,Toast.LENGTH_LONG).show();

            }
        });*/





       // startActivity(new Intent(this,ComplainActivity.class));
        //((EditText)findViewById(R.id.url)).setText("rtmp://live.hkstv.hk.lxdns.com/live/hks");

        //VLCApplication.getInstance().initPushService();

        ((EditText)findViewById(R.id.url)).setText("rtsp://218.204.223.237:554/live/1/67A7572844E51A64/f68g2mj7wjua3la7.ts");

        //((EditText)findViewById(R.id.url)).setText("http://bbs.tvhuan.com/");

        ((Button)findViewById(R.id.ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data=((EditText)findViewById(R.id.url)).getText().toString();

                Intent intent=new Intent(TestActivity.this,TVPlayerActivity.class);

                intent.putExtra("data",data);

                startActivity(intent);

               // VideoPlayerActivity.start(TestActivity.this,uri);
            }
        });

    }
}
