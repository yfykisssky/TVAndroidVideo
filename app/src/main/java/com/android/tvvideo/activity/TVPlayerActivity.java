package com.android.tvvideo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.tools.ImageLoad;
import com.android.tvvideo.tools.SystemUtil;
import com.android.tvvideo.view.MarqueeText;
import com.android.tvvideo.view.ScrollRelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yangfengyuan on 16/7/29.
 */
public class TVPlayerActivity extends BaseActivity {

    //############################################################################################################################################################################
    //############################################################################################################################################################################
    //############################################################################################################################################################################
    //############################################################################################################################################################################
    //############################################################################################################################################################################

    ListView listView;

    ScrollRelativeLayout relativeLayout;

    Context context;

    MyAdapter myAdapter;

    CountTimeThread countTimeThread;

    MyHandler mHandler=new MyHandler(this);

    List<Map<String,String>> listData=new ArrayList<>();

    int indexList;

    WebView adWebView;

    String adUrl;

    MarqueeText showMsgTex;

    String msgData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_tvplayer);

        context = this;

        initView();

        getAdData();

        getShowMsg();

        super.setOnPushMsgListener(new PushMsgListener() {
            @Override
            public void onMsgReceive(Intent data) {

                String kind=data.getStringExtra("kind");

                if(kind.equals("adshow")){

                    getAdData();

                }

                if(kind.equals("showmsg")){

                    getShowMsg();

                }

            }
        });

        startCountTimeThread();

    }

    private void getShowMsg() {

        new NetDataTool(this).sendGet(NetDataConstants.GET_MSG_DATA, new NetDataTool.IResponse() {
            @Override
            public void onSuccess(String data) {

                msgData="";

                final int startHour=0;
                final int startMinute=0;

                final int endHour=0;
                final int endMinute=0;

                SystemUtil.getLocalTime(new SystemUtil.GetLocalTime() {
                    @Override
                    public void time(int year, int month, int day, int hour, int minute, int second) {

                        msgShowHandler.removeCallbacks(msgShowRunnable);

                        msgShowHandler.removeCallbacks(msgHideRunnable);

                        long startTime=((startHour-hour)*60+(startMinute-minute))*60*1000;

                        long endTime=((endHour-hour)*60+(endMinute-minute))*60*1000;

                        if(endTime<=0){

                            msgShowHandler.post(msgHideRunnable);

                            return;
                        }

                        if(startTime<=0){
                            msgShowHandler.post(msgShowRunnable);
                        }else{
                            msgShowHandler.postDelayed(msgShowRunnable,startTime);
                        }

                        msgShowHandler.postDelayed(msgHideRunnable,endTime);

                    }
                });

            }

            @Override
            public void onFailed(String error) {
                showToast(error);
            }
        });



    }

    private void getAdData() {

        new NetDataTool(this).sendGet(NetDataConstants.GET_AD_DATA, new NetDataTool.IResponse() {
            @Override
            public void onSuccess(String data) {

                adUrl="";

                final int startHour=0;
                final int startMinute=0;

                final int endHour=0;
                final int endMinute=0;

                SystemUtil.getLocalTime(new SystemUtil.GetLocalTime() {
                    @Override
                    public void time(int year, int month, int day, int hour, int minute, int second) {

                        adShowHandler.removeCallbacks(adShowRunnable);

                        adShowHandler.removeCallbacks(adHideRunnable);

                        long startTime=((startHour-hour)*60+(startMinute-minute))*60*1000;

                        long endTime=((endHour-hour)*60+(endMinute-minute))*60*1000;

                        if(endTime<=0){

                            adShowHandler.post(adHideRunnable);

                            return;
                        }

                        if(startTime<=0){
                            adShowHandler.post(adShowRunnable);
                        }else{
                            adShowHandler.postDelayed(adShowRunnable,startTime);
                        }

                        adShowHandler.postDelayed(adHideRunnable,endTime);

                    }
                });

            }

            @Override
            public void onFailed(String error) {
                showToast(error);
            }
        });

    }

    Handler adShowHandler=new Handler();

    Runnable adShowRunnable=new Runnable() {

        @Override
        public void run() {
            showAd();
        }
    };

    Runnable adHideRunnable=new Runnable() {
        @Override
        public void run() {
            hideAd();
        }
    };

    private void showAd(){
        adWebView.loadUrl(adUrl);
        adWebView.setVisibility(View.VISIBLE);
    }

    private void hideAd(){
        adWebView.setVisibility(View.GONE);
    }

    Handler msgShowHandler=new Handler();

    Runnable msgShowRunnable=new Runnable() {

        @Override
        public void run() {
            showMsg();
        }
    };

    Runnable msgHideRunnable=new Runnable() {
        @Override
        public void run() {
            hideMsg();
        }
    };

    private void showMsg(){

        showMsgTex.setText(msgData);

        showMsgTex.startFor0();

        showMsgTex.setVisibility(View.VISIBLE);
    }

    private void hideMsg(){

        showMsgTex.setVisibility(View.GONE);

    }

    private void initView() {

        showMsgTex=(MarqueeText)findViewById(R.id.showmsg);

        adWebView=(WebView)findViewById(R.id.adwebview);

        relativeLayout = (ScrollRelativeLayout) findViewById(R.id.relative);

        listView = (ListView) findViewById(R.id.list);

        myAdapter = new MyAdapter();

        listView.setAdapter(myAdapter);

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                indexList=i;
                myAdapter.notifyDataSetChanged();

                countTimeThread.reset();

                if (!relativeLayout.isShow()) {
                    relativeLayout.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
           return listData.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            MyHolder myHolder = null;
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.item_tv_list, null);
                myHolder = new MyHolder();
                myHolder.indexTex = (TextView) view.findViewById(R.id.index);
                myHolder.img = (ImageView) view.findViewById(R.id.img);
                view.setTag(myHolder);
            } else {
                myHolder = (MyHolder)view.getTag();
            }

            String imgUrl=listData.get(i).get("path");

            myHolder.indexTex.setText(String.valueOf(i));

            ImageLoad.loadDefultImage(imgUrl,myHolder.img);

            if(indexList==i){
                view.setBackgroundColor(Color.parseColor("#ccFF0000"));
            }else{
                view.setBackgroundColor(Color.parseColor("#cc000000"));
            }

            return view;
        }
    }

    class MyHolder {
        TextView indexTex;
        ImageView img;
    }

    private void startCountTimeThread() {
        countTimeThread = new CountTimeThread(2);
        countTimeThread.start();
    }


    private void hide() {
        if (relativeLayout.isShow()) {
            relativeLayout.hide();
        }
    }

    static class MyHandler extends Handler {

        private final int MSG_HIDE = 0x001;

        private WeakReference<TVPlayerActivity> weakRef;

        public MyHandler(TVPlayerActivity pMainActivity) {
            weakRef = new WeakReference<TVPlayerActivity>(pMainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            final TVPlayerActivity mainActivity = weakRef.get();

            if (mainActivity != null) {
                switch (msg.what) {
                    case MSG_HIDE:
                        mainActivity.hide();
                        break;

                }
            }
            super.handleMessage(msg);
        }

        public void sendHideControllMessage() {
            obtainMessage(MSG_HIDE).sendToTarget();
        }

    }

    class CountTimeThread extends Thread {
        private final long maxVisibleTime;
        private long startVisibleTime;

        public CountTimeThread(int second) {
            maxVisibleTime = second * 1000;

            setDaemon(true);
        }

        private synchronized void reset() {
            startVisibleTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            startVisibleTime = System.currentTimeMillis();

            while (true) {

                if (startVisibleTime + maxVisibleTime < System.currentTimeMillis()) {
                    mHandler.sendHideControllMessage();

                    startVisibleTime = System.currentTimeMillis();
                }

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
            }

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch(keyCode){
            case KeyEvent.KEYCODE_DPAD_UP:

                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:

                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    //############################################################################################################################################################################
    //############################################################################################################################################################################
    //############################################################################################################################################################################
    //############################################################################################################################################################################
    //############################################################################################################################################################################
}

