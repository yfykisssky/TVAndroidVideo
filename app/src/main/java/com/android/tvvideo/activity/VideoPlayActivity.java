package com.android.tvvideo.activity;

import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;



/**
 * Created by yangfengyuan on 16/7/18.
 */
public class VideoPlayActivity extends BaseActivity {

    SurfaceView surfaceView;

    String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        initData();

        initView();

    }

    private void initView() {

        surfaceView= (SurfaceView) findViewById(R.id.surfaceview);

        SurfaceHolder holder=surfaceView.getHolder();

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });

    }

    private void initData() {

        videoUrl=getIntent().getStringExtra("video_url");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
