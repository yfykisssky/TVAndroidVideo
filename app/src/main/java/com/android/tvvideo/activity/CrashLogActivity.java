package com.android.tvvideo.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;


/**
 * Created by yangfengyuan on 16/7/22.
 */
public class CrashLogActivity extends BaseActivity implements View.OnClickListener{

    private TextView logText;

    private Button exitBnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_log);

        initView();

        initData();

    }

    private void initData() {
        SharedPreferences sharedPreferences=getSharedPreferences("log", Activity.MODE_PRIVATE);
        String data =sharedPreferences.getString("error","");
        String date =sharedPreferences.getString("time","");

        if(!data.equals("")){

            if(date.equals("")){
                date="unkonwn time";
            }

            logText.setText(date+"\n"+data);

        }

    }

    private void initView() {

        logText=(TextView)findViewById(R.id.log);

        exitBnt=(Button)findViewById(R.id.exit);

        exitBnt.setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.exit:
                finish();
                break;
        }
    }

}

