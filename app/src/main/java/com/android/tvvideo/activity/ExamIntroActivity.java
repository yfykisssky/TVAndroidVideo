package com.android.tvvideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.tools.ImageLoad;
import com.android.tvvideo.tools.SystemUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yfykisssky on 16/7/16.
 */
public class ExamIntroActivity extends BaseActivity {

    Button scanDocoors;

    Button scanNurses;

    ImageView img;

    TextView detial;

    TextView bottom;

    String examRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_intro);

        initView();

        getDataFromNet();

    }

    private void initView() {

        scanDocoors=(Button)findViewById(R.id.scanDoc);

        scanDocoors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(ExamIntroActivity.this,DoctorsSelectActivity.class);

                intent.putExtra("exam_room_id",examRoomId);

                startActivity(intent);

            }
        });

        scanDocoors.requestFocus();

        scanNurses=(Button)findViewById(R.id.scanNur);

        scanNurses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(ExamIntroActivity.this,NursesSelectActivity.class);

                intent.putExtra("exam_room_id",examRoomId);

                startActivity(intent);

            }
        });


        img=(ImageView)findViewById(R.id.img);

        detial=(TextView)findViewById(R.id.detial);

        bottom=(TextView)findViewById(R.id.detial_bottom);

    }

    private void getDataFromNet() {

        JSONObject postData=new JSONObject();
        try {
            postData.put("ipaddress", SystemUtil.getLocalHostIp());

            new NetDataTool(this).sendPost(NetDataConstants.GET_EXAMROOM_INFO,postData.toString(), new NetDataTool.IResponse() {
                @Override
                public void onSuccess(String data) {

                    try {

                        JSONObject jsonObject=new JSONObject(data);

                        examRoomId= String.valueOf(jsonObject.getInt("id"));

                        String remark=jsonObject.getString("remark");
                        String path = jsonObject.getString("path");
                        String bottomremark=jsonObject.getString("bottomremark");

                        detial.setText(remark);

                        if(bottomremark!=null){
                            bottom.setText(bottomremark);
                        }

                        ImageLoad.loadDefultImage(path,img);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailed(String error) {
                    showToast(error);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
