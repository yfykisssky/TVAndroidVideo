package com.android.tvvideo.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangfengyuan on 16/8/22.
 */
public class SatisfactionActivity extends BaseActivity {

    ListView listView;

    Context context;

    MyAdapter myAdapter;

    List<Map<String,String>> listData=new ArrayList<>();

    Button comfirmBnt;

    int listSelect=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_satisfaction);

        context=this;

        initView();

        getFeedBackList();

    }

    private void getFeedBackList() {

        new NetDataTool(this).sendGet(NetDataConstants.GET_SATISFACTION_LIST, new NetDataTool.IResponse() {
            @Override
            public void onSuccess(String data) {

                try {

                    JSONArray array=new JSONArray(data);

                    for(int x=0;x<array.length();x++){

                        JSONObject jsonObject=array.getJSONObject(x);

                        Map<String,String> map=new HashMap<String, String>();

                        map.put("id",jsonObject.getString("id"));

                        map.put("remark",jsonObject.getString("remark"));

                        listData.add(map);
                    }

                    myAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailed(String error) {
                showToast(error);
            }
        });


    }

    private void sendFeedBack() {

        JSONObject postData=new JSONObject();
        try {

            JSONArray array=new JSONArray();

            for(int c=0;c<array.length();c++){

                JSONObject jsonObject=new JSONObject();

                String id=listData.get(c).get("id");

                jsonObject.put("id",id);

                String selectIndex=listData.get(c).get("selectIndex");

                jsonObject.put("selectIndex",selectIndex);

                array.put(jsonObject);

            }

            new NetDataTool(this).sendPost(NetDataConstants.SATISFACTION_FEED_BACK,postData.toString(), new NetDataTool.IResponse() {
                @Override
                public void onSuccess(String data) {
                    showToast("发送成功!");
                }

                @Override
                public void onFailed(String error) {
                    showToast(error);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void initView() {

        listView=(ListView)findViewById(R.id.list);

        myAdapter=new MyAdapter();

        listView.setAdapter(myAdapter);

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                listSelect=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        listView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                int indexRadioSelect=Integer.parseInt(listData.get(listSelect).get("selectIndex"));

                if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT){

                    if(indexRadioSelect>0){
                        indexRadioSelect--;
                    }

                }else if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT){

                    if(indexRadioSelect<3){
                        indexRadioSelect++;
                    }
                }

                listData.get(listSelect).remove("selectIndex");
                listData.get(listSelect).put("selectIndex",String.valueOf(indexRadioSelect));
                myAdapter.notifyDataSetChanged();

                return false;
            }
        });

        comfirmBnt=(Button)findViewById(R.id.confirm);

        comfirmBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendFeedBack();

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
                view = LayoutInflater.from(context).inflate(R.layout.item_satisfaction_list, null);
                myHolder = new MyHolder();
                myHolder.remarkTex = (TextView) view.findViewById(R.id.remark);
                myHolder.radioGroup= (RadioGroup) view.findViewById(R.id.group);
                view.setTag(myHolder);
            } else {
                myHolder = (MyHolder)view.getTag();
            }

            myHolder.remarkTex.setText(listData.get(i).get("remark"));

            if(i==listSelect){

                switch(Integer.parseInt(listData.get(listSelect).get("selectIndex"))){

                    case 0:
                        myHolder.radioGroup.check(R.id.radio1);
                        break;
                    case 1:
                        myHolder.radioGroup.check(R.id.radio2);
                        break;
                    case 2:
                        myHolder.radioGroup.check(R.id.radio3);
                        break;

                }


            }

            return view;
        }
    }

    class MyHolder {
        TextView remarkTex;
        RadioGroup radioGroup;
    }

}

