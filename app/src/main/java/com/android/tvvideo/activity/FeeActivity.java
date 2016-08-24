package com.android.tvvideo.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.view.TimeEditDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yfykisssky on 16/8/4.
 */
public class FeeActivity extends BaseActivity {

    Context context;

    Button startTimeBnt;

    Button endTimeBnt;

    String startTime;

    String endTime;

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

        setContentView(R.layout.activity_fee);

        context=this;

        initView();

        searchData();

    }

    private void initView() {

        listView=(ListView)findViewById(R.id.feelist);

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


        startTimeBnt=(Button)findViewById(R.id.startTime);

        startTimeBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimeEditDialog timeEditDialog=new TimeEditDialog(FeeActivity.this);

                timeEditDialog.setTimeChangeListener(new TimeEditDialog.TimeChangeListener() {
                    @Override
                    public void change(String date) {
                        startTime=date;
                        startTimeBnt.setText(date);
                    }
                });

                timeEditDialog.show();

            }
        });

        endTimeBnt=(Button)findViewById(R.id.endTime);

        endTimeBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimeEditDialog timeEditDialog=new TimeEditDialog(FeeActivity.this);

                timeEditDialog.setTimeChangeListener(new TimeEditDialog.TimeChangeListener() {
                    @Override
                    public void change(String date) {
                        endTime=date;
                        endTimeBnt.setText(date);
                    }
                });

                timeEditDialog.show();

            }
        });

        searchBnt=(Button)findViewById(R.id.search);

        searchBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(startTime)||TextUtils.isEmpty(endTime)){
                    showToast("时间不能为空");
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
                view = LayoutInflater.from(context).inflate(R.layout.item_fee_list,null);
                holder.date = (TextView)view.findViewById(R.id.date);
                holder.projectnum = (TextView)view.findViewById(R.id.projectnum);
                holder.projectname = (TextView)view.findViewById(R.id.projectname);
                holder.quality = (TextView)view.findViewById(R.id.quality);
                holder.unit = (TextView)view.findViewById(R.id.unit);
                holder.unitprice = (TextView)view.findViewById(R.id.unitprice);
                holder.price= (TextView)view.findViewById(R.id.price);
                view.setTag(holder);
            } else {
                holder = (MyListHolder)view.getTag();
            }

            holder.date.setText(listData.get(i).get("date"));
            holder.projectnum.setText(listData.get(i).get("projectnum"));
            holder.projectname.setText(listData.get(i).get("projectname"));
            holder.quality.setText(listData.get(i).get("quality"));
            holder.unit.setText(listData.get(i).get("unit"));
            holder.unitprice.setText(listData.get(i).get("unitprice"));
            holder.price.setText(listData.get(i).get("price"));

            return view;
        }
    }

    class MyListHolder{
        TextView date;
        TextView projectnum;
        TextView projectname;
        TextView quality;
        TextView unit;
        TextView unitprice;
        TextView price;
    }

    private void searchData() {

        listAllData.clear();

        for(int b=0;b<20;b++){

            Map<String,String> map=new HashMap<>();

            map.put("date","2016-12-12");
            map.put("projectnum",String.valueOf(b));
            map.put("projectname",String.valueOf(b));
            map.put("quality",String.valueOf(b));
            map.put("unit",String.valueOf(b));
            map.put("unitprice",String.valueOf(b));
            map.put("price",String.valueOf(b));

            listAllData.add(map);

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

}
