package com.android.tvvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.android.tvvideo.R;

/**
 * Created by yangfengyuan on 16/8/9.
 */
public class ShutDownDialog extends Dialog {

    TextView remindTex;

    public ShutDownDialog(Context context) {
        super(context, R.style.Base_Dialog);

        initView();
    }

    private void initView() {

        setContentView(R.layout.dialog_shutdown);

        remindTex = (TextView) this.findViewById(R.id.remind);

    }

    Handler timeHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            remindTex.setText("还有"+String.valueOf(msg.what)+"秒后关机");

            if(msg.what==0){

            }else{

                Message message=new Message();

                message.what=msg.what-1;

                timeHandler.sendMessageDelayed(message,1000);
            }

        }
    };

    public void setData(int second) {

        Message message=new Message();

        message.what=second;

        timeHandler.sendMessage(message);

    }
}

