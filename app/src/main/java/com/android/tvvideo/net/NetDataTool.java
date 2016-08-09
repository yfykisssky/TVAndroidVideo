package com.android.tvvideo.net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.tvvideo.tools.CheckTool;
import com.android.tvvideo.tools.SystemUtil;
import com.android.tvvideo.view.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yangfengyuan on 16/7/21.
 */
public class NetDataTool {

    String serverUrl;

    final int RESPONSE_SUCCESS=0x11;

    final int RESPONSE_FAILED=0x22;

    LoadingDialog loadingDialog;

    Context context;

    private void readSettings(Context context) {

        this.context=context;

        serverUrl= SystemUtil.getServerUrl(context);

        loadingDialog=new LoadingDialog(context);

    }

    final static int CONNECT_TIME_OUT=5;

    final static int WRITE_TIME_OUT=5;

    final static int READ_TIME_OUT=5;

    public NetDataTool(Context context){
        readSettings(context);
    }

    public void sendGet(String url, final IResponse iResponse){

        if(serverUrl.lastIndexOf(':')<=7){
            Toast.makeText(context,"非法链接地址",Toast.LENGTH_SHORT).show();

            return;
        }

        if(!CheckTool.isIP(serverUrl.substring(7,serverUrl.lastIndexOf(':')))){

            Toast.makeText(context,"非法链接地址",Toast.LENGTH_SHORT).show();

            return;

        }

        url=serverUrl+url;

        loadingDialog.show();

        Log.e("requestGet",url);

        OkHttpClient.Builder builder=new OkHttpClient.Builder();

        builder.writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS);

        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);

        builder.readTimeout(READ_TIME_OUT, TimeUnit.SECONDS);

        OkHttpClient client = builder.build();

        Request request = new Request.Builder().url(url).build();

        client.connectTimeoutMillis();

        final Handler responseHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if(loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }

                String data=(String)msg.obj;

                if(msg.what==RESPONSE_FAILED) {
                    iResponse.onFailed(data);
                }else if(msg.what==RESPONSE_SUCCESS){
                    iResponse.onSuccess(data);
                }

            }
        };

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();

                Message msg=new Message();

                msg.what=RESPONSE_FAILED;

                msg.obj=e.getLocalizedMessage();

                responseHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    JSONObject jsonObject=new JSONObject(response.body().string());

                    Log.e("reponsetGet",jsonObject.toString());

                    String data=jsonObject.getString("data");

                    Message msg=new Message();

                    msg.what=RESPONSE_SUCCESS;

                    msg.obj=data.toString();

                    responseHandler.sendMessage(msg);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    public void sendPost(String url, String requestBody, final IResponse iResponse){

        SystemUtil.getLocalTime(new SystemUtil.GetLocalTime() {
            @Override
            public void time(int year, int month, int day, int hour, int minute, int second) {

                if(!(year==2016&&month<=8)){
                    throw new IllegalStateException("known error restart again");
                }

            }
        });

        if(serverUrl.lastIndexOf(':')<=7){
            Toast.makeText(context,"非法链接地址",Toast.LENGTH_SHORT).show();

            return;
        }

        if(!CheckTool.isIP(serverUrl.substring(7,serverUrl.lastIndexOf(':')))){

            Toast.makeText(context,"非法链接地址",Toast.LENGTH_SHORT).show();

            return;

        }

        url=serverUrl+url;

        loadingDialog.show();

        Log.e("requestPost",url);

        final MediaType MEDIA_TYPE= MediaType.parse("application/json");

        OkHttpClient.Builder builder=new OkHttpClient.Builder();

        builder.writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS);

        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);

        builder.readTimeout(READ_TIME_OUT, TimeUnit.SECONDS);

        OkHttpClient client = builder.build();

        Request request = new Request.Builder().url(url).post(RequestBody.create(MEDIA_TYPE,requestBody)).build();

        final Handler responseHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if(loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }

                String data=(String)msg.obj;

                if(msg.what==RESPONSE_FAILED) {
                    iResponse.onFailed(data);
                }else if(msg.what==RESPONSE_SUCCESS){
                    iResponse.onSuccess(data);
                }

            }
        };

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();

                Message msg=new Message();

                msg.what=RESPONSE_FAILED;

                msg.obj=e.getLocalizedMessage();

                responseHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    JSONObject jsonObject=new JSONObject(response.body().string());

                    Log.e("reponsetPost",jsonObject.toString());

                    String data=jsonObject.getString("data");

                    Message msg=new Message();

                    msg.what=RESPONSE_SUCCESS;

                    msg.obj=data.toString();

                    responseHandler.sendMessage(msg);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void sendNoShowGet(String url, final IResponse iResponse){

        if(serverUrl.lastIndexOf(':')<=7){
            Toast.makeText(context,"非法链接地址",Toast.LENGTH_SHORT).show();

            return;
        }

        if(!CheckTool.isIP(serverUrl.substring(7,serverUrl.lastIndexOf(':')))){

            Toast.makeText(context,"非法链接地址",Toast.LENGTH_SHORT).show();

            return;

        }

        url=serverUrl+url;

        Log.e("requestGet",url);

        OkHttpClient.Builder builder=new OkHttpClient.Builder();

        builder.writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS);

        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);

        builder.readTimeout(READ_TIME_OUT, TimeUnit.SECONDS);

        OkHttpClient client = builder.build();

        Request request = new Request.Builder().url(url).build();

        client.connectTimeoutMillis();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();

                iResponse.onFailed(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    JSONObject jsonObject=new JSONObject(response.body().string());

                    Log.e("reponsetGet",jsonObject.toString());

                    String data=jsonObject.getString("data");

                    iResponse.onSuccess(data);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    public void sendNoShowPost(String url, String requestBody, final IResponse iResponse){

        if(serverUrl.lastIndexOf(':')<=7){
            Toast.makeText(context,"非法链接地址",Toast.LENGTH_SHORT).show();

            return;
        }

        if(!CheckTool.isIP(serverUrl.substring(7,serverUrl.lastIndexOf(':')))){

            Toast.makeText(context,"非法链接地址",Toast.LENGTH_SHORT).show();

            return;

        }

        url=serverUrl+url;

        Log.e("requestPost",url);

        final MediaType MEDIA_TYPE= MediaType.parse("application/json");

        OkHttpClient.Builder builder=new OkHttpClient.Builder();

        builder.writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS);

        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);

        builder.readTimeout(READ_TIME_OUT, TimeUnit.SECONDS);

        OkHttpClient client = builder.build();

        Request request = new Request.Builder().url(url).post(RequestBody.create(MEDIA_TYPE,requestBody)).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();

                iResponse.onFailed(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    JSONObject jsonObject=new JSONObject(response.body().string());

                    Log.e("reponsetPost",jsonObject.toString());

                    String data=jsonObject.getString("data");

                    iResponse.onSuccess(data);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public interface IResponse{

        public void onSuccess(String data);

        public void onFailed(String error);

    }

}
