package com.android.tvvideo.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.tools.SystemUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.VLCApplication;

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

    List<Map<String,Object>> listData=new ArrayList<>();

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

                        Map<String,Object> map=new HashMap<String,Object>();

                        map.put("id",jsonObject.getString("id"));

                        map.put("question",jsonObject.getString("question"));

                        JSONArray arrayChoices=jsonObject.getJSONArray("choices");

                        List<String> list=new ArrayList<String>();

                        for(int k=0;k<arrayChoices.length();k++){
                            list.add(arrayChoices.getString(k));
                        }

                        map.put("choices",list);

                        map.put("selectIndex","0");

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

            postData.put("ipaddress", SystemUtil.getLocalHostIp());
            postData.put("hospitalId", VLCApplication.getInstance().getPatientNum());
            postData.put("mobile",VLCApplication.getInstance().getPatientPhoneNum());

            JSONArray array=new JSONArray();

            for(int c=0;c<listData.size();c++){

                JSONObject jsonObject=new JSONObject();

                String id=(String)listData.get(c).get("id");

                jsonObject.put("question",id);

                String selectIndex=(String)listData.get(c).get("selectIndex");

                String choice=((List<String>)listData.get(c).get("choices")).get(Integer.parseInt(selectIndex));

                jsonObject.put("choiced",choice);

                array.put(jsonObject);

            }

            postData.put("list",array);

            new NetDataTool(this).sendPost(NetDataConstants.SATISFACTION_FEED_BACK,postData.toString(), new NetDataTool.IResponse() {
                @Override
                public void onSuccess(String data) {
                    if(Boolean.parseBoolean(data)){
                        showToast("发送成功!");
                    }else{
                        showToast("发送失败!");
                    }
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

                int indexRadioSelect=Integer.parseInt((String)listData.get(listSelect).get("selectIndex"));

                if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT){

                    if(indexRadioSelect>0){
                        indexRadioSelect--;
                    }

                }else if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT){

                    if(indexRadioSelect<((List<String>)listData.get(i).get("choices")).size()){
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
                myHolder.radioButton1=(RadioButton)view.findViewById(R.id.radio1);
                myHolder.radioButton2=(RadioButton)view.findViewById(R.id.radio2);
                myHolder.radioButton3=(RadioButton)view.findViewById(R.id.radio3);
                myHolder.radioButton4=(RadioButton)view.findViewById(R.id.radio4);
                myHolder.radioButton5=(RadioButton)view.findViewById(R.id.radio5);
                view.setTag(myHolder);
            } else {
                myHolder = (MyHolder)view.getTag();
            }

            setRadioButtonDrawable(myHolder.radioButton1);
            setRadioButtonDrawable(myHolder.radioButton2);
            setRadioButtonDrawable(myHolder.radioButton3);
            setRadioButtonDrawable(myHolder.radioButton4);
            setRadioButtonDrawable(myHolder.radioButton5);

            myHolder.remarkTex.setText(String.valueOf(i+1)+"."+(String)listData.get(i).get("question"));

            switch (((List<String>)listData.get(i).get("choices")).size()){
                case 1:
                    myHolder.radioButton1.setVisibility(View.VISIBLE);
                    myHolder.radioButton2.setVisibility(View.GONE);
                    myHolder.radioButton3.setVisibility(View.GONE);
                    myHolder.radioButton4.setVisibility(View.GONE);
                    myHolder.radioButton5.setVisibility(View.GONE);

                    myHolder.radioButton1.setText(((List<String>)listData.get(i).get("choices")).get(0));
                    break;
                case 2:
                    myHolder.radioButton1.setVisibility(View.VISIBLE);
                    myHolder.radioButton2.setVisibility(View.VISIBLE);
                    myHolder.radioButton3.setVisibility(View.GONE);
                    myHolder.radioButton4.setVisibility(View.GONE);
                    myHolder.radioButton5.setVisibility(View.GONE);

                    myHolder.radioButton1.setText(((List<String>)listData.get(i).get("choices")).get(0));
                    myHolder.radioButton2.setText(((List<String>)listData.get(i).get("choices")).get(1));
                    break;
                case 3:
                    myHolder.radioButton1.setVisibility(View.VISIBLE);
                    myHolder.radioButton2.setVisibility(View.VISIBLE);
                    myHolder.radioButton3.setVisibility(View.VISIBLE);
                    myHolder.radioButton4.setVisibility(View.GONE);
                    myHolder.radioButton5.setVisibility(View.GONE);

                    myHolder.radioButton1.setText(((List<String>)listData.get(i).get("choices")).get(0));
                    myHolder.radioButton2.setText(((List<String>)listData.get(i).get("choices")).get(1));
                    myHolder.radioButton3.setText(((List<String>)listData.get(i).get("choices")).get(2));
                    break;
                case 4:
                    myHolder.radioButton1.setVisibility(View.VISIBLE);
                    myHolder.radioButton2.setVisibility(View.VISIBLE);
                    myHolder.radioButton3.setVisibility(View.VISIBLE);
                    myHolder.radioButton4.setVisibility(View.VISIBLE);
                    myHolder.radioButton5.setVisibility(View.GONE);

                    myHolder.radioButton1.setText(((List<String>)listData.get(i).get("choices")).get(0));
                    myHolder.radioButton2.setText(((List<String>)listData.get(i).get("choices")).get(1));
                    myHolder.radioButton3.setText(((List<String>)listData.get(i).get("choices")).get(2));
                    myHolder.radioButton4.setText(((List<String>)listData.get(i).get("choices")).get(3));
                    break;
                case 5:
                    myHolder.radioButton1.setVisibility(View.VISIBLE);
                    myHolder.radioButton2.setVisibility(View.VISIBLE);
                    myHolder.radioButton3.setVisibility(View.VISIBLE);
                    myHolder.radioButton4.setVisibility(View.VISIBLE);
                    myHolder.radioButton5.setVisibility(View.VISIBLE);

                    myHolder.radioButton1.setText(((List<String>)listData.get(i).get("choices")).get(0));
                    myHolder.radioButton2.setText(((List<String>)listData.get(i).get("choices")).get(1));
                    myHolder.radioButton3.setText(((List<String>)listData.get(i).get("choices")).get(2));
                    myHolder.radioButton4.setText(((List<String>)listData.get(i).get("choices")).get(3));
                    myHolder.radioButton5.setText(((List<String>)listData.get(i).get("choices")).get(4));
                    break;
                default:
                    myHolder.radioButton1.setVisibility(View.GONE);
                    myHolder.radioButton2.setVisibility(View.GONE);
                    myHolder.radioButton3.setVisibility(View.GONE);
                    myHolder.radioButton4.setVisibility(View.GONE);
                    myHolder.radioButton5.setVisibility(View.GONE);
                    break;
            }

            if(i==listSelect){

                switch(Integer.parseInt((String)listData.get(listSelect).get("selectIndex"))){

                    case 0:
                        myHolder.radioGroup.check(R.id.radio1);
                        myHolder.radioButton1.setBackgroundColor(Color.parseColor("#00FF00"));
                        myHolder.radioButton2.setBackgroundColor(Color.TRANSPARENT);
                        myHolder.radioButton3.setBackgroundColor(Color.TRANSPARENT);
                        myHolder.radioButton4.setBackgroundColor(Color.TRANSPARENT);
                        myHolder.radioButton5.setBackgroundColor(Color.TRANSPARENT);
                        break;
                    case 1:
                        myHolder.radioGroup.check(R.id.radio2);
                        myHolder.radioButton1.setBackgroundColor(Color.TRANSPARENT);
                        myHolder.radioButton2.setBackgroundColor(Color.parseColor("#00FF00"));
                        myHolder.radioButton3.setBackgroundColor(Color.TRANSPARENT);
                        myHolder.radioButton4.setBackgroundColor(Color.TRANSPARENT);
                        myHolder.radioButton5.setBackgroundColor(Color.TRANSPARENT);
                        break;
                    case 2:
                        myHolder.radioGroup.check(R.id.radio3);
                        myHolder.radioButton1.setBackgroundColor(Color.TRANSPARENT);
                        myHolder.radioButton2.setBackgroundColor(Color.TRANSPARENT);
                        myHolder.radioButton3.setBackgroundColor(Color.parseColor("#00FF00"));
                        myHolder.radioButton4.setBackgroundColor(Color.TRANSPARENT);
                        myHolder.radioButton5.setBackgroundColor(Color.TRANSPARENT);
                        break;
                    case 3:
                        myHolder.radioGroup.check(R.id.radio4);
                        myHolder.radioButton1.setBackgroundColor(Color.TRANSPARENT);
                        myHolder.radioButton2.setBackgroundColor(Color.TRANSPARENT);
                        myHolder.radioButton3.setBackgroundColor(Color.TRANSPARENT);
                        myHolder.radioButton4.setBackgroundColor(Color.parseColor("#00FF00"));
                        myHolder.radioButton5.setBackgroundColor(Color.TRANSPARENT);
                        break;
                    case 4:
                        myHolder.radioGroup.check(R.id.radio5);
                        myHolder.radioButton1.setBackgroundColor(Color.TRANSPARENT);
                        myHolder.radioButton2.setBackgroundColor(Color.TRANSPARENT);
                        myHolder.radioButton3.setBackgroundColor(Color.TRANSPARENT);
                        myHolder.radioButton4.setBackgroundColor(Color.TRANSPARENT);
                        myHolder.radioButton5.setBackgroundColor(Color.parseColor("#00FF00"));
                        break;

                }


            }

            return view;
        }
    }

    private void setRadioButtonDrawable(RadioButton radioButton){

        Drawable drawableAdd = getResources().getDrawable(R.drawable.radiobutton_selector);
        drawableAdd.setBounds(0, 0,50,50);
        radioButton.setCompoundDrawables(drawableAdd, null, null, null);

    }

    class MyHolder {
        TextView remarkTex;
        RadioGroup radioGroup;
        RadioButton radioButton1;
        RadioButton radioButton2;
        RadioButton radioButton3;
        RadioButton radioButton4;
        RadioButton radioButton5;
    }

}

