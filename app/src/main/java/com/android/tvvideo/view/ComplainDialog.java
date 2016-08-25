package com.android.tvvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.tvvideo.R;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.tools.SystemUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangfengyuan on 16/8/25.
 */
public class ComplainDialog extends Dialog {

    Button confirmBnt;

    Button cancelBnt;

    String inHospitalNum;

    String phoneNum;

    Context context;

    RadioGroup radioGroup;

    public ComplainDialog(Context context) {
        super(context, R.style.Base_Dialog);

        this.context=context;

        initView();
    }

    private void initView() {

        setContentView(R.layout.dialog_choice);

        confirmBnt=(Button)this.findViewById(R.id.ok);

        cancelBnt=(Button)this.findViewById(R.id.cancel);

        radioGroup=(RadioGroup)this.findViewById(R.id.radiogroup);

        confirmBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // sendOrder(id,inHospitalNum,phoneNum);
            }
        });

        cancelBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComplainDialog.this.dismiss();
            }
        });

    }

    public void setUserData(String id,String inHospitalNum,String phoneNum){

    /*    this.id=id;

        this.inHospitalNum=inHospitalNum;

        this.phoneNum=phoneNum;*/

    }

    private void sendComplain() {

        JSONObject postData=new JSONObject();
        try {
            postData.put("ipaddress", SystemUtil.getLocalHostIp());

            new NetDataTool(getContext()).sendPost(NetDataConstants.SEND_COMPLAIN,postData.toString(), new NetDataTool.IResponse() {
                @Override
                public void onSuccess(String data) {

                    if(Boolean.parseBoolean(data)){
                        Toast.makeText(getContext(),"投诉成功",Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailed(String error) {

                    Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}

