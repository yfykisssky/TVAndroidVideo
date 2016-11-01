package com.android.tvvideo.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.tools.SystemUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.VLCApplication;


/**
 * Created by yfykisssky on 16/8/4.
 */
public class PersionalActivity extends BaseActivity {

    Context context;

    TextView inHospitalNum;

    TextView bedNum;

    TextView name;

    TextView sex;

    TextView age;

    TextView area;

    TextView fee;

    TextView inTime;

    TextView liveTime;

    TextView level;

    TextView doctor;

    TextView nurse;

    TextView deposit;

    TextView allFee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_persional);

        context=this;

        initView();

        getPersionalMsg();

    }

    private void getPersionalMsg() {

        JSONObject postData=new JSONObject();
        try {
            postData.put("ipaddress", SystemUtil.getLocalHostIp());
            postData.put("hospitalId", VLCApplication.getInstance().getPatientNum());
            postData.put("mobile",VLCApplication.getInstance().getPatientPhoneNum());

            new NetDataTool(this).sendPost(NetDataConstants.GET_PERSIONAL,postData.toString(),new NetDataTool.IResponse() {
                @Override
                public void onSuccess(String data) {

                    if(TextUtils.isEmpty(data)){

                        showToast("获取数据失败");

                        return;

                    }

                    try {

                        JSONObject jsonObject=new JSONObject(data);

                        inHospitalNum.setText(jsonObject.getString("inhospitalid"));

                        bedNum.setText(jsonObject.getString("bedid"));

                        name.setText(jsonObject.getString("name"));

                        sex.setText(jsonObject.getString("sex"));

                        age.setText(jsonObject.getString("age"));

                        area.setText(jsonObject.getString("area"));

                        fee.setText(jsonObject.getString("fee"));

                        inTime.setText(jsonObject.getString("intime"));

                        liveTime.setText(jsonObject.getString("livetime"));

                        level.setText(jsonObject.getString("level"));

                        doctor.setText(jsonObject.getString("doctor"));

                        nurse.setText(jsonObject.getString("nurse"));

                        deposit.setText(jsonObject.getString("deposit"));

                        allFee.setText(jsonObject.getString("allfee"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailed(String error) {

                    Toast.makeText(context,error,Toast.LENGTH_SHORT).show();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    private void initView() {

        inHospitalNum=(TextView)findViewById(R.id.inhospital);

        bedNum=(TextView)findViewById(R.id.bed);

        name=(TextView)findViewById(R.id.name);

        sex=(TextView)findViewById(R.id.sex);

        age=(TextView)findViewById(R.id.age);

        area=(TextView)findViewById(R.id.area);

        fee=(TextView)findViewById(R.id.feekind);

        inTime=(TextView)findViewById(R.id.livedate);

        liveTime=(TextView)findViewById(R.id.livetime);

        level=(TextView)findViewById(R.id.level);

        doctor=(TextView)findViewById(R.id.doctor);

        nurse=(TextView)findViewById(R.id.nurse);

        deposit=(TextView)findViewById(R.id.deposit);

        allFee=(TextView)findViewById(R.id.allfee);

    }

}


