package com.android.tvvideo.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.tools.ViewTool;

import org.videolan.libvlc.VLCApplication;


/**
 * Created by yangfengyuan on 16/8/22.
 */
public class ComplainAndAdviceActivity extends BaseActivity {

    Context context;

    int[] images = {R.drawable.personal,R.drawable.fee};

    String[] names = {"满意度调查", "我要投诉"};

    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_server);

        context=this;

        initView();

    }

    private void initView() {

        gridView = (GridView) findViewById(R.id.grid);

        gridView.setNumColumns(4);

        gridView.setAdapter(new GridAdapter());

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent=null;

                switch (i){
                    case 0:
                        intent=new Intent(context,SatisfactionActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent=new Intent(context,ComplainActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

    }

    class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            MyGridHolder holder = new MyGridHolder();
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.item_in_hospital, null);
                holder.tex = (TextView) view.findViewById(R.id.tex);
                holder.img = (ImageView) view.findViewById(R.id.img);
                view.setTag(holder);
            } else {
                holder = (MyGridHolder) view.getTag();
            }

            holder.img.setBackground(ViewTool.getDrawble(context,images[i]));
            holder.tex.setText(names[i]);
            return view;
        }
    }

    class MyGridHolder {
        ImageView img;
        TextView tex;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        VLCApplication.getInstance().setPatientPhoneNum(null);

        VLCApplication.getInstance().setPatientNum(null);

    }

}



