package com.android.tvvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.tvvideo.R;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangfengyuan on 16/8/25.
 */
public class ComplainDialog extends Dialog {

    Button confirmBnt;

    Button cancelBnt;

    Context context;

    CheckBox checkBox1;

    CheckBox checkBox2;

    CheckBox checkBox3;

    EditText otherEdit;

    String id;

    String kind;

    final String[] checkStr={"服务态度差","不负责任","过度医疗"};

    public ComplainDialog(Context context) {
        super(context, R.style.Base_Dialog);

        this.context=context;

        initView();
    }

    private void initView() {

        setContentView(R.layout.dialog_complain);

        confirmBnt=(Button)this.findViewById(R.id.ok);

        cancelBnt=(Button)this.findViewById(R.id.cancel);

        checkBox1=(CheckBox)this.findViewById(R.id.radio1);

        checkBox2=(CheckBox)this.findViewById(R.id.radio2);

        checkBox3=(CheckBox)this.findViewById(R.id.radio3);

        otherEdit=(EditText)this.findViewById(R.id.other);

        SpannableString ss = new SpannableString("其他");
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(40,false);
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        otherEdit.setHint(new SpannedString(ss));

        setCheckBoxDrawable(checkBox1);
        setCheckBoxDrawable(checkBox2);
        setCheckBoxDrawable(checkBox3);

        checkBox1.setText(checkStr[0]);
        checkBox2.setText(checkStr[1]);
        checkBox3.setText(checkStr[2]);

        confirmBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if((!checkBox1.isChecked())&&(!checkBox2.isChecked())&&(!checkBox3.isChecked())&&(TextUtils.isEmpty(otherEdit.getText().toString()))){
                    Toast.makeText(getContext(),"请选择或填写投诉项",Toast.LENGTH_SHORT).show();
                    return;
                }

                String complainReason="";

                if(checkBox1.isChecked()){
                    complainReason+=checkStr[0];
                }

                if(checkBox2.isChecked()){
                    complainReason+=","+checkStr[1];
                }

                if(checkBox3.isChecked()){
                    complainReason+=","+checkStr[2];
                }

                if(!TextUtils.isEmpty(otherEdit.getText().toString())){
                    complainReason+=","+otherEdit.getText().toString();
                }

                sendComplain(complainReason);
            }
        });

        cancelBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComplainDialog.this.dismiss();
            }
        });

    }

    public void setData(String id,String kind){

        this.id=id;

        this.kind=kind;

    }

    private void setCheckBoxDrawable(CheckBox checkBox){

        Drawable drawableAdd = context.getResources().getDrawable(R.drawable.radiobutton_selector);
        drawableAdd.setBounds(0, 0,80,80);
        checkBox.setCompoundDrawables(drawableAdd, null, null, null);

    }

    private void sendComplain(String remark) {

        JSONObject postData=new JSONObject();
        try {
          /*  postData.put("ipaddress", SystemUtil.getLocalHostIp());
            postData.put("hospitalId", VLCApplication.getInstance().getPatientNum());
            postData.put("mobile",VLCApplication.getInstance().getPatientPhoneNum());*/

            postData.put("ipaddress","192.168.2.117");
            postData.put("hospitalId","123456");
            postData.put("mobile","15936046693");
            postData.put("remark",remark);
            postData.put("id",id);
            postData.put("kind",kind);
            new NetDataTool(getContext()).sendPost(NetDataConstants.SEND_COMPLAIN,postData.toString(), new NetDataTool.IResponse() {
                @Override
                public void onSuccess(String data) {

                    if(Boolean.parseBoolean(data)){
                        Toast.makeText(getContext(),"投诉成功",Toast.LENGTH_SHORT).show();

                        ComplainDialog.this.dismiss();
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

