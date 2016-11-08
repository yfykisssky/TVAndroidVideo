package com.android.tvvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.tools.SystemUtil;

/**
 * Created by yangfengyuan on 16/8/9.
 */
public class ShutDownDialog extends Dialog {

    TextView remindTex;

    final int second=5;

    public ShutDownDialog(Context context) {
        super(context, R.style.Base_Dialog);

        this.setCancelable(false);

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
                SystemUtil.shutDownSystem(getContext());
            }else{

                Message message=new Message();

                message.what=msg.what-1;

                timeHandler.sendMessageDelayed(message,1000);
            }

        }
    };

    public void start() {

        Message message=new Message();

        message.what=second;

        timeHandler.sendMessage(message);

    }
}

