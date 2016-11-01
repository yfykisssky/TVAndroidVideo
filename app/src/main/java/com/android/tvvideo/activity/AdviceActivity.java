package com.android.tvvideo.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by yfykisssky on 16/8/4.
 */
public class AdviceActivity extends BaseActivity {

    Context context;

    EditText searchEdit;

    Button searchBnt;

    ListView listView;

    ListAdapter listAdapter;

    List<Map<String,String>> listData=new ArrayList<>();

    List<Map<String,String>> listAllData=new ArrayList<>();

    int pageAll=0;

    int pageIndex=0;

    final int pageSize=9;

    Button upBnt;

    Button downBnt;

    TextView allTex;

    TextView indexTex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_advice);

        context=this;

        initView();

        searchData();

    }

    private void searchData() {

        listAllData.clear();

        JSONObject postData=new JSONObject();
        try {
            postData.put("ipaddress", SystemUtil.getLocalHostIp());
            postData.put("hospitalId", VLCApplication.getInstance().getPatientNum());
            postData.put("mobile",VLCApplication.getInstance().getPatientPhoneNum());

            new NetDataTool(this).sendPost(NetDataConstants.GET_ADVICE,postData.toString(),new NetDataTool.IResponse() {
                @Override
                public void onSuccess(String data) {

                    if(TextUtils.isEmpty(data)){

                        showToast("获取数据失败");

                        return;

                    }

                    try {

                        JSONArray array=new JSONArray(data);

                        for(int b=0;b<array.length();b++){

                            JSONObject jsonObject=array.getJSONObject(b);

                            Map<String,String> map=new HashMap<>();

                            map.put("kind",jsonObject.getString("kind"));
                            map.put("name",jsonObject.getString("name"));
                            map.put("use",jsonObject.getString("use"));
                            map.put("usetime",jsonObject.getString("usetime"));
                            map.put("unit",jsonObject.getString("unit"));
                            map.put("usequlaty",jsonObject.getString("usequlaty"));
                            map.put("useunit",jsonObject.getString("useunit"));

                            listAllData.add(map);

                        }

                        listData=getIndexPageData(pageIndex,pageSize);

                        listAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailed(String error) {

                    Toast.makeText(context,error,Toast.LENGTH_SHORT).show();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        pageIndex=0;

        pageAll=0;

        listData=getIndexPageData(pageIndex,pageSize);

        pageAll=listAllData.size()/pageSize;

        if(listAllData.size()%pageSize!=0){
            pageAll++;
        }

        allTex.setText(String.valueOf(pageAll));

        listAdapter.notifyDataSetChanged();


    }

    private void initView() {

        listView=(ListView)findViewById(R.id.advicelist);

        listAdapter=new ListAdapter();

        listView.setAdapter(listAdapter);

        allTex=(TextView)findViewById(R.id.all);

        indexTex=(TextView)findViewById(R.id.index);

        upBnt=(Button)findViewById(R.id.up);

        downBnt=(Button)findViewById(R.id.down);

        upBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pageIndex==0){
                    return;
                }

                pageIndex--;
                listData=getIndexPageData(pageIndex,pageSize);
                listAdapter.notifyDataSetChanged();
            }
        });

        downBnt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(pageIndex<pageAll-1){
                    pageIndex++;
                    listData=getIndexPageData(pageIndex,pageSize);
                    listAdapter.notifyDataSetChanged();
                }

            }
        });

        searchEdit=(EditText)findViewById(R.id.data);

        searchBnt=(Button)findViewById(R.id.search);

        searchBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String data=searchEdit.getText().toString();

                if(TextUtils.isEmpty(data)){
                    showToast("搜索项不能为空");
                }else{
                    searchData();
                }

            }
        });

    }

    List<Map<String,String>> getIndexPageData(int index, int pageSize){

        List<Map<String,String>> datas=new ArrayList<>();

        int start=index*pageSize;

        int end=(index+1)*pageSize;

        if(listAllData.size()>=end){
            datas=listAllData.subList(start,end);
        }else{
            datas=listAllData.subList(start,listAllData.size());
        }

        indexTex.setText(String.valueOf(pageIndex+1));

        return datas;

    }

    class ListAdapter extends BaseAdapter {

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
            MyListHolder holder = new MyListHolder();
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.item_advice_list,null);
                holder.kind = (TextView)view.findViewById(R.id.kind);
                holder.name = (TextView)view.findViewById(R.id.name);
                holder.use = (TextView)view.findViewById(R.id.use);
                holder.usetime = (TextView)view.findViewById(R.id.usetime);
                holder.usequlaty = (TextView)view.findViewById(R.id.usequlaty);
                holder.useunit = (TextView)view.findViewById(R.id.useunit);
                view.setTag(holder);
            } else {
                holder = (MyListHolder)view.getTag();
            }

            holder.kind.setText(listData.get(i).get("kind"));
            holder.name.setText(listData.get(i).get("name"));
            holder.use.setText(listData.get(i).get("use"));
            holder.usetime.setText(listData.get(i).get("usetime"));
            holder.usequlaty.setText(listData.get(i).get("usequlaty"));
            holder.useunit.setText(listData.get(i).get("useunit"));

            return view;
        }
    }

    class MyListHolder{
        TextView kind;
        TextView name;
        TextView use;
        TextView usetime;
        TextView usequlaty;
        TextView useunit;
    }


}
