package com.android.tvvideo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.android.tvvideo.R;
import com.android.tvvideo.view.ShutDownDialog;


/**
 * Created by yfykisssky on 16/7/25.
 */
public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context=this;

        setContentView(R.layout.activity_test);

        ShutDownDialog shutDownDialog=new ShutDownDialog(this);

        shutDownDialog.setData(10);

        shutDownDialog.show();
/*
        //startActivity(new Intent(this,InHospitalActivity.class));
        ((EditText)findViewById(R.id.url)).setText("rtsp://218.204.223.237:554/live/1/67A7572844E51A64/f68g2mj7wjua3la7.sdp");

        ((Button)findViewById(R.id.ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data=((EditText)findViewById(R.id.url)).getText().toString();

                Uri uri= Uri.parse(data);

                VideoPlayerActivity.start(TestActivity.this,uri);
            }
        });*/

    }
}
