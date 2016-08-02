package com.android.tvvideo.util;

import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;

import com.hospital.video.R;


/**
 * Created by yangfengyuan on 16/7/18.
 */
public class NetWorkUtil {

    static NetWorkUtil netWorkUtil;

    Dialog dialog;

    public static NetWorkUtil getInstance(Context context){

        if(netWorkUtil==null){
            netWorkUtil=new NetWorkUtil(context);
        }

        return netWorkUtil;
    }

    public NetWorkUtil(Context context){

        dialog=new Dialog(context);

        dialog.setContentView(R.layout.dialog_alert_nowifi);

        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        dialog.setCancelable(false);

    }

    public void showAlertDialog() {

        if(!dialog.isShowing()){

            dialog.show();

        }

    }

    public void hideAlertDialog(){

        if(dialog.isShowing()){

            dialog.dismiss();

        }

    }

}
