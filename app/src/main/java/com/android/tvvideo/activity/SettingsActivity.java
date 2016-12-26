package com.android.tvvideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.tools.CheckTool;
import com.android.tvvideo.tools.ShaPreHelper;
import com.android.tvvideo.tools.SystemUtil;
import com.android.tvvideo.view.SettingPswdDialog;


/**
 * Created by yangfengyuan on 16/7/22.
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener{

    private TextView ipEdit;

    private TextView macEdit;

    private EditText serverIpEdit;

    private String serverIp;

    private EditText serverPortEdit;

    private String serverPort;

    private Button logBnt;

    private Button settingsTvBnt;

    private Button confirmBnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_settings);

        initData();

        initView();

    }

    private void initData() {

        final SettingPswdDialog settingPswdDialog=new SettingPswdDialog(this);

        settingPswdDialog.setConfirmOrCancelListener(new SettingPswdDialog.ConfirmOrCancelListener() {
            @Override
            public void confirmOrCancel(boolean b) {

                if(b){
                    settingPswdDialog.dismiss();
                }else{
                    settingPswdDialog.dismiss();
                    SettingsActivity.this.finish();
                }

            }
        });

        settingPswdDialog.show();

        readSettings();

    }

    private void initView() {

        ipEdit=(TextView)findViewById(R.id.ipaddress);

        ipEdit.setText(SystemUtil.getLocalHostIp());

        macEdit=(TextView)findViewById(R.id.macddress);

        macEdit.setText(SystemUtil.getLocalMac(this));

        serverIpEdit=(EditText)findViewById(R.id.serverip);

        SystemUtil.setHideKeyBoard(this,serverIpEdit);

        serverIpEdit.requestFocus();

        if(serverIp!=null&&serverIp.length()>0){

            serverIp=serverIp.substring(7,serverIp.length());

            serverIpEdit.setText(serverIp);
        }

        serverPortEdit=(EditText)findViewById(R.id.serverport);

        if(serverPort!=null&&serverPort.length()>0){
            serverPortEdit.setText(serverPort);
        }

        SystemUtil.setHideKeyBoard(this,serverPortEdit);

        settingsTvBnt=(Button)findViewById(R.id.tvsettings);

        settingsTvBnt.setOnClickListener(this);

        confirmBnt=(Button)findViewById(R.id.confirm);

        confirmBnt.setOnClickListener(this);

        logBnt=(Button)findViewById(R.id.log);

        logBnt.setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.confirm:

                serverIp=serverIpEdit.getText().toString();

                serverPort=serverPortEdit.getText().toString();

                if(!CheckTool.isIP(serverIp)){

                    Toast.makeText(this,"IP地址错误！", Toast.LENGTH_SHORT).show();

                    return;

                }

                if(serverPort.equals("")){

                    Toast.makeText(this,"端口错误！", Toast.LENGTH_SHORT).show();

                    return;

                }

                serverIp="http://"+serverIp;

                writeSettings();

                Intent intent=new Intent();

                intent.putExtra("server_ip",serverIp);

                intent.putExtra("server_port",serverPort);

                setResult(0x11,intent);

                finish();

                break;
            case R.id.tvsettings:

                startActivity(new Intent(Settings.ACTION_SETTINGS));

                break;

            case R.id.log:

                startActivity(new Intent(this,TestMoreActivity.class));

                break;

        }
    }


    private void readSettings() {


        serverIp= ShaPreHelper.readShaPre("settings","server_ip",this);

        serverPort= ShaPreHelper.readShaPre("settings","server_port",this);


    }

    private void writeSettings() {

        ShaPreHelper.writeShaPre("settings","server_ip",serverIp,this);

        ShaPreHelper.writeShaPre("settings","server_port",serverPort,this);

    }

}

