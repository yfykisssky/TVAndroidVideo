package com.android.tvvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.tvvideo.R;

/**
 * Created by yangfengyuan on 16/7/29.
 */
public class ValidateDialog extends Dialog {

    EditText phoneEdit;

    EditText inhospiEdit;

    Button confirmBnt;

    Button cancelBnt;


    public ValidateDialog(Context context) {
        super(context);

        initView();
    }

    private void initView() {

        setContentView(R.layout.dialog_choice);

        phoneEdit=(EditText)this.findViewById(R.id.phonenum);

        inhospiEdit=(EditText)this.findViewById(R.id.inhospitalnum);

        confirmBnt=(Button)this.findViewById(R.id.ok);

        cancelBnt=(Button)this.findViewById(R.id.cancel);

        confirmBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phonenum=phoneEdit.getText().toString();

                String inhospinum=inhospiEdit.getText().toString();

            /*    if(){
                    Toast.makeText(getContext(),"",Toast.LENGTH_SHORT).show();
                }else{

                }*/

                validate(phonenum,inhospinum);
            }
        });

        cancelBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateDialog.this.dismiss();
            }
        });

    }


    private void validate(String phonenum,String inhospinum){
        ValidateDialog.this.dismiss();
    }


}

