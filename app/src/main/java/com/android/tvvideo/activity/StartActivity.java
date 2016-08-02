package com.android.tvvideo.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.tools.CrashHandler;
import com.android.tvvideo.tools.ImageLoad;
import com.android.tvvideo.tools.ShaPreHelper;
import com.android.tvvideo.tools.UpdateHelpter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangfengyuan on 16/7/18.
 */
public class StartActivity extends BaseActivity {

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

    private void initView() {

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

        ImageLoad.init(this);

        if(serverIp==null||serverPort==null){

            readSettings();

        }

        if(TextUtils.isEmpty(serverIp)|| TextUtils.isEmpty(serverPort)){

            Intent intent=new Intent(this,SettingsActivity.class);

            startActivityForResult(intent,settingsIntent);

        }else{

            CrashHandler.getInstance().init(this.getApplicationContext());

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

        if(requestCode==settingsIntent){
            initData();
        }

        if(resultCode==0x11){
            serverIp=data.getStringExtra("server_ip");
            serverPort=data.getStringExtra("server_port");
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(String error) {

                showToast(error);

            }
        });

    }

    private void playSound(String url){
        MediaPlayer player = MediaPlayer.create(this, Uri.parse(url));
        player.setLooping(false);//设置不循环播放
        player.start();
    }

}