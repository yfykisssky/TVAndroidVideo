package com.android.tvvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by yangfengyuan on 16/7/28.
 */
public class RemindDialog extends Dialog {

    public RemindDialog(Context context) {
        super(context);


    }

    private void installApk(String path,Context context) {
        File apkfile = new File(path);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),"application/vnd.android.package-archive");
        context.startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
