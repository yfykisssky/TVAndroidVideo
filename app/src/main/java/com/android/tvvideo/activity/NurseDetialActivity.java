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
public class NurseDetialActivity extends BaseActivity {

    Context context;

    ImageView headImg;

    TextView nameTex;
    TextView postTex;
    TextView detialTex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse_detial);

        initView();

        context=this;

    }

    private void initView() {

        headImg=(ImageView)findViewById(R.id.img);

        nameTex=(TextView)findViewById(R.id.name);
        postTex=(TextView)findViewById(R.id.post);
        detialTex=(TextView)findViewById(R.id.detial);

        Intent intent=getIntent();

        String name=intent.getStringExtra("name");
        String post=intent.getStringExtra("post");
        String detial=intent.getStringExtra("detial");

        String path=intent.getStringExtra("path");

        nameTex.setText(name);
        postTex.setText(post);
        detialTex.setText(detial);

        ImageLoad.loadDefultImage(path,headImg);


    }

}
