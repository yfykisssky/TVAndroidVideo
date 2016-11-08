package com.android.tvvideo.activity;

import android.content.Context;
import android.content.Intent;
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
import com.android.tvvideo.model.DoctorModel;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.tools.ImageLoad;
import com.android.tvvideo.view.SmoothGridView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfengyuan on 16/7/18.
 */
public class DoctorsSelectActivity extends BaseActivity {

    Context context;

    String examRoomId;

    SmoothGridView smoothGridView;

    MyAdapter myAdapter;

    final int NUM_COLUMNS=5;

    List<DoctorModel> datas=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_doctors_select);

        context=this;

        initData();

        initView();

        getDataFromNet();

    }

    private void initData() {

        examRoomId=getIntent().getStringExtra("exam_room_id");

    }

    private void initView() {

        smoothGridView=(SmoothGridView)findViewById(R.id.select);

        smoothGridView.setNumColumns(NUM_COLUMNS);

        myAdapter=new MyAdapter();

        smoothGridView.setAdapter(myAdapter);

        smoothGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(DoctorsSelectActivity.this,DoctorDetialActivity.class);

                intent.putExtra("name",datas.get(i).getName());
                intent.putExtra("post",datas.get(i).getTitle());
                intent.putExtra("goodat",datas.get(i).getSpecialty());
                intent.putExtra("time",datas.get(i).getVisitTime());
                intent.putExtra("detial",datas.get(i).getRemark());

                intent.putExtra("path",datas.get(i).getPath());

                startActivity(intent);
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
                convertView = LayoutInflater.from(context).inflate(R.layout.item_doctors_select,null);
                viewHolder = new ViewHolder();
                viewHolder.headImg=(ImageView) convertView.findViewById(R.id.head);
                viewHolder.nameTex=(TextView) convertView.findViewById(R.id.name);
                viewHolder.postTex=(TextView) convertView.findViewById(R.id.post);
                convertView.setTag(viewHolder);
            } else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ImageLoad.loadDefultImage(datas.get(i).getPath(),viewHolder.headImg);
            viewHolder.nameTex.setText(datas.get(i).getName());
            viewHolder.postTex.setText(datas.get(i).getTitle());
            return convertView;
        }

    }

    class ViewHolder{

        ImageView headImg;
        TextView nameTex;
        TextView postTex;

    }


    private void getDataFromNet() {

        new NetDataTool(this).sendGet(NetDataConstants.GET_DOCTOR_LIST+"/"+examRoomId,new NetDataTool.IResponse() {
            @Override
            public void onSuccess(String data) {

                try {

                    JSONArray arrays=new JSONArray(data);

                    for(int v=0;v<arrays.length();v++){

                        DoctorModel doctorModel=new DoctorModel();

                        doctorModel.setRemark(arrays.getJSONObject(v).getString("remark"));
                        doctorModel.setTitle(arrays.getJSONObject(v).getString("title"));
                        doctorModel.setName(arrays.getJSONObject(v).getString("name"));
                        doctorModel.setPath(arrays.getJSONObject(v).getString("path"));
                        doctorModel.setSpecialty(arrays.getJSONObject(v).getString("specialty"));
                        doctorModel.setVisitTime(arrays.getJSONObject(v).getString("visitTime"));

                        datas.add(doctorModel);

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

}
