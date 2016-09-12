package com.android.tvvideo.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.net.NetDataConstants;
import com.android.tvvideo.net.NetDataTool;
import com.android.tvvideo.tools.ImageLoad;
import com.android.tvvideo.tools.SystemUtil;
import com.android.tvvideo.tools.TimerTaskHelper;
import com.android.tvvideo.view.LoadingDialog;
import com.android.tvvideo.view.MarqueeText;
import com.android.tvvideo.view.ScrollRelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.videolan.libvlc.LibVlcUtil;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.WeakHandler;
import org.videolan.libvlc.util.VLCInstance;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by yangfengyuan on 16/7/29.
 */
public class TVPlayerActivity extends BaseActivity implements IVideoPlayer{

    //############################################################################################################################################################################
    //############################################################################################################################################################################
    //############################################################################################################################################################################
    //############################################################################################################################################################################
    //############################################################################################################################################################################

    ListView listView;

    ScrollRelativeLayout relativeLayout;

    Context context;

    MyAdapter myAdapter;

    CountTimeThread countTimeThread;

    MyHandler msgHandler=new MyHandler(this);

    List<Map<String,String>> listData=new ArrayList<>();

    int indexList;

    WebView adWebView;

    List<String> adData=new ArrayList<>();

    MarqueeText showMsgTex;

    List<String> msgTex=new ArrayList<>();

    TimerTaskHelper msgTimerTaskHelper;

    TimerTaskHelper adTimerTaskHelper;

    RelativeLayout playRelative;

    final int changeWidth=1440;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        setContentView(R.layout.activity_tvplayer);

        initVideoPlayer();

        context = this;

        initView();

        initData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        destoryTimer();

        destroyVideo();
    }


    private void getShowMsg() {

        JSONObject postData = new JSONObject();
        try {
            postData.put("ipaddress", SystemUtil.getLocalHostIp());

            new NetDataTool(this).sendNoShowPost(NetDataConstants.GET_MSG_DATA, postData.toString(), new NetDataTool.IResponse() {
                @Override
                public void onSuccess(String data) {

                    msgTex.clear();

                    hideMsg();

                    msgTimerTaskHelper.stopAndRemove();

                    List<TimerTaskHelper.TimeModel> timeModels=new ArrayList<TimerTaskHelper.TimeModel>();

                    try {
                        JSONArray jsonArray=new JSONArray(data);

                        for(int c=0;c<jsonArray.length();c++){

                            JSONObject jsonObject=jsonArray.getJSONObject(c);

                            TimerTaskHelper.TimeModel timeModel=new TimerTaskHelper.TimeModel();

                            msgTex.add(jsonObject.getString("msgTex"));

                            timeModel.startTime=jsonObject.getString("startTime");

                            timeModel.endTime=jsonObject.getString("endTime");

                            timeModels.add(timeModel);

                        }

                        msgTimerTaskHelper.setData(timeModels);

                        startMsgListening();

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

    private void getAdData() {

        JSONObject postData = new JSONObject();
        try {
            postData.put("ipaddress", SystemUtil.getLocalHostIp());

            new NetDataTool(this).sendNoShowPost(NetDataConstants.GET_AD_DATA, postData.toString(), new NetDataTool.IResponse() {
                @Override
                public void onSuccess(String data) {

                    adData.clear();

                    hideAd();

                    adTimerTaskHelper.stopAndRemove();

                    List<TimerTaskHelper.TimeModel> timeModels = new ArrayList<TimerTaskHelper.TimeModel>();

                    try {
                        JSONArray jsonArray = new JSONArray(data);

                        for (int c = 0; c < jsonArray.length(); c++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(c);

                            TimerTaskHelper.TimeModel timeModel = new TimerTaskHelper.TimeModel();

                            adData.add(jsonObject.getString("adTex"));

                            timeModel.startTime = jsonObject.getString("startTime");

                            timeModel.endTime = jsonObject.getString("endTime");

                            timeModels.add(timeModel);

                        }

                        adTimerTaskHelper.setData(timeModels);

                        startAdListening();

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

    private void getTVListData() {

        new NetDataTool(this).sendGet(NetDataConstants.GET_TV_LIST, new NetDataTool.IResponse() {
            @Override
            public void onSuccess(String data) {

                try {
                    JSONArray array=new JSONArray(data);

                    for(int x=0;x<array.length();x++){

                        Map<String,String> map=new HashMap<String, String>();

                        map.put("imgurl",array.getJSONObject(x).getString("path"));
                        map.put("playurl",array.getJSONObject(x).getString("iptv"));
                        map.put("name",array.getJSONObject(x).getString("name"));

                        listData.add(map);
                    }

                    myAdapter.notifyDataSetChanged();

                    resetTvPlay(listData.get(0).get("playurl"));

                    startCountTimeThread();

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

    private void startAdListening(){

        adTimerTaskHelper.startAndListener("ad", new TimerTaskHelper.OnStartOrEndListener() {
            @Override
            public void onStartOrEnd(boolean startOrEnd,int index) {
                if(startOrEnd){
                    showAd(adData.get(index));
                }else{
                    hideAd();
                }
            }
        });

    }

    private void showAd(String adUrl){

        showAdFrameLocation();

        String url=SystemUtil.getServerAdPath(this)+adUrl;

        adWebView.loadUrl(url);
        adWebView.setVisibility(View.VISIBLE);
    }

    private void showAdFrameLocation(){

        playRelative.setLayoutParams(new RelativeLayout.LayoutParams(changeWidth,RelativeLayout.LayoutParams.MATCH_PARENT));

        mCurrentSize = SURFACE_BEST_FIT;
        setSurfaceSize(playRelative.getWidth(),playRelative.getHeight(),playRelative.getWidth(),playRelative.getHeight(),1,1);
    }

    private void hideAdFrameLocation(){

        playRelative.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));

        mCurrentSize = SURFACE_BEST_FIT;
        setSurfaceSize(playRelative.getWidth(),playRelative.getHeight(),playRelative.getWidth(),playRelative.getHeight(),1,1);
    }

    private void hideAd(){
        adWebView.setVisibility(View.GONE);

        hideAdFrameLocation();
    }

    private void startMsgListening(){

        msgTimerTaskHelper.startAndListener("msg", new TimerTaskHelper.OnStartOrEndListener() {
            @Override
            public void onStartOrEnd(boolean startOrEnd,int index) {
                if(startOrEnd){
                    showMsg(msgTex.get(index));
                }else{
                    hideMsg();
                }
            }
        });

    }

    private void showMsg(String msgData){

        showMsgTex.setShowText(msgData);

        showMsgTex.startFor0();

        showMsgTex.setVisibility(View.VISIBLE);
    }

    private void hideMsg(){

        showMsgTex.setVisibility(View.GONE);

    }

    private void initView() {

        playRelative=(RelativeLayout)findViewById(R.id.play_relative);

        showMsgTex=(MarqueeText)findViewById(R.id.showmsg);

        adWebView=(WebView)findViewById(R.id.adwebview);

        adWebView.getSettings().setJavaScriptEnabled(true);
        adWebView.getSettings().setAllowFileAccess(true);
        adWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        adWebView.setBackgroundColor(0);

        relativeLayout = (ScrollRelativeLayout) findViewById(R.id.relative);

        listView = (ListView) findViewById(R.id.list);

        myAdapter = new MyAdapter();

        listView.setAdapter(myAdapter);

        listView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT||keyEvent.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT){
                    return true;
                }

                return false;
            }
        });

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                indexList=i;
                myAdapter.notifyDataSetChanged();

                countTimeThread.reset();

                if (!relativeLayout.isShow()) {
                    relativeLayout.show();
                }

                String playUrl=listData.get(i).get("playurl");

                resetTvPlay(playUrl);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void initData() {

        super.setOnPushMsgListener(new PushMsgListener() {
            @Override
            public void onMsgReceive(Intent data) {

                String kind=data.getStringExtra("kind");

                if(kind.equals("adchange")){

                    getAdData();

                }

                if(kind.equals("showchange")){

                    getShowMsg();

                }

            }
        });

        msgTimerTaskHelper=new TimerTaskHelper(this);

        adTimerTaskHelper=new TimerTaskHelper(this);

        getTVListData();

        getAdData();

        getShowMsg();

    }

    private void destoryTimer(){
        msgTimerTaskHelper.stopAndRemove();
        adTimerTaskHelper.stopAndRemove();
    }

    private void resetTvPlay(String playUrl) {

        startLoading();

        mUri=playUrl;

        stopPlayback();

        startPlayback();

    }

    private void stopPlayback() {

        mLibVLC.stop();

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
                view = LayoutInflater.from(context).inflate(R.layout.item_tv_list, null);
                myHolder = new MyHolder();
                myHolder.indexTex = (TextView) view.findViewById(R.id.index);
                myHolder.img = (ImageView) view.findViewById(R.id.img);
                view.setTag(myHolder);
            } else {
                myHolder = (MyHolder)view.getTag();
            }

            String imgUrl=listData.get(i).get("imgurl");

            myHolder.indexTex.setText(String.valueOf(i+1));

           ImageLoad.loadDefultImage(imgUrl,myHolder.img);

            if(indexList==i){
                view.setBackgroundColor(Color.parseColor("#ccFF0000"));
            }else{
                view.setBackgroundColor(Color.parseColor("#cc000000"));
            }

            return view;
        }
    }

    class MyHolder {
        TextView indexTex;
        ImageView img;
    }

    private void startCountTimeThread() {
        countTimeThread = new CountTimeThread(3);
        countTimeThread.start();
    }


    private void hide() {
        if (relativeLayout.isShow()) {
            relativeLayout.hide();
        }
    }

    static class MyHandler extends Handler {

        private final int MSG_HIDE = 0x001;

        private WeakReference<TVPlayerActivity> weakRef;

        public MyHandler(TVPlayerActivity pMainActivity) {
            weakRef = new WeakReference<TVPlayerActivity>(pMainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            final TVPlayerActivity mainActivity = weakRef.get();

            if (mainActivity != null) {
                switch (msg.what) {
                    case MSG_HIDE:
                        mainActivity.hide();
                        break;

                }
            }
            super.handleMessage(msg);
        }

        public void sendHideControllMessage() {
            obtainMessage(MSG_HIDE).sendToTarget();
        }

    }

    class CountTimeThread extends Thread {
        private final long maxVisibleTime;
        private long startVisibleTime;

        public CountTimeThread(int second) {
            maxVisibleTime = second * 1000;

            setDaemon(true);
        }

        private synchronized void reset() {
            startVisibleTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            startVisibleTime = System.currentTimeMillis();

            while (true) {

                if (startVisibleTime + maxVisibleTime < System.currentTimeMillis()) {
                    msgHandler.sendHideControllMessage();

                    startVisibleTime = System.currentTimeMillis();
                }

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
            }

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch(keyCode){
            case KeyEvent.KEYCODE_DPAD_UP:

                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:

                break;
        }

        return super.onKeyDown(keyCode, event);
    }


    private boolean mIsLoading;
    private LoadingDialog loadingDialog;
    private void startLoading() {
        mIsLoading = true;

        loadingDialog.show();
    }

    private void stopLoading() {
        mIsLoading = false;

        loadingDialog.dismiss();
    }

    //############################################################################################################################################################################
    //############################################################################################################################################################################
    //############################################################################################################################################################################
    //############################################################################################################################################################################
    //############################################################################################################################################################################

    private String mUri;

    public final static String TAG = "VLC/VideoPlayerActivity";

    private SurfaceView mSurface;
    private SurfaceHolder mSurfaceHolder;
    private FrameLayout mSurfaceFrame;
    private LibVLC mLibVLC;
    //private String mLocation;

    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_HORIZONTAL = 1;
    private static final int SURFACE_FIT_VERTICAL = 2;
    private static final int SURFACE_FILL = 3;
    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_ORIGINAL = 6;
    private int mCurrentSize = SURFACE_BEST_FIT;

    private SharedPreferences mSettings;

    private static final int OVERLAY_TIMEOUT = 4000;
    private static final int OVERLAY_INFINITE = 3600000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private static final int SURFACE_SIZE = 3;
    private static final int AUDIO_SERVICE_CONNECTION_SUCCESS = 5;
    private static final int AUDIO_SERVICE_CONNECTION_FAILED = 6;
    private static final int FADE_OUT_INFO = 4;
    private int mUiVisibility = -1;

    // Playlist
    private int savedIndexPosition = -1;

    // size of the video
    private int mVideoHeight;
    private int mVideoWidth;
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum;
    private int mSarDen;

    // Whether fallback from HW acceleration to SW decoding was done.
    private boolean mDisabledHardwareAcceleration = false;
    private int mPreviousHardwareAccelerationMode;

    private void initVideoPlayer(){

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_tvplayer);

        loadingDialog=new LoadingDialog(this);

        if (LibVlcUtil.isICSOrLater())
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                    new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if (visibility == mUiVisibility)
                                return;
                            setSurfaceSize(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);
                          /*  if (visibility == View.SYSTEM_UI_FLAG_VISIBLE && !mShowing && !isFinishing()) {
                                //showOverlay();
                            }*/
                            mUiVisibility = visibility;
                        }
                    }
            );

        try {
            mLibVLC = VLCInstance.getLibVlcInstance();
        } catch (LibVlcException e) {
            Log.d(TAG, "LibVLC initialisation failed");
            return;
        }

        mLibVLC.setSpuTrack(-1);

        mSurface = (SurfaceView) findViewById(R.id.player_surface);
        mSurfaceHolder = mSurface.getHolder();
        mSurfaceFrame = (FrameLayout) findViewById(R.id.player_surface_frame);
        String chroma = mSettings.getString("chroma_format", "RV32");
        if(LibVlcUtil.isGingerbreadOrLater() && chroma.equals("YV12")) {
            mSurfaceHolder.setFormat(ImageFormat.YV12);
        } else if (chroma.equals("RV16")) {
            mSurfaceHolder.setFormat(PixelFormat.RGB_565);
        } else {
            mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
        }

        mSurfaceHolder.addCallback(mSurfaceCallback);

        // Signal to LibVLC that the videoPlayerActivity was created, thus the
        // SurfaceView is now available for MediaCodec direct rendering.
        mLibVLC.eventVideoPlayerActivityCreated(true);

        EventHandler em = EventHandler.getInstance();
        em.addHandler(eventHandler);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //noinspection WrongConstant
        setRequestedOrientation(getScreenOrientation());

    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
         * Pausing here generates errors because the vout is constantly
         * trying to refresh itself every 80ms while the surface is not
         * accessible anymore.
         * To workaround that, we keep the last known position in the playlist
         * in savedIndexPosition to be able to restore it during onResume().
         */
        mLibVLC.stop();

        mSurface.setKeepScreenOn(false);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onStop() {
        super.onStop();
    }

    private void destroyVideo() {

        EventHandler em = EventHandler.getInstance();
        em.removeHandler(eventHandler);

        // MediaCodec opaque direct rendering should not be used anymore since there is no surface to attach.
        mLibVLC.eventVideoPlayerActivityCreated(false);
        // HW acceleration was temporarily disabled because of an error, restore the previous value.
        if (mDisabledHardwareAcceleration)
            mLibVLC.setHardwareAcceleration(mPreviousHardwareAccelerationMode);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startPlayback() {
        loadMedia();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setSurfaceSize(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void setSurfaceSize(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den) {
        if (width * height == 0)
            return;

        // store video size
        mVideoHeight = height;
        mVideoWidth = width;
        mVideoVisibleHeight = visible_height;
        mVideoVisibleWidth  = visible_width;
        mSarNum = sar_num;
        mSarDen = sar_den;
        Message msg = mHandler.obtainMessage(SURFACE_SIZE);
        mHandler.sendMessage(msg);
    }

    /**
     * Show text in the info view for "duration" milliseconds
     * @param text
     * @param duration
     */
    private void showInfo(String text, int duration) {
   /*     mInfo.setVisibility(View.VISIBLE);
        mInfo.setText(text);*/
        mHandler.removeMessages(FADE_OUT_INFO);
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, duration);
    }

    private void showInfo(int textid, int duration) {
     /*   mInfo.setVisibility(View.VISIBLE);
        mInfo.setText(textid);*/
        mHandler.removeMessages(FADE_OUT_INFO);
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, duration);
    }

    /**
     *  Handle libvlc asynchronous events
     */
    private final Handler eventHandler = new VideoPlayerEventHandler(this);

    private static class VideoPlayerEventHandler extends WeakHandler<TVPlayerActivity> {
        public VideoPlayerEventHandler(TVPlayerActivity owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            TVPlayerActivity activity = getOwner();
            if(activity == null) return;
            // Do not handle events if we are leaving the VideoPlayerActivity
            //if (activity.mSwitchingView) return;

            switch (msg.getData().getInt("event")) {
                case EventHandler.MediaParsedChanged:
                    Log.i(TAG, "MediaParsedChanged");
                    //activity.updateNavStatus();
                 /*   if (!activity.mHasMenu && activity.mLibVLC.getVideoTracksCount() < 1) {
                        Log.i(TAG, "No video track, open in audio mode");
                        //activity.switchToAudioMode();
                    }*/
                    break;
                case EventHandler.MediaPlayerPlaying:
                    Log.i(TAG, "MediaPlayerPlaying");
                    activity.stopLoading();
                    //activity.showOverlay();
                    /** FIXME: update the track list when it changes during the
                     *  playback. (#7540) */
                    //activity.setESTrackLists(true);
                    // activity.setESTracks();
                    //activity.changeAudioFocus(true);
                    // activity.updateNavStatus();
                    break;
                case EventHandler.MediaPlayerPaused:
                    Log.i(TAG, "MediaPlayerPaused");
                    break;
                case EventHandler.MediaPlayerStopped:
                    Log.i(TAG, "MediaPlayerStopped");
                    //activity.changeAudioFocus(false);
                    break;
                case EventHandler.MediaPlayerEndReached:
                    Log.i(TAG, "MediaPlayerEndReached");
                    //activity.changeAudioFocus(false);
                    //activity.endReached();
                    break;
                case EventHandler.MediaPlayerVout:
                 /*   activity.updateNavStatus();
                    if (!activity.mHasMenu)
                        activity.handleVout(msg);*/
                    break;
                case EventHandler.MediaPlayerPositionChanged:
                 /*   if (!activity.mCanSeek)
                        activity.mCanSeek = true;*/
                    //don't spam the logs
                    break;
                case EventHandler.MediaPlayerEncounteredError:
                    Log.i(TAG, "MediaPlayerEncounteredError");
                    activity.encounteredError();
                    break;
                case EventHandler.HardwareAccelerationError:
                    Log.i(TAG, "HardwareAccelerationError");
                    activity.handleHardwareAccelerationError();
                    break;
                case EventHandler.MediaPlayerTimeChanged:
                    // avoid useless error logs
                    break;
                default:
                    Log.e(TAG, String.format("Event not handled (0x%x)", msg.getData().getInt("event")));
                    break;
            }
            //activity.updateOverlayPausePlay();
        }
    };

    /**
     * Handle resize of the surface and the overlay
     */
    private final Handler mHandler = new VideoPlayerHandler(this);

    private static class VideoPlayerHandler extends WeakHandler<TVPlayerActivity> {
        public VideoPlayerHandler(TVPlayerActivity owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            TVPlayerActivity activity = getOwner();
            if(activity == null) // WeakReference could be GC'ed early
                return;

            switch (msg.what) {
                case FADE_OUT:
                    //activity.hideOverlay(false);
                    break;
                case SHOW_PROGRESS:
               /*     int pos = activity.setOverlayProgress();
                    if (activity.canShowProgress()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;*/
                case SURFACE_SIZE:
                    activity.changeSurfaceSize();
                    break;
                case FADE_OUT_INFO:
                    //activity.fadeOutInfo();
                    break;
                case AUDIO_SERVICE_CONNECTION_SUCCESS:
                    activity.startPlayback();
                    break;
                case AUDIO_SERVICE_CONNECTION_FAILED:
                    activity.finish();
                    break;
            }
        }
    };

    private void encounteredError() {
     /*   *//* Encountered Error, exit player with a message *//*
        AlertDialog dialog = new AlertDialog.Builder(VideoPlayerActivity.this)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setTitle(R.string.encountered_error_title)
                .setMessage(R.string.encountered_error_message)
                .create();
        dialog.show();*/
    }

    private void handleHardwareAccelerationError() {
        mLibVLC.stop();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void changeSurfaceSize() {
        int sw;
        int sh;

        // get screen size
        sw = getWindow().getDecorView().getWidth();
        sh = getWindow().getDecorView().getHeight();

        double dw = sw, dh = sh;
        boolean isPortrait;

        // getWindow().getDecorView() doesn't always take orientation into account, we have to correct the values
        isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        if (sw > sh && isPortrait || sw < sh && !isPortrait) {
            dw = sh;
            dh = sw;
        }

        // sanity check
        if (dw * dh == 0 || mVideoWidth * mVideoHeight == 0) {
            Log.e(TAG, "Invalid surface size");
            return;
        }

        // compute the aspect ratio
        double ar, vw;
        if (mSarDen == mSarNum) {
            /* No indication about the density, assuming 1:1 */
            vw = mVideoVisibleWidth;
            ar = (double)mVideoVisibleWidth / (double)mVideoVisibleHeight;
        } else {
            /* Use the specified aspect ratio */
            vw = mVideoVisibleWidth * (double)mSarNum / mSarDen;
            ar = vw / mVideoVisibleHeight;
        }

        // compute the display aspect ratio
        double dar = dw / dh;

        switch (mCurrentSize) {
            case SURFACE_BEST_FIT:
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_FIT_HORIZONTAL:
                dh = dw / ar;
                break;
            case SURFACE_FIT_VERTICAL:
                dw = dh * ar;
                break;
            case SURFACE_FILL:
                break;
            case SURFACE_16_9:
                ar = 16.0 / 9.0;
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_4_3:
                ar = 4.0 / 3.0;
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_ORIGINAL:
                dh = mVideoVisibleHeight;
                dw = vw;
                break;
        }

        SurfaceView surface;
        SurfaceHolder surfaceHolder;
        FrameLayout surfaceFrame;

        surface = mSurface;
        surfaceHolder = mSurfaceHolder;
        surfaceFrame = mSurfaceFrame;

        // force surface buffer size
        surfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);

        // set display size
        ViewGroup.LayoutParams lp = surface.getLayoutParams();
        lp.width  = (int) Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
        lp.height = (int) Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);
        surface.setLayoutParams(lp);

        // set frame size (crop if necessary)
        lp = surfaceFrame.getLayoutParams();
        lp.width = (int) Math.floor(dw);
        lp.height = (int) Math.floor(dh);
        surfaceFrame.setLayoutParams(lp);

        surface.invalidate();
    }

    /**
     * attach and disattach surface to the lib
     */
    private final SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if(format == PixelFormat.RGBX_8888)
                Log.d(TAG, "Pixel format is RGBX_8888");
            else if(format == PixelFormat.RGB_565)
                Log.d(TAG, "Pixel format is RGB_565");
            else if(format == ImageFormat.YV12)
                Log.d(TAG, "Pixel format is YV12");
            else
                Log.d(TAG, "Pixel format is other/unknown");
            if(mLibVLC != null)
                mLibVLC.attachSurface(holder.getSurface(),TVPlayerActivity.this);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if(mLibVLC != null)
                mLibVLC.detachSurface();
        }
    };

    /**
     *
     */
    private void play() {
        mLibVLC.play();
        mSurface.setKeepScreenOn(true);
    }

    /**
     *
     */
    private void pause() {
        mLibVLC.pause();
        mSurface.setKeepScreenOn(false);
    }

    /**
     * External extras:
     * - position (long) - position of the video to start with (in ms)
     */
    @SuppressWarnings({ "unchecked" })
    private void loadMedia() {

        mSurface.setKeepScreenOn(true);

        if(mLibVLC == null)
            return;

        /* WARNING: hack to avoid a crash in mediacodec on KitKat.
         * Disable hardware acceleration if the media has a ts extension. */
        if (mUri!= null && LibVlcUtil.isKitKatOrLater()) {
            String locationLC =mUri.toLowerCase(Locale.ENGLISH);
            if (locationLC.endsWith(".ts")
                    || locationLC.endsWith(".tts")
                    || locationLC.endsWith(".m2t")
                    || locationLC.endsWith(".mts")
                    || locationLC.endsWith(".m2ts")) {
                mDisabledHardwareAcceleration = true;
                mPreviousHardwareAccelerationMode = mLibVLC.getHardwareAcceleration();
                mLibVLC.setHardwareAcceleration(LibVLC.HW_ACCELERATION_DISABLED);
            }
        }

        mLibVLC.setMediaList();
        mLibVLC.getMediaList().add(new Media(mLibVLC,mUri));
        savedIndexPosition = mLibVLC.getMediaList().size() - 1;
        mLibVLC.playIndex(savedIndexPosition);

    }

    @SuppressWarnings("deprecation")
    private int getScreenRotation(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO /* Android 2.2 has getRotation */) {
            try {
                Method m = display.getClass().getDeclaredMethod("getRotation");
                return (Integer) m.invoke(display);
            } catch (Exception e) {
                return Surface.ROTATION_0;
            }
        } else {
            return display.getOrientation();
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private int getScreenOrientation(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int rot = getScreenRotation();
        /*
         * Since getRotation() returns the screen's "natural" orientation,
         * which is not guaranteed to be SCREEN_ORIENTATION_PORTRAIT,
         * we have to invert the SCREEN_ORIENTATION value if it is "naturally"
         * landscape.
         */
        @SuppressWarnings("deprecation")
        boolean defaultWide = display.getWidth() > display.getHeight();
        if(rot == Surface.ROTATION_90 || rot == Surface.ROTATION_270)
            defaultWide = !defaultWide;
        if(defaultWide) {
            switch (rot) {
                case Surface.ROTATION_0:
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                case Surface.ROTATION_90:
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                case Surface.ROTATION_180:
                    // SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
                    // Level 9+
                    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                            : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                case Surface.ROTATION_270:
                    // SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
                    // Level 9+
                    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                            : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                default:
                    return 0;
            }
        } else {
            switch (rot) {
                case Surface.ROTATION_0:
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                case Surface.ROTATION_90:
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                case Surface.ROTATION_180:
                    // SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
                    // Level 9+
                    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                            : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                case Surface.ROTATION_270:
                    // SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
                    // Level 9+
                    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                            : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                default:
                    return 0;
            }
        }
    }

}

