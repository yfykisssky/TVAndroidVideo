package com.android.tvvideo.activity;

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
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.tools.SystemUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

              /*  try {

                    JSONObject jsonObject=new JSONObject(data);

                    final String remark=jsonObject.getString("remark");
                    String path=jsonObject.getString("path");
                    String bottomremark=jsonObject.getString("bottomremark");


                    detial.setText(remark);

                    if(bottomremark!=null){
                        bottom.setText(bottomremark);
                    }

                    ImageLoad.loadDefultImage(path,img);

                } catch (JSONException e) {
                    e.printStackTrace();
                }*/


            }

            @Override
            public void onFailed(String error) {
                showToast(error);
            }
        });



    }

    private void initView() {

        listView=(ListView)findViewById(R.id.list);

        myAdapter=new MyAdapter();

        listView.setAdapter(myAdapter);

        comfirmBnt=(Button)findViewById(R.id.confirm);

        comfirmBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendFeedBack();

            }
        });

    }

    private void sendFeedBack() {


        JSONObject postData=new JSONObject();
        try {
            postData.put("ipaddress", SystemUtil.getLocalHostIp());

            new NetDataTool(this).sendPost(NetDataConstants.SATISFACTION_FEED_BACK,postData.toString(), new NetDataTool.IResponse() {
                @Override
                public void onSuccess(String data) {
/*

                    try {
*/
/*
                        JSONObject jsonObject=new JSONObject(data);

                        examRoomId= String.valueOf(jsonObject.getInt("id"));

                        String remark=jsonObject.getString("remark");
                        String path = jsonObject.getString("path");
                        String bottomremark=jsonObject.getString("bottomremark");

                        detial.setText(remark);

                        if(bottomremark!=null){
                            bottom.setText(bottomremark);
                        }

                        ImageLoad.loadDefultImage(path,img);*//*


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
*/


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
                view = LayoutInflater.from(context).inflate(R.layout.item_satisfaction_list, null);
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

