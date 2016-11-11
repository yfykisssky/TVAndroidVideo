package com.android.tvvideo.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.tvvideo.R;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.tools.ShaPreHelper;
import com.android.tvvideo.tools.UpdateHelpter;
import com.android.tvvideo.tools.ViewTool;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by yangfengyuan on 16/7/18.
 */
public class StartActivity extends Activity {

    int settingsIntent=0x11;
    private String serverIp;
    private String serverPort;

    Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        initView();

        initData();


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initView() {

        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.start_bg);

        relativeLayout.setBackground(ViewTool.getDrawble(this,R.drawable.start_bg));

        settingsButton=(Button) findViewById(R.id.settings);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(StartActivity.this,SettingsActivity.class);

                startActivityForResult(intent,settingsIntent);
            }
        });

    }

    private void initData() {

        if(serverIp==null||serverPort==null){

            readSettings();

        }

        if(TextUtils.isEmpty(serverIp)|| TextUtils.isEmpty(serverPort)){

            Intent intent=new Intent(this,SettingsActivity.class);

            startActivityForResult(intent,settingsIntent);

        }else{

            checkUpdate();

        }


    }

    private void readSettings() {

        serverIp= ShaPreHelper.readShaPre("settings","server_ip",this);

        serverPort= ShaPreHelper.readShaPre("settings","server_port",this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==0x11){
            serverIp=data.getStringExtra("server_ip");
            serverPort=data.getStringExtra("server_port");
        }

        if(requestCode==settingsIntent){
            initData();
        }

    }

    private void checkUpdate() {

        new NetDataTool(this).sendGet(NetDataConstants.GET_VERSION, new NetDataTool.IResponse() {

            @Override
            public void onSuccess(String data) {

                try {
                    JSONObject jsonObject=new JSONObject(data);

                    String version=jsonObject.getString("version");
                    String url=jsonObject.getString("address");

                    if(!new UpdateHelpter().checkUpdate(StartActivity.this,version,url,"TeachVideo.apk")){

                        Toast.makeText(StartActivity.this,"已经是最新版本",Toast.LENGTH_LONG).show();

                        toHome();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(String error) {

                Toast.makeText(StartActivity.this,error,Toast.LENGTH_LONG).show();

            }
        });

    }

    private void getSystemTime(){

        new NetDataTool(this).sendGet(NetDataConstants.GET_SYS_TIME, new NetDataTool.IResponse() {

            @Override
            public void onSuccess(String data) {

            }

            @Override
            public void onFailed(String error) {

                Toast.makeText(StartActivity.this,error,Toast.LENGTH_LONG).show();

            }
        });

    }

    private void toHome(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent intent=new Intent(StartActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();

            }
        }).start();

    }

    private void playSound(String url){
        MediaPlayer player = MediaPlayer.create(this, Uri.parse(url));
        player.setLooping(false);//设置不循环播放
        player.start();
    }

}