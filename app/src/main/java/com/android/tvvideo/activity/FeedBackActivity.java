package com.android.tvvideo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.tvvideo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yangfengyuan on 16/7/29.
 */
public class FeedBackActivity extends Activity {

    ListView listView;

    Context context;

    MyAdapter myAdapter;

    List<Map<String,String>> listData=new ArrayList<>();

    Button comfirmBnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        context=this;

        initView();

    }

    private void initView() {

        listView=(ListView)findViewById(R.id.list);

        myAdapter=new MyAdapter();

        listView.setAdapter(myAdapter);

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
                view = LayoutInflater.from(context).inflate(R.layout.item_feedback_list, null);
                myHolder = new MyHolder();
                myHolder.remarkTex = (TextView) view.findViewById(R.id.remark);
                myHolder.radioGroup= (RadioGroup) view.findViewById(R.id.group);
                view.setTag(myHolder);
            } else {
                myHolder = (MyHolder)view.getTag();
            }
         /*   listData.get(i).get("name");
            listData.get(i).get("path");*/

//            myHolder.remarkTex.setText(listData.get(i).get("remark"));

            myHolder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {

                    switch (i){
                        case R.id.radio1:
                            Log.e("checked","1");
                        break;
                        case R.id.radio2:
                            Log.e("checked","2");
                            break;
                        case R.id.radio3:
                            Log.e("checked","3");
                            break;
                    }

                }
            });

            return view;
        }
    }

    class MyHolder {
        TextView remarkTex;
        RadioGroup radioGroup;
    }


}
