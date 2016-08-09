package com.android.tvvideo.activity;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
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
import com.android.tvvideo.model.VideoModel;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.tools.ImageLoad;
import com.android.tvvideo.tools.SystemUtil;
import com.android.tvvideo.view.ReFousListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VideoSelectActivity extends BaseActivity {

    Context context;

    ReFousListView listView;

    int indexList;

    ListAdapter listAdapter;

    List<Map<String,String>> listData=new ArrayList<>();

    GridView gridView;

    GridAdapter gridAdapter;

    List<VideoModel> gridData=new ArrayList<>();

    List<VideoModel> gridAllData=new ArrayList<>();

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

        setContentView(R.layout.activity_video_select);

        context=this;

        initView();

        getVideoMenus();

    }

    private void getVideoMenus(){

        JSONObject postData=new JSONObject();
        try {
            postData.put("ipaddress", SystemUtil.getLocalHostIp());

            new NetDataTool(this).sendPost(NetDataConstants.GET_VIDEO_KIND_LIST,postData.toString(), new NetDataTool.IResponse() {
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getVideoList(String menuId){

        new NetDataTool(this).sendGet(NetDataConstants.GET_VIDEO_LIST_FROM_KIND+"/"+menuId, new NetDataTool.IResponse() {
            @Override
            public void onSuccess(String data) {

                gridAllData.clear();

                try {

                    JSONArray array=new JSONArray(data.toString());

                    for(int i=0;i<array.length();i++){
                        JSONObject jsonObject=array.getJSONObject(i);

                        VideoModel videoModel=new VideoModel();

                        videoModel.setImgUrl(jsonObject.getString("imgPath"));
                        videoModel.setTitle(jsonObject.getString("name"));
                        videoModel.setRemark(jsonObject.getString("remark"));
                        videoModel.setVideoUrl(jsonObject.getString("videoPath"));

                        gridAllData.add(videoModel);

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

    List<VideoModel> getIndexPageData(int index, int pageSize){

        List<VideoModel> datas=new ArrayList<>();

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
                getVideoList(listData.get(indexList).get("id"));
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

                String url=gridData.get(i).getVideoUrl();

                Uri uri= Uri.parse(url);

               // VideoPlayerActivity.start(VideoSelectActivity.this,uri);

            }
        });


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
                view = LayoutInflater.from(context).inflate(R.layout.item_video_list,null);
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
                view = LayoutInflater.from(context).inflate(R.layout.item_video_grid,null);
                holder.tex = (TextView)view.findViewById(R.id.video_tex);
                holder.img=(ImageView)view.findViewById(R.id.video_img);
                view.setTag(holder);
            } else {
                holder = (MyGridHolder)view.getTag();
            }

            ImageLoad.loadDefultImage(gridData.get(i).getImgUrl(),holder.img);

            holder.tex.setText(gridData.get(i).getTitle());

            return view;
        }
    }

    class MyGridHolder{
        ImageView img;
        TextView tex;
    }

}
