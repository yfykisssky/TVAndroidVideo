package com.android.tvvideo.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;

public class WeatherActivity extends BaseActivity {

    ListView listView;

    Button upBnt;
    Button downBnt;

    MyListAdapter myAdapter;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_weather);

        context=this;

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

            //view= LayoutInflater.from(context).inflate(R.layout.item_weather,null);

            return view;
        }
    }

   /* class MyHolder{
        Text
    }*/


}
