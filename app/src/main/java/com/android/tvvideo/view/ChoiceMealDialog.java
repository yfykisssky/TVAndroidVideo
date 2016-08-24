package com.android.tvvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tvvideo.R;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.tools.ImageLoad;
import com.android.tvvideo.tools.SystemUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangfengyuan on 16/7/29.
 */
public class ChoiceMealDialog extends Dialog {

    ImageView imageView;

    TextView nameTex;

    TextView priceTex;

    TextView introTex;

    Button confirmBnt;

    Button cancelBnt;

    String id;

    String inHospitalNum;

    String phoneNum;

    Context context;

    public ChoiceMealDialog(Context context) {
        super(context,R.style.Base_Dialog);

        this.context=context;

        initView();
    }

    private void initView() {

        setContentView(R.layout.dialog_choice);

        imageView=(ImageView)this.findViewById(R.id.img);

        nameTex=(TextView)this.findViewById(R.id.name);

        priceTex=(TextView)this.findViewById(R.id.price);

        introTex=(TextView)this.findViewById(R.id.introduce);

        confirmBnt=(Button)this.findViewById(R.id.ok);

        cancelBnt=(Button)this.findViewById(R.id.cancel);

        confirmBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOrder(id,inHospitalNum,phoneNum);
            }
        });

        cancelBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChoiceMealDialog.this.dismiss();
            }
        });

    }

    public void setData(String name,String price,String intro,String imgUrl){

        nameTex.setText(name);

        priceTex.setText(price);

        introTex.setText(intro);

        ImageLoad.loadDefultImage(imgUrl,imageView);

    }

    public void setUserData(String id,String inHospitalNum,String phoneNum){

        this.id=id;

        this.inHospitalNum=inHospitalNum;

        this.phoneNum=phoneNum;

    }

    private void sendOrder(String id,String inhospinum,String phonenum){

        JSONObject postData=new JSONObject();
        try {
            postData.put("ipaddress", SystemUtil.getLocalHostIp());
            postData.put("hospitalId",inhospinum);
            postData.put("mobile",phonenum);
            postData.put("foodId",id);

            new NetDataTool(this.getContext()).sendPost(NetDataConstants.MEAL_ORDER,postData.toString(),new NetDataTool.IResponse() {
                @Override
                public void onSuccess(String data) {

                    if(Boolean.parseBoolean(data)){
                        Toast.makeText(context,"下单成功",Toast.LENGTH_SHORT).show();

                        ChoiceMealDialog.this.dismiss();
                    }else{
                        Toast.makeText(context,"下单失败",Toast.LENGTH_SHORT).show();
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


}
