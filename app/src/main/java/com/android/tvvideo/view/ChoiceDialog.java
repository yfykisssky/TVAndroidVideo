package com.android.tvvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tvvideo.R;

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


    public ChoiceDialog(Context context) {
        super(context);

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

    public void setData(String url,String name,String price,String intro){

        //imageView

        nameTex.setText(name);

        priceTex.setText(price);

        introTex.setText(intro);

    }

    private void sendOrder(){
        ChoiceDialog.this.dismiss();
    }


}
