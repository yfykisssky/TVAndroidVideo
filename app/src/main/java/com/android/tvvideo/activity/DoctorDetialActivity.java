package com.android.tvvideo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.tools.ImageLoad;



/**
 * Created by yangfengyuan on 16/7/18.
 */
public class DoctorDetialActivity extends BaseActivity {

    Context context;

    ImageView headImg;

    TextView nameTex;
    TextView postTex;
    TextView goodatTex;
    TextView timeTex;
    TextView detialTex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_doctor_detial);

        initView();

        context=this;

    }

    private void initView() {

        headImg=(ImageView)findViewById(R.id.img);

        nameTex=(TextView)findViewById(R.id.name);
        postTex=(TextView)findViewById(R.id.post);
        goodatTex=(TextView)findViewById(R.id.goodat);
        timeTex=(TextView)findViewById(R.id.time);
        detialTex=(TextView)findViewById(R.id.detial);

        Intent intent=getIntent();

        String name=intent.getStringExtra("name");
        String post=intent.getStringExtra("post");
        String goodat=intent.getStringExtra("goodat");
        String time=intent.getStringExtra("time");
        String detial=intent.getStringExtra("detial");

        String path=intent.getStringExtra("path");

        nameTex.setText(name);
        postTex.setText(post);
        goodatTex.setText(goodat);
        timeTex.setText(time);
        detialTex.setText(detial);

        ImageLoad.loadDefultImage(path,headImg);


    }

}
