package com.android.tvvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.tvvideo.R;

/**
 * Created by yangfengyuan on 16/7/26.
 */
public class WarringDialog extends Dialog {

    String title;
    String data;
    boolean isButton;

    public WarringDialog(Context context,String title,String data,boolean isButton) {
        super(context, R.style.Base_Dialog);

        this.title=title;
        this.data=data;
        this.isButton=isButton;

        initDialog(context);
    }

    private void initDialog(Context context) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.dialog_warring);

        Button confirm = (Button) this.findViewById(R.id.confirm);

        TextView titleTex=(TextView) this.findViewById(R.id.title);

        TextView dataTex=(TextView) this.findViewById(R.id.data);

        titleTex.setText(title);

        dataTex.setText(data);

        if(isButton){

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WarringDialog.this.dismiss();
                }
            });

        }else{
            confirm.setVisibility(View.GONE);
        }

        this.setCancelable(false);
    }
}
