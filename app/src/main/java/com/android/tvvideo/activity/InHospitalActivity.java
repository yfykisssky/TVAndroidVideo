package com.android.tvvideo.activity;

import android.content.Context;
import android.content.Intent;
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



/**
 * Created by yfykisssky on 16/7/16.
 */
public class InHospitalActivity extends BaseActivity {

    Context context;

    int[] images = {R.drawable.inhospital_bg,R.drawable.right_bg,R.drawable.policy_bg};

    String[] names = {"住院告知", "患者权利和义务", "医保农合政策"};

    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_hospital);

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
                        intent=new Intent(context,InHospitalIntroActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent=new Intent(context,RightIntroActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent=new Intent(context,PolicyIntroActivity.class);
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

            holder.img.setBackgroundResource(images[i]);
            holder.tex.setText(names[i]);
            return view;
        }
    }

    class MyGridHolder {
        ImageView img;
        TextView tex;
    }


}

