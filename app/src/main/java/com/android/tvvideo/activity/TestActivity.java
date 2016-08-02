package com.android.tvvideo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.tools.TextViewUtil;



/**
 * Created by yfykisssky on 16/7/25.
 */
public class TestActivity extends Activity {

    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

       /* ImageView imgView=(ImageView)findViewById(R.id.img);

        ImageLoad.init(this);

        ImageLoad.loadDefultImage("http://nonobank.iask.in/app/img/hospital.jpg",imgView);*/

        /*Intent intent=new Intent(this,InHospitalActivity.class);

        startActivity(intent);*/

        TextView texR=(TextView)findViewById(R.id.detial);

        TextView texB=(TextView)findViewById(R.id.detial_bottom);

        //texR.setText(text);

        new TextViewUtil().setText(text,texR,texB);

    }
}
