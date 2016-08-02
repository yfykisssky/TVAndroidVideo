package com.android.tvvideo.view;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by yangfengyuan on 16/7/28.
 */
public class PushService extends Service {

    public static final String BROAD_CAST_ACTION="com.teachvideo.msg.push";

    String serviceUrl="ws://nonobank.iask.in/ws/";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        serviceUrl="ws://"+"nonobank.iask.in"+"/ws/";
//ws://nonobank.iask.in/ws/192.168.1.2
        serviceUrl+="192.168.1.2";

        startReceiverPush();

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void startReceiverPush(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectPushService();

            }
        }).start();

    }

    private void connectPushService(){

        URI uri=null;

        try {

            uri = new URI(serviceUrl);

        } catch (URISyntaxException e) {

            e.printStackTrace();

        }

        WebSocketWorker webSocketWorker=new WebSocketWorker(uri, new Draft_17());

        webSocketWorker.connect();

    }

    private void sendMsg(String data){

        Intent intent=new Intent();

        intent.putExtra("kind","");

        intent.putExtra("data",data);

        intent.setAction(BROAD_CAST_ACTION);

        sendBroadcast(intent);

    }

    private class WebSocketWorker extends WebSocketClient{

        public WebSocketWorker(URI serverUri, Draft draft) {
            super(serverUri, draft);

        }

        @Override
        public void onClose(int arg0, String arg1, boolean arg2) {
            Log.e("close","close");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            connectPushService();

        }

        @Override
        public void onError(Exception error) {
            Log.e("error","error"+error.getLocalizedMessage());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            connectPushService();

        }

        @Override
        public void onMessage(String msg) {

            Log.e("message",msg);

            sendMsg(msg);

        }

        @Override
        public void onOpen(ServerHandshake arg0) {
            Log.e("open","open");
        }

    }
}
