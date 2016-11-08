package com.android.tvvideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.CustomViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.video.TVPlayerActivity;
import com.android.tvvideo.view.ValidateDialog;
import com.android.tvvideo.view.page.Image3DModel;
import com.android.tvvideo.view.page.PagerViewAdapter;
import com.android.tvvideo.view.page.ZoomCardPageTransformer;

import org.videolan.libvlc.VLCApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class HomeActivity extends BaseActivity {

    int index=0;
    TextView titleTex;

    final String[] titles={"医院介绍","科室介绍","住院告知","健康宣教","电视直播","日常服务","患者服务","投诉建议","系统设置"};

    final int[] pageViewIds={R.drawable.hosipital,R.drawable.exams,R.drawable.inhospital,R.drawable.teach,R.drawable.tv,R.drawable.server,R.drawable.hospitalserve,R.drawable.feedback,R.drawable.settings};

    CustomViewPager customViewPager;

    private PagerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_home);

        initUseData();

        titleTex=(TextView)findViewById(R.id.title);

        customViewPager= (CustomViewPager) findViewById(R.id.view_pager);

        adapter = new PagerViewAdapter(this);

        for(int x=0;x<pageViewIds.length;x++){
            adapter.add(new Image3DModel(pageViewIds[x]));
        }

        customViewPager.setPageTransformer(true, new ZoomCardPageTransformer(customViewPager,true));
        customViewPager.setOffscreenPageLimit(12);

        customViewPager.setAdapter(adapter);

        customViewPager.setCurrentItem(index);

        customViewPager.setOnPageChangeListener(new CustomViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                index=position;

                titleTex.setText(titles[position]);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        customViewPager.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                final ValidateDialog validateDialog=new ValidateDialog(HomeActivity.this);

                if(keyEvent.getKeyCode()== KeyEvent.KEYCODE_DPAD_CENTER){

                    switch (index){
                        case 0:
                            toHospitIntro();
                            break;
                        case 1:
                            toExam();
                            break;
                        case 2:
                            toInHospital();
                            break;
                        case 3:
                            toTeach();
                            break;
                        case 4:
                            toTV();
                            break;
                        case 5:

                            validateDialog.setValidateListener(new ValidateDialog.ValidateListener() {
                                @Override
                                public void validate(boolean result) {
                                    if(result){
                                        validateDialog.dismiss();
                                        toServer();
                                    }else{
                                        showToast("号码错误");
                                    }
                                }
                            });

                            validateDialog.show();

                            break;
                        case 6:

                            validateDialog.setValidateListener(new ValidateDialog.ValidateListener() {
                                @Override
                                public void validate(boolean result) {
                                    if(result){
                                        validateDialog.dismiss();
                                        toServerInHospital();
                                    }else{
                                        showToast("号码错误");
                                    }
                                }
                            });

                            validateDialog.show();

                            break;
                        case 7:

                            validateDialog.setValidateListener(new ValidateDialog.ValidateListener() {
                                @Override
                                public void validate(boolean result) {
                                    if(result){
                                        validateDialog.dismiss();
                                        toComplainAndAdvice();
                                    }else{
                                        showToast("号码错误");
                                    }
                                }
                            });

                            validateDialog.show();

                            break;

                        case 8:
                            toSettings();
                            break;
                    }

                }

                return false;
            }
        });

    }

    private void checkTime(){

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long checkTime=sdf.parse("2017-3-1 00:00:00").getTime();
            long time = System.currentTimeMillis();

            if(checkTime<time){
                throw new IllegalStateException("unknown error,restart again");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void initUseData() {

        checkTime();

        VLCApplication.getInstance().initPushService();

        VLCApplication.getInstance().resetMaxVolumeTimer();

        VLCApplication.getInstance().resetOnOffTimer();

    }

    private void toExam(){
        Intent intent=new Intent(this,ExamIntroActivity.class);
        startActivity(intent);
    }

    private void toHospitIntro(){
        Intent intent=new Intent(this,HospitIntroActivity.class);
        startActivity(intent);
    }

    private void toInHospital(){
        Intent intent=new Intent(this,InHospitalActivity.class);
        startActivity(intent);
    }

    private void toTeach(){
        Intent intent=new Intent(this,VideoSelectActivity.class);
        startActivity(intent);
    }

    private void toTV(){
        Intent intent=new Intent(this, TVPlayerActivity.class);
        startActivity(intent);
    }

    private void toServer(){
        Intent intent=new Intent(this,ServerActivity.class);
        startActivity(intent);
    }

    private void toServerInHospital(){
        Intent intent=new Intent(this,ServerInHospitalActivity.class);
        startActivity(intent);
    }

    private void toComplainAndAdvice(){
        Intent intent=new Intent(this,ComplainAndAdviceActivity.class);
        startActivity(intent);
    }

    private void toSettings(){
        Intent intent=new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

}
