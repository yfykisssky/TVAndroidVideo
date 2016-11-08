package com.android.tvvideo.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.model.OrderModel;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.tools.ImageLoad;
import com.android.tvvideo.view.ChoiceMealDialog;
import com.android.tvvideo.view.ReFousListView;

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
public class OrderMealActivity extends BaseActivity {

    Context context;

    ReFousListView listView;

    int indexList;

    ListAdapter listAdapter;

    List<Map<String,String>> listData=new ArrayList<>();

    GridView gridView;

    GridAdapter gridAdapter;

    List<OrderModel> gridData=new ArrayList<>();

    List<OrderModel> gridAllData=new ArrayList<>();

    int pageAll=0;

    int pageIndex=0;

    final int pageSize=8;

    Button upBnt;

    Button downBnt;

    TextView allTex;

    TextView indexTex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_order_meal);

        context=this;

        initView();

        getOrderKinds();

    }

    private void getOrderKinds(){

        new NetDataTool(this).sendGet(NetDataConstants.GET_MEAL_KINDS, new NetDataTool.IResponse() {
            @Override
            public void onSuccess(String data) {

                try {

                    JSONArray array=new JSONArray(data.toString());

                    for(int i=0;i<array.length();i++){

                        JSONObject jsonObject=array.getJSONObject(i);

                        Map<String,String> map=new HashMap<String, String>();

                        map.put("name",jsonObject.getString("name"));

                        map.put("id",jsonObject.getString("id"));

                        listData.add(map);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailed(String error) {
                showToast(error);
            }
        });

    }

    private void getOrderList(String menuId){

        new NetDataTool(this).sendGet(NetDataConstants.GET_MEAL_LIST+"/"+menuId, new NetDataTool.IResponse() {
            @Override
            public void onSuccess(String data) {

                gridAllData.clear();

                try {

                    JSONArray array=new JSONArray(data.toString());

                    for(int i=0;i<array.length();i++){
                        JSONObject jsonObject=array.getJSONObject(i);

                        OrderModel model=new OrderModel();

                        model.setImgUrl(jsonObject.getString("path"));
                        model.setName(jsonObject.getString("name"));
                        model.setRemark(jsonObject.getString("remark"));
                        model.setPrice(jsonObject.getString("price"));
                        model.setId(jsonObject.getString("foodId"));

                        gridAllData.add(model);

                    }

                    pageIndex=0;

                    pageAll=0;

                    gridData=getIndexPageData(pageIndex,pageSize);

                    pageAll=gridAllData.size()/pageSize;

                    if(gridAllData.size()%pageSize!=0){
                        pageAll++;
                    }

                    allTex.setText(String.valueOf(pageAll));

                    gridAdapter.notifyDataSetChanged();

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

    List<OrderModel> getIndexPageData(int index, int pageSize){

        List<OrderModel> datas=new ArrayList<>();

        int start=index*pageSize;

        int end=(index+1)*pageSize;

        if(gridAllData.size()>=end){
            datas=gridAllData.subList(start,end);
        }else{
            datas=gridAllData.subList(start,gridAllData.size());
        }

        indexTex.setText(String.valueOf(pageIndex+1));

        return datas;

    }

    private void initView() {

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
                gridData=getIndexPageData(pageIndex,pageSize);
                gridAdapter.notifyDataSetChanged();
            }
        });

        downBnt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(pageIndex<pageAll-1){
                    pageIndex++;
                    gridData=getIndexPageData(pageIndex,pageSize);
                    gridAdapter.notifyDataSetChanged();
                }

            }
        });

        listView=(ReFousListView)findViewById(R.id.list);

        indexList=0;

        listAdapter=new ListAdapter();

        listView.setAdapter(listAdapter);

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                listAdapter.notifyDataSetChanged();
                indexList=i;
                gridData.clear();
                gridAdapter.notifyDataSetChanged();
                getOrderList(listData.get(indexList).get("id"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        gridView=(GridView)findViewById(R.id.grid);

        gridView.setNumColumns(4);

        gridAdapter=new GridAdapter();

        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                OrderConfirm(gridData.get(i), VLCApplication.getInstance().getPatientNum(),VLCApplication.getInstance().getPatientPhoneNum());

            }
        });

    }

    private void OrderConfirm(OrderModel model,String inHospitalNum,String phoneNum) {

        ChoiceMealDialog dialog=new ChoiceMealDialog(this);

        dialog.setData(model.getName(),model.getPrice(),model.getRemark(),model.getImgUrl());

        dialog.setUserData(model.getId(),inHospitalNum,phoneNum);

        dialog.show();

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
                view = LayoutInflater.from(context).inflate(R.layout.item_meal_list,null);
                holder.tex = (TextView)view.findViewById(R.id.text);
                view.setTag(holder);
            } else {
                holder = (MyListHolder)view.getTag();
            }

            holder.tex.setText(listData.get(i).get("name"));

            if(indexList==i){
                view.setBackgroundColor(Color.parseColor("#ccFF0000"));
            }else{
                view.setBackgroundColor(Color.parseColor("#cc000000"));
            }

            return view;
        }
    }

    class MyListHolder{
        TextView tex;
    }

    class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return gridData.size();
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
                view = LayoutInflater.from(context).inflate(R.layout.item_meal_grid,null);
                holder.img=(ImageView)view.findViewById(R.id.img);
                holder.tex = (TextView)view.findViewById(R.id.tex);
                holder.price = (TextView)view.findViewById(R.id.price);
                holder.remark=(TextView)view.findViewById(R.id.remark);
                view.setTag(holder);
            } else {
                holder = (MyGridHolder)view.getTag();
            }

            ImageLoad.loadDefultImage(gridData.get(i).getImgUrl(),holder.img);

            holder.tex.setText(gridData.get(i).getName());
            holder.price.setText("￥"+gridData.get(i).getPrice());
            holder.remark.setText(gridData.get(i).getRemark());

            return view;
        }
    }

    class MyGridHolder{
        ImageView img;
        TextView tex;
        TextView price;
        TextView remark;
    }

}
