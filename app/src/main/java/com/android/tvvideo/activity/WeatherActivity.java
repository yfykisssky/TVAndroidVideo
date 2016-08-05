package com.android.tvvideo.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.model.WeatherModel;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends BaseActivity {

    ListView listView;

    TextView timeTex;

    int pageIndex;

    Button upBnt;
    Button downBnt;

    MyListAdapter myAdapter;

    Context context;

    List<WeatherModel> listData=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_weather);

        context=this;

        initView();

        getWeatherData(pageIndex);

    }

    private void getWeatherData(int index) {


        new NetDataTool(this).sendGet(NetDataConstants.GET_VERSION, new NetDataTool.IResponse() {

            @Override
            public void onSuccess(String data) {

                try {
                    JSONObject jsonObject=new JSONObject(data);

                    String version=jsonObject.getString("version");
                    String url=jsonObject.getString("address");

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

    private void initView() {

        timeTex=(TextView)findViewById(R.id.time);

        upBnt=(Button)findViewById(R.id.up);

        upBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageIndex--;
                getWeatherData(pageIndex);
            }
        });

        downBnt=(Button)findViewById(R.id.down);

        downBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageIndex++;
                getWeatherData(pageIndex);
            }
        });

        listView=(ListView) findViewById(R.id.list);

        myAdapter=new MyListAdapter();

        listView.setAdapter(myAdapter);

    }

    class MyListAdapter extends BaseAdapter{

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
        public View getView(int i, View convertView, ViewGroup viewGroup) {

            ViewHolder viewHolder;
            if (convertView == null)
            {
                convertView= LayoutInflater.from(context).inflate(R.layout.item_weather_list,null);
                viewHolder = new ViewHolder();
                viewHolder.cityTex=(TextView) convertView.findViewById(R.id.city);
                viewHolder.weatherTex=(TextView) convertView.findViewById(R.id.weather);
                viewHolder.temperatureTex=(TextView) convertView.findViewById(R.id.temperature);
                viewHolder.humidityTex=(TextView) convertView.findViewById(R.id.humidity);
                convertView.setTag(viewHolder);
            } else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.cityTex.setText(listData.get(i).getCity());
            viewHolder.weatherTex.setText(listData.get(i).getWeather());
            viewHolder.temperatureTex.setText(listData.get(i).getTemperature());
            viewHolder.humidityTex.setText(listData.get(i).getHumidity());
            return convertView;
        }
    }

    class ViewHolder{

        TextView cityTex;

        TextView weatherTex;

        TextView temperatureTex;

        TextView humidityTex;
    }

}
