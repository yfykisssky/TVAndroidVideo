package com.android.tvvideo.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.tvvideo.R;
import com.android.tvvideo.tools.PushService;

import java.util.List;

public class MainActivity extends Activity {

    ListView listView;

    Button upBnt;
    Button downBnt;

    MyListAdapter myAdapter;

    String activityName;

    Context context;

    protected void setActivityName(String activityName){
        this.activityName=activityName;
    }

    private void initPushService(Context context){

        Intent intent=new Intent(this,PushService.class);

        ServiceConnection conn=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        context.bindService(intent,conn,Context.BIND_AUTO_CREATE);

    }

    BroadcastReceiver pushReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String kind=intent.getStringExtra("kind");

            switch(kind){
                case "remind":
                    if(isTopActivity(context,activityName)){
                        showRemindDialog();
                    }
                    break;
                case "playvideo":
                    break;
                case "volume":
                    break;
                case "onoff":
                    break;
                case "":
                    break;
            }

        }
    };

    private void registerPushReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(PushService.BROAD_CAST_ACTION);
        registerReceiver(pushReceiver, filter);
    }

    private void unregisterPushReceiver(){
        unregisterReceiver(pushReceiver);
    }

    private void showRemindDialog(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        context=this;

        initPushService(this);

        //initView();

    }

    private void initView() {

        upBnt=(Button)findViewById(R.id.up);

        upBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        downBnt=(Button)findViewById(R.id.down);

        downBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        listView=(ListView) findViewById(R.id.list);

        myAdapter=new MyListAdapter();

        listView.setAdapter(myAdapter);

    }

    class MyListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 0;
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

            view= LayoutInflater.from(context).inflate(R.layout.item_weather,null);

            return view;
        }
    }

   /* class MyHolder{
        Text
    }*/

    private static boolean isTopActivity(Context context, String className)
    {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
