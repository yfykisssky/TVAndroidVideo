package com.android.tvvideo.activity;

import android.app.Activity;
import android.os.Bundle;

import com.android.tvvideo.R;
import com.android.tvvideo.view.ValidateDialog;


/**
 * Created by yfykisssky on 16/7/25.
 */
public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        ValidateDialog v=new ValidateDialog(this);

        v.show();

    }
}
