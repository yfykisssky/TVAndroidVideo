package com.android.tvvideo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.tvvideo.R;

import org.videolan.vlc.gui.video.VideoPlayerActivity;


/**
 * Created by yfykisssky on 16/7/25.
 */
public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context=this;

        setContentView(R.layout.activity_test);

        startActivity(new Intent(this,InHospitalActivity.class));

        ((Button)findViewById(R.id.ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data=((EditText)findViewById(R.id.url)).getText().toString();

                Uri uri= Uri.parse(data);

                VideoPlayerActivity.start(TestActivity.this,uri);
            }
        });

    }
}
