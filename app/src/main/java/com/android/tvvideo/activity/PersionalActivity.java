package com.android.tvvideo.activity;

import android.content.Context;
import android.os.Bundle;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;

/**
 * Created by yfykisssky on 16/8/4.
 */
public class PersionalActivity extends BaseActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_complain_select);

        context=this;

        initView();

        getPersionalMsg();

    }

    private void getPersionalMsg() {


    }

    private void initView() {

    }

}


