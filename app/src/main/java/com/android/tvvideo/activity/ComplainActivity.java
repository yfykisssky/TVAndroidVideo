package com.android.tvvideo.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.view.ComplainDialog;
import com.android.tvvideo.view.SmoothGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangfengyuan on 16/8/5.
 */
public class ComplainActivity extends BaseActivity {

    Context context;

    SmoothGridView smoothGridView;

    MyAdapter myAdapter;

    final int NUM_COLUMNS=5;

    List<Map<String,String>> datas=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_complain_select);

        context=this;

        initView();

        getComplainList();

    }

    private void initView() {

        smoothGridView=(SmoothGridView)findViewById(R.id.select);

        smoothGridView.setNumColumns(NUM_COLUMNS);

        myAdapter=new MyAdapter();

        smoothGridView.setAdapter(myAdapter);

        smoothGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                ComplainDialog complainDialog=new ComplainDialog(context);

                complainDialog.setData(datas.get(i).get("id"),datas.get(i).get("kind"));

                complainDialog.show();

                //sendComplain();

            }
        });


    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datas.size();
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
        public View getView(int i, View convertView, ViewGroup viewGroup) {

            ViewHolder viewHolder;
            if (convertView == null)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_complain_grid,null);
                viewHolder = new ViewHolder();
                viewHolder.headImg=(ImageView) convertView.findViewById(R.id.head);
                viewHolder.nameTex=(TextView) convertView.findViewById(R.id.name);
                viewHolder.postTex=(TextView) convertView.findViewById(R.id.post);
                convertView.setTag(viewHolder);
            } else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
//            ImageLoad.loadDefultImage(datas.get(i).get("path"),viewHolder.headImg);
            viewHolder.nameTex.setText(datas.get(i).get("name"));
            viewHolder.postTex.setText(datas.get(i).get("title"));
            return convertView;
        }

    }

    class ViewHolder{

        ImageView headImg;
        TextView nameTex;
        TextView postTex;

    }

    private void getComplainList() {

        JSONObject postData=new JSONObject();
        try {
            //postData.put("ipaddress", SystemUtil.getLocalHostIp());
            //postData.put("hospitalId", VLCApplication.getInstance().getPatientNum());
            //postData.put("mobile",VLCApplication.getInstance().getPatientPhoneNum());
            postData.put("ipaddress","192.168.2.117");
            postData.put("hospitalId","123456");
            postData.put("mobile","15936046693");

            new NetDataTool(this).sendPost(NetDataConstants.GET_COMPLAIN_LIST,postData.toString(), new NetDataTool.IResponse() {
                @Override
                public void onSuccess(String data) {

                    try {

                        JSONObject jsonObject=new JSONObject(data);

                        JSONArray nurses=jsonObject.getJSONArray("nurses");

                        JSONArray doctors=jsonObject.getJSONArray("doctors");

                        for(int v=0;v<doctors.length();v++){

                            Map<String,String> map=new HashMap<String, String>();

                            map.put("name",doctors.getJSONObject(v).getString("name"));
                            map.put("title",doctors.getJSONObject(v).getString("title"));
                            map.put("path",doctors.getJSONObject(v).getString("path"));
                            map.put("kind","doctors");
                            map.put("id",doctors.getJSONObject(v).getString("id"));

                            datas.add(map);

                        }

                        for(int v=0;v<nurses.length();v++){

                            Map<String,String> map=new HashMap<String, String>();

                            map.put("name",nurses.getJSONObject(v).getString("name"));
                            map.put("title",nurses.getJSONObject(v).getString("title"));
                            map.put("path",nurses.getJSONObject(v).getString("path"));
                            map.put("kind","nurses");
                            map.put("id",doctors.getJSONObject(v).getString("id"));

                            datas.add(map);

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

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

