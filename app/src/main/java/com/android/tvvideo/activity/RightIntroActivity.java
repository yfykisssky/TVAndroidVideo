package com.android.tvvideo.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.tools.ImageLoad;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yfykisssky on 16/7/27.
 */
public class RightIntroActivity extends BaseActivity {

    Context context;

    ImageView img;

    TextView detial;
    private TextView bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_right_detial);

        initView();

        getDataFromNet();

        context=this;

    }

    private void getDataFromNet() {

        new NetDataTool(this).sendGet(NetDataConstants.GET_INFO+ NetDataConstants.INFO_EUME.RIGHT_INFO, new NetDataTool.IResponse() {
            @Override
            public void onSuccess(String data) {

                try {

                    JSONObject jsonObject=new JSONObject(data);

                    String remark=jsonObject.getString("remark");
                    String path=jsonObject.getString("path");
                    String bottomremark=jsonObject.getString("bottomRemark");

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


    }

    private void initView() {

        img=(ImageView)findViewById(R.id.img);

        detial=(TextView)findViewById(R.id.detial);

        bottom=(TextView)findViewById(R.id.detial_bottom);

    }

}


