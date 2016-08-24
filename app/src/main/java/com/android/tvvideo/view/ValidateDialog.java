package com.android.tvvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.tvvideo.R;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.tools.CheckTool;
import com.android.tvvideo.tools.SystemUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.vlc.VLCApplication;

/**
 * Created by yangfengyuan on 16/7/29.
 */
public class ValidateDialog extends Dialog {

    EditText phoneEdit;

    EditText inhospiEdit;

    Button confirmBnt;

    Button cancelBnt;

    ValidateListener validateListener;

    public interface ValidateListener{
        void validate(boolean result);
    }

    public void setValidateListener(ValidateListener validateListener){
        this.validateListener=validateListener;
    }

    public ValidateDialog(Context context) {
        super(context, R.style.Base_Dialog);

        initView();
    }

    private void initView() {

        setContentView(R.layout.dialog_validate);

        phoneEdit=(EditText)this.findViewById(R.id.phonenum);

        inhospiEdit=(EditText)this.findViewById(R.id.inhospitalnum);

        confirmBnt=(Button)this.findViewById(R.id.ok);

        cancelBnt=(Button)this.findViewById(R.id.cancel);

        confirmBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phonenum=phoneEdit.getText().toString();

                String inhospinum=inhospiEdit.getText().toString();

                if(!CheckTool.isMobileNum(phonenum)){
                    Toast.makeText(ValidateDialog.this.getContext(),"手机号码错误",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(phonenum)||TextUtils.isEmpty(inhospinum)){
                    Toast.makeText(getContext(),"",Toast.LENGTH_SHORT).show();
                }else{

                    validate(phonenum,inhospinum);

                }

            }
        });

        cancelBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateDialog.this.dismiss();
            }
        });

    }


    private void validate(final String phonenum, final String inhospinum){

        JSONObject postData=new JSONObject();
        try {
            postData.put("ipaddress", SystemUtil.getLocalHostIp());
            postData.put("hospitalId",inhospinum);
            postData.put("mobile",phonenum);

            new NetDataTool(this.getContext()).sendPost(NetDataConstants.VALIDATE_ACCOUNT,postData.toString(),new NetDataTool.IResponse() {
                @Override
                public void onSuccess(String data) {

                    if(Boolean.parseBoolean(data)){

                        VLCApplication.getInstance().setPatientNum(inhospinum);

                        VLCApplication.getInstance().setPatientPhoneNum(phonenum);

                        validateListener.validate(true);
                    }else{
                        validateListener.validate(false);
                    }

                }

                @Override
                public void onFailed(String error) {

                    Toast.makeText(ValidateDialog.this.getContext(),error,Toast.LENGTH_SHORT).show();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}

