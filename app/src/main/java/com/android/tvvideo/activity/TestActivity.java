package com.android.tvvideo.activity;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.tvvideo.R;
import com.android.tvvideo.tools.ShaPreHelper;

import org.videolan.vlc.video.VideoPlayerActivity;


/**
 * Created by yfykisssky on 16/7/25.
 */
public class TestActivity extends Activity {

    private void writeSettings() {

        ShaPreHelper.writeShaPre("settings","server_ip","http://192.168.7.1",this);

        ShaPreHelper.writeShaPre("settings","server_port","8080",this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context=this;

        writeSettings();

        setContentView(R.layout.activity_test);

   /*     ValidateDialog validateDialog=new ValidateDialog(this);

        validateDialog.setValidateListener(new ValidateDialog.ValidateListener() {
            @Override
            public void validate(boolean result) {
                Log.e("ok",String.valueOf(result));
            }
        });

        validateDialog.show();*/

        //startActivity(new Intent(this,OrderMealActivity.class));
        //((EditText)findViewById(R.id.url)).setText("rtsp://218.204.223.237:554/live/1/67A7572844E51A64/f68g2mj7wjua3la7.sdp");

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
