package org.videolan.vlc;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.tvvideo.R;

import org.videolan.vlc.gui.video.VideoPlayerActivity;

/**
 * Created by yangfengyuan on 16/8/3.
 */
public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

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