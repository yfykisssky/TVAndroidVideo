package com.android.tvvideo.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by yangfengyuan on 16/7/21.
 */
public class UpdateHelpter {

    ProgressDialog progressDialog;

    Context context;

    String fileName;

    int allLength;

    int downLength;

    Handler viewUpdateHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.arg1!=0){

                progressDialog.setMax(msg.arg1);

                allLength=msg.arg1;

            }

            if(msg.arg2>=0){
                progressDialog.setProgress(msg.arg2);

                downLength=msg.arg2;
            }

            if(progressDialog.getMax()==msg.arg2){

                progressDialog.dismiss();

                update(context);
            }

        }
    };

    public boolean checkUpdate(Context context, String version, String url, String fileName) {

        this.context=context;

        this.fileName=fileName;

        progressDialog=new ProgressDialog(context);

        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置进度条对话框//样式（水平，旋转）

        progressDialog.setProgress(0);

        if(checkVersion(version,getVersion(context))){

            downFile(url);

            progressDialog.setTitle("新版本:"+ String.valueOf(version));

            progressDialog.setCancelable(false);

            progressDialog.show();

            return true;

        }

        return false;

    }

    private boolean checkVersion(String newVer, String oldVer){

        int newVersion= Integer.parseInt(newVer.replace(".",""));

        int oldVersion= Integer.parseInt(oldVer.replace(".",""));

        if(newVersion>oldVersion){
            return true;
        }else{
            return false;
        }

    }

    // 获取当前版本的版本号
    private String getVersion(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(),0);

            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }

    void downFile(final String downurl) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                //创建按一个URL实例
                URL url = null;
                try {
                    url = new URL(downurl);
                    //创建一个HttpURLConnection的链接对象
                    HttpURLConnection httpConn =(HttpURLConnection)url.openConnection();
                    //获取所下载文件的InputStream对象
                    InputStream is=httpConn.getInputStream();

                    int length =httpConn.getContentLength(); //获取文件大小

                    Message msg=new Message();

                    msg.arg1=length;

                    viewUpdateHandler.sendMessage(msg);       //设置进度条的总长度

                    FileOutputStream fileOutputStream = null;
                    if (is != null) {

                        fileOutputStream =context.openFileOutput(fileName, Context.MODE_WORLD_READABLE+ Context.MODE_WORLD_WRITEABLE);
                        byte[] buf = new byte[1024];   //这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一 下就下载完了，看不出progressbar的效果。
                        int ch = -1;
                        int process = 0;

                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            process += ch;

                            Message msgP=new Message();

                            msgP.arg2=process;

                            viewUpdateHandler.sendMessage(msgP);

                        }

                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    //安装文件，一般固定写法
    void update(Context context) {

        String[] command = {"chmod", "777",context.getFilesDir().getAbsolutePath()};

        ProcessBuilder builder = new ProcessBuilder(command);

        try {

            builder.start();

        } catch (IOException e) {

            Toast.makeText(context,"没有权限",Toast.LENGTH_SHORT).show();

            e.printStackTrace();

            return;

        }

  /*      try{

            String command = "chmod 777 " + destFile.getAbsolutePath();
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(command);

        }catch (IOException e)
        {

            e.printStackTrace();

        }*/

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(context.getFilesDir().getAbsoluteFile(),fileName)),"application/vnd.android.package-archive");
        context.startActivity(intent);
    }

}
