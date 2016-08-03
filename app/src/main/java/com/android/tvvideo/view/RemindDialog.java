package com.android.tvvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.tvvideo.R;

/**
 * Created by yangfengyuan on 16/7/28.
 */
public class RemindDialog extends Dialog {

    TextView remindTex;

    Button confirmBnt;

    public RemindDialog(Context context) {
        super(context, R.style.Base_Dialog);

        initView();
    }

    private void initView() {

        setContentView(R.layout.dialog_remind);

        remindTex=(TextView)this.findViewById(R.id.remind);

        confirmBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RemindDialog.this.dismiss();
            }
        });

    }

    public void setData(String remind){
        remindTex.setText(remind);
    }

}
