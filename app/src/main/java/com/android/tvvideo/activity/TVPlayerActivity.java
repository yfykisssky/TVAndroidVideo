package com.android.tvvideo.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.view.ScrollRelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yangfengyuan on 16/7/29.
 */
public class TVPlayerActivity extends BaseActivity {

    ListView listView;

    ScrollRelativeLayout relativeLayout;

    Context context;

    MyAdapter myAdapter;

    CountTimeThread countTimeThread;

    MyHandler mHandler=new MyHandler(this);

    List<Map<String,String>> listData=new ArrayList<>();

    int indexList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_tvplayer);

        context = this;

        initView();

        startCountTimeThread();

    }


    private void initView() {

        relativeLayout = (ScrollRelativeLayout) findViewById(R.id.relative);

        listView = (ListView) findViewById(R.id.list);

        myAdapter = new MyAdapter();

        listView.setAdapter(myAdapter);

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Log.e("select",String.valueOf(i));

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
            // return listData.size();
            return 20;
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

         /*   listData.get(i).get("name");
            listData.get(i).get("path");*/

            myHolder.indexTex.setText(String.valueOf(i));

            if(indexList==i){

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

                Log.e("up","up");

                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:

                Log.e("down","down");

                break;
        }

        return super.onKeyDown(keyCode, event);
    }
}

