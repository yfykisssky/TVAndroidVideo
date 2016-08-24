package com.android.tvvideo.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.view.TimeEditDialog;

/**
 * Created by yfykisssky on 16/8/4.
 */
public class FeeActivity extends BaseActivity {

    Context context;

    Button startTimeBnt;

    Button endTimeBnt;

    String startTime;

    String endTime;

    Button searchBnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_fee);

        context=this;

        initView();

        getFee();

    }

    private void getFee() {


    }

    private void initView() {

        startTimeBnt=(Button)findViewById(R.id.startTiem);

        startTimeBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimeEditDialog timeEditDialog=new TimeEditDialog(FeeActivity.this);

                timeEditDialog.setTimeChangeListener(new TimeEditDialog.TimeChangeListener() {
                    @Override
                    public void change(String date) {
                        startTime=date;
                    }
                });

                timeEditDialog.show();

            }
        });

        endTimeBnt=(Button)findViewById(R.id.startTiem);

        endTimeBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimeEditDialog timeEditDialog=new TimeEditDialog(FeeActivity.this);

                timeEditDialog.setTimeChangeListener(new TimeEditDialog.TimeChangeListener() {
                    @Override
                    public void change(String date) {
                        endTime=date;
                    }
                });

                timeEditDialog.show();

            }
        });

        searchBnt=(Button)findViewById(R.id.search);

        searchBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(startTime)||TextUtils.isEmpty(endTime)){
                    showToast("时间不能为空");
                }else{
                    searchData();
                }

            }
        });

    }

    private void searchData() {



    }

}
