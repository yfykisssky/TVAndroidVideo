package com.android.tvvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.tools.ImageLoad;

/**
 * Created by yangfengyuan on 16/7/29.
 */
public class ChoiceDialog extends Dialog {

    ImageView imageView;

    TextView nameTex;

    TextView priceTex;

    TextView introTex;

    Button confirmBnt;

    Button cancelBnt;

    String orderId;

    public ChoiceDialog(Context context) {
        super(context,R.style.Base_Dialog);

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
                sendOrder();
            }
        });

        cancelBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChoiceDialog.this.dismiss();
            }
        });

    }

    public void setData(String name,String price,String intro,String imgUrl){

        nameTex.setText(name);

        priceTex.setText(price);

        introTex.setText(intro);

        ImageLoad.loadDefultImage(imgUrl,imageView);

    }

    public void setUserData(String orderId,String inHospitalNum){

        this.orderId=orderId;

    }

    private void sendOrder(){
        ChoiceDialog.this.dismiss();
    }


}
