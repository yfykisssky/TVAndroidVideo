package org.videolan.vlc.video;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.AndroidUtil;
import org.videolan.vlc.PlaybackHelper;
import org.videolan.vlc.PlaybackService;
import org.videolan.vlc.VLCApplication;
import org.videolan.vlc.media.MediaWrapper;
import org.videolan.vlc.util.Permissions;
import org.videolan.vlc.util.Preferences;
import org.videolan.vlc.util.Util;
import org.videolan.vlc.util.VLCInstance;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangfengyuan on 16/7/29.
 */
public class TVPlayerActivity extends BaseActivity implements IVLCVout.Callback,
        GestureDetector.OnDoubleTapListener, LibVLC.HardwareAccelerationError,
        PlaybackService.Client.Callback, PlaybackService.Callback {

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

        mAudioManager = null;
    }


    private void getShowMsg() {

        JSONObject postData = new JSONObject();
        try {
            postData.put("ipaddress", SystemUtil.getLocalHostIp());

            new NetDataTool(this).sendPost(NetDataConstants.GET_MSG_DATA, postData.toString(), new NetDataTool.IResponse() {
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

                            msgTex.add(jsonObject.getString(""));

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

            new NetDataTool(this).sendPost(NetDataConstants.GET_AD_DATA, postData.toString(), new NetDataTool.IResponse() {
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

                            adData.add(jsonObject.getString(""));

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
                        map.put("id",array.getJSONObject(x).getString("id"));

                        listData.add(map);
                    }

                    myAdapter.notifyDataSetChanged();

                    resetTvPlay(listData.get(0).get("playurl"));

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
        adWebView.loadUrl(adUrl);
        adWebView.setVisibility(View.VISIBLE);
    }

    private void hideAd(){
        adWebView.setVisibility(View.GONE);
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

        showMsgTex.setText(msgData);

        showMsgTex.startFor0();

        showMsgTex.setVisibility(View.VISIBLE);
    }

    private void hideMsg(){

        showMsgTex.setVisibility(View.GONE);

    }

    private void initView() {

        showMsgTex=(MarqueeText)findViewById(R.id.showmsg);

        adWebView=(WebView)findViewById(R.id.adwebview);

        relativeLayout = (ScrollRelativeLayout) findViewById(R.id.relative);

        listView = (ListView) findViewById(R.id.list);

        myAdapter = new MyAdapter();

        listView.setAdapter(myAdapter);

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


        startCountTimeThread();

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

        mUri=Uri.parse(playUrl);

        stopPlayback();

        startPlayback();

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

            myHolder.indexTex.setText(String.valueOf(i));

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
        countTimeThread = new CountTimeThread(2);
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

    //############################################################################################################################################################################
    //############################################################################################################################################################################
    //############################################################################################################################################################################
    //############################################################################################################################################################################
    //############################################################################################################################################################################

    public final static String PLAY_DISABLE_HARDWARE = "disable_hardware";
    public final static String PLAY_URL = "play_url";

    private final PlaybackHelper mHelper = new PlaybackHelper(this, this);
    private PlaybackService mService;
    private View mRootView;
    private SurfaceView mSurfaceView = null;
    private FrameLayout mSurfaceFrame;
    private Uri mUri;
    private GestureDetectorCompat mDetector = null;

    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_HORIZONTAL = 1;
    private static final int SURFACE_FIT_VERTICAL = 2;
    private static final int SURFACE_FILL = 3;
    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_ORIGINAL = 6;
    private int mCurrentSize = SURFACE_BEST_FIT;

    private SharedPreferences mSettings;

    /** Overlay */
    private static final int FADE_OUT_INFO = 3;
    private static final int START_PLAYBACK = 4;
    private static final int AUDIO_SERVICE_CONNECTION_FAILED = 5;
    private static final int RESET_BACK_LOCK = 6;
    private static final int CHECK_VIDEO_TRACKS = 7;
    private static final int HW_ERROR = 1000; // TODO REMOVE

    private boolean mDragging;
    private boolean mShowing;
    private int mUiVisibility = -1;
    private TextView mInfo;
    private View mVerticalBar;
    private View mVerticalBarProgress;
    private boolean mIsLoading;
    private LoadingDialog loadingDialog;
    /*    private View mObjectFocused;
        private boolean mDisplayRemainingTime = false;*/
    private int mScreenOrientation;
    private int mScreenOrientationLock;
    private boolean mIsLocked = false;
    /* -1 is a valid track (Disable) */
    private int mLastAudioTrack = -2;
    private int mLastSpuTrack = -2;
    private int mOverlayTimeout = 0;
    private boolean mLockBackButton = false;

    /**
     * For uninterrupted switching between audio and video mode
     */
    private boolean mSwitchingView;
    private boolean mHardwareAccelerationError;
    private boolean mEndReached;
    private boolean mHasSubItems = false;

    // Playlist
    private int indexPosition = -1;

    // size of the video
    private int mVideoHeight;
    private int mVideoWidth;
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum;
    private int mSarDen;

    //Volume
    private AudioManager mAudioManager;
    private int mAudioMax;
    private boolean mMute = false;
    private int mVolSave;

    /**
     * Flag to indicate whether the media should be paused once loaded
     * (e.g. lock screen, or to restore the pause state)
     */
    private boolean mPlaybackStarted = false;
    private boolean mSurfacesAttached = false;

    // Navigation handling (DVD, Blu-Ray...)
    private int mMenuIdx = -1;
    private boolean mIsNavMenu = false;

    private View.OnLayoutChangeListener mOnLayoutChangeListener;
    private AlertDialog mAlertDialog;

    private static LibVLC LibVLC() {
        return VLCInstance.get();
    }

    private void initVideoPlayer(){

        if (!VLCInstance.testCompatibleCPU(this)) {
            exit();
            return;
        }

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        /* Services and miscellaneous */
        mAudioManager = (AudioManager) VLCApplication.getAppContext().getSystemService(AUDIO_SERVICE);
        mAudioMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mRootView = findViewById(R.id.player_root);

        loadingDialog=new LoadingDialog(this);

        // the info textView is not on the overlay
        mInfo = (TextView) findViewById(R.id.player_overlay_textinfo);
        mVerticalBar = findViewById(R.id.verticalbar);
        mVerticalBarProgress = findViewById(R.id.verticalbar_progress);

        mScreenOrientation = Integer.valueOf(mSettings.getString("screen_orientation_value", "4" /*SCREEN_ORIENTATION_SENSOR*/));

        mSurfaceView = (SurfaceView) findViewById(R.id.player_surface);

        mSurfaceFrame = (FrameLayout) findViewById(R.id.player_surface_frame);

        //startLoading();

        mSwitchingView = false;
        mHardwareAccelerationError = false;
        mEndReached = false;

        // Clear the resume time, since it is only used for resumes in external
        // videos.
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putLong(Preferences.VIDEO_RESUME_TIME, -1);
        // Also clear the subs list, because it is supposed to be per session
        // only (like desktop VLC). We don't want the custom subtitle files
        // to persist forever with this video.
        editor.putString(Preferences.VIDEO_SUBTITLE_FILES, null);
        // Paused flag - per session too, like the subs list.
        editor.remove(Preferences.VIDEO_PAUSED);
        Util.commitPreferences(editor);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // Orientation
        // 100 is the value for screen_orientation_start_lock
        //noinspection WrongConstant
        setRequestedOrientation(mScreenOrientation != 100? mScreenOrientation : getScreenOrientation());

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSwitchingView = false;

        if (mIsLocked && mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR)
            setRequestedOrientation(mScreenOrientationLock);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPause() {
        super.onPause();

        /* Stop the earliest possible to avoid vout error */
        if (isFinishing())
            stopPlayback();
        else if (true&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !requestVisibleBehind(true))
            stopPlayback();
    }

    @Override
    public void onVisibleBehindCanceled() {
        super.onVisibleBehindCanceled();
        stopPlayback();
        exitOK();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (!AndroidUtil.isHoneycombOrLater())
            changeSurfaceLayout();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHelper.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (mAlertDialog != null && mAlertDialog.isShowing())
            mAlertDialog.dismiss();
        stopPlayback();
        if (mService != null)
            mService.removeCallback(this);
        mHelper.onStop();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void startPlayback() {
        /* start playback only when audio service and both surfaces are ready */
        if (mPlaybackStarted || mService == null)
            return;

        mPlaybackStarted = true;

        if (AndroidUtil.isICSOrLater())
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                    new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if (visibility == mUiVisibility)
                                return;
                            if (visibility == View.SYSTEM_UI_FLAG_VISIBLE && !mShowing && !isFinishing()) {
                                //showOverlay();
                            }
                            mUiVisibility = visibility;
                        }
                    }
            );

        if (AndroidUtil.isHoneycombOrLater()) {
            if (mOnLayoutChangeListener == null) {
                mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
                    private final Runnable mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            changeSurfaceLayout();
                        }
                    };
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right,
                                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                            /* changeSurfaceLayout need to be called after the layout changed */
                            mHandler.removeCallbacks(mRunnable);
                            mHandler.post(mRunnable);
                        }
                    }
                };
            }
            mSurfaceFrame.addOnLayoutChangeListener(mOnLayoutChangeListener);
        }
        changeSurfaceLayout();

        LibVLC().setOnHardwareAccelerationError(this);
        final IVLCVout vlcVout = mService.getVLCVout();
        vlcVout.detachViews();

        vlcVout.setVideoView(mSurfaceView);

        mSurfacesAttached = true;
        vlcVout.addCallback(this);
        vlcVout.attachViews();
        if (mRootView != null)
            mRootView.setKeepScreenOn(true);

        loadMedia();

        // Set user playback speed
        mService.setRate(mSettings.getFloat(Preferences.VIDEO_SPEED, 1));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void stopPlayback() {
        if (!mPlaybackStarted)
            return;

        if (mMute)
            mute(false);

        LibVLC().setOnHardwareAccelerationError(null);

        mPlaybackStarted = false;

        mService.removeCallback(this);
        final IVLCVout vlcVout = mService.getVLCVout();
        vlcVout.removeCallback(this);
        if (mSurfacesAttached)
            vlcVout.detachViews();
        if (mRootView != null)
            mRootView.setKeepScreenOn(false);

        mHandler.removeCallbacksAndMessages(null);

        if (mDetector != null) {
            mDetector.setOnDoubleTapListener(null);
            mDetector = null;
        }

        if (AndroidUtil.isHoneycombOrLater() && mOnLayoutChangeListener != null)
            mSurfaceFrame.removeOnLayoutChangeListener(mOnLayoutChangeListener);

        if (AndroidUtil.isICSOrLater())
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(null);

        if(mSwitchingView && mService != null) {
            mService.showWithoutParse(indexPosition);
            return;
        }

        mService.stop();

    }

    public static void start(Context context, Uri uri) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(PLAY_URL, uri);

        context.startActivity(intent);
    }

    private void exit(){
        if (isFinishing())
            return;
        finish();
    }

    private void exitOK() {
        exit();
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        if (mIsLoading)
            return false;
        //showOverlay();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mLockBackButton) {
            mLockBackButton = false;
            mHandler.sendEmptyMessageDelayed(RESET_BACK_LOCK, 2000);
            Toast.makeText(this, getString(R.string.back_quit_lock), Toast.LENGTH_SHORT).show();
        }else if (true&& mShowing && !mIsLocked) {
            //hideOverlay(true);
        } else
            exitOK();
    }

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_BUTTON_B)
            return super.onKeyDown(keyCode, event);
        if (mIsLoading) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    exitOK();
                    return true;
            }
            return false;
        }
        showOverlayTimeout(OVERLAY_TIMEOUT);
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                seekDelta(10000);
                return true;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                seekDelta(-10000);
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
       *//*     case KeyEvent.KEYCODE_SPACE:
                if (mIsNavMenu)
                    return navigateDvdMenu(keyCode);
                else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) //prevent conflict with remote control
                    return super.onKeyDown(keyCode, event);
                else
                    doPlayPause();
                return true;*//*
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                updateMute();
                return true;
            case KeyEvent.KEYCODE_MEDIA_STOP:
                exitOK();
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (mIsNavMenu)
                    return navigateDvdMenu(keyCode);
                else
                    return super.onKeyDown(keyCode, event);
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (mMute) {
                    updateMute();
                    return true;
                } else
                    return false;
        }
        return super.onKeyDown(keyCode, event);
    }*/

   /* private boolean navigateDvdMenu(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                mService.navigate(MediaPlayer.Navigate.Up);
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mService.navigate(MediaPlayer.Navigate.Down);
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mService.navigate(MediaPlayer.Navigate.Left);
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mService.navigate(MediaPlayer.Navigate.Right);
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_BUTTON_X:
            case KeyEvent.KEYCODE_BUTTON_A:
                mService.navigate(MediaPlayer.Navigate.Activate);
                return true;
            default:
                return false;
        }
    }*/

    /**
     * Show text in the info view and vertical progress bar for "duration" milliseconds
     * @param text
     * @param duration
     * @param barNewValue new volume/brightness value (range: 0 - 15)
     */
    private void showInfoWithVerticalBar(String text, int duration, int barNewValue) {
        showInfo(text, duration);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mVerticalBarProgress.getLayoutParams();
        layoutParams.weight = barNewValue;
        mVerticalBarProgress.setLayoutParams(layoutParams);
        mVerticalBar.setVisibility(View.VISIBLE);
    }

    /**
     * Show text in the info view for "duration" milliseconds
     * @param text
     * @param duration
     */
    private void showInfo(String text, int duration) {
        if (mVerticalBar != null)
            mVerticalBar.setVisibility(View.GONE);
        mInfo.setVisibility(View.VISIBLE);
        mInfo.setText(text);
        mHandler.removeMessages(FADE_OUT_INFO);
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, duration);
    }

    private void showInfo(int textid, int duration) {
        if (mVerticalBar != null)
            mVerticalBar.setVisibility(View.GONE);
        mInfo.setVisibility(View.VISIBLE);
        mInfo.setText(textid);
        mHandler.removeMessages(FADE_OUT_INFO);
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, duration);
    }

    /**
     * hide the info view with "delay" milliseconds delay
     * @param delay
     */
    private void hideInfo(int delay) {
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, delay);
    }

    /**
     * hide the info view
     */
    private void hideInfo() {
        hideInfo(0);
    }

    private void fadeOutInfo() {
        if (mInfo.getVisibility() == View.VISIBLE)
            mInfo.startAnimation(AnimationUtils.loadAnimation(
                    TVPlayerActivity.this, android.R.anim.fade_out));
        mInfo.setVisibility(View.INVISIBLE);

        if (mVerticalBar != null) {
            if (mVerticalBar.getVisibility() == View.VISIBLE) {
                mVerticalBar.startAnimation(AnimationUtils.loadAnimation(
                        TVPlayerActivity.this, android.R.anim.fade_out));
                mVerticalBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (mService == null)
            return false;
        if (!mIsLocked) {
            //doPlayPause();
            return true;
        }
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public void update() {
    }

    @Override
    public void updateProgress() {
    }

    @Override
    public void onMediaEvent(Media.Event event) {
        switch (event.type) {
            case Media.Event.ParsedChanged:
                //updateNavStatus();
                break;
            case Media.Event.MetaChanged:
                break;
            case Media.Event.SubItemTreeAdded:
                mHasSubItems = true;
                break;
        }
    }

    @Override
    public void onMediaPlayerEvent(MediaPlayer.Event event) {
        switch (event.type) {
            case MediaPlayer.Event.Opening:
                mHasSubItems = false;
                break;
            case MediaPlayer.Event.Playing:
                onPlaying();
                break;
          /*  case MediaPlayer.Event.Paused:
                updateOverlayPausePlay();
                break;*/
            case MediaPlayer.Event.Stopped:
                exitOK();
                break;
            case MediaPlayer.Event.EndReached:
                /* Don't end the activity if the media has subitems since the next child will be
                 * loaded by the PlaybackService */
                if (!mHasSubItems)
                    endReached();
                break;
            case MediaPlayer.Event.EncounteredError:
                encounteredError();
                break;
            case MediaPlayer.Event.TimeChanged:
                break;
            case MediaPlayer.Event.Vout:
                //updateNavStatus();
                if (mMenuIdx == -1)
                    handleVout(event.getVoutCount());
                break;
            case MediaPlayer.Event.ESAdded:
            case MediaPlayer.Event.ESDeleted:
                if (mMenuIdx == -1 && event.getEsChangedType() == Media.Track.Type.Video) {
                    mHandler.removeMessages(CHECK_VIDEO_TRACKS);
                    mHandler.sendEmptyMessageDelayed(CHECK_VIDEO_TRACKS, 1000);
                }
                break;
       /*     case MediaPlayer.Event.SeekableChanged:
                updateSeekable(event.getSeekable());
                break;
            case MediaPlayer.Event.PausableChanged:
                updatePausable(event.getPausable());
                break;*/
        }
    }

    /**
     * Handle resize of the surface and the overlay
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mService == null)
                return true;

            switch (msg.what) {
         /*       case FADE_OUT:
                    hideOverlay(false);
                    break;
                case SHOW_PROGRESS:
                    int pos = setOverlayProgress();
                    if (canShowProgress()) {
                        msg = mHandler.obtainMessage(SHOW_PROGRESS);
                        mHandler.sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;*/
                case FADE_OUT_INFO:
                    fadeOutInfo();
                    break;
                case START_PLAYBACK:
                    startPlayback();
                    break;
                case AUDIO_SERVICE_CONNECTION_FAILED:
                    exit();
                    break;
                case RESET_BACK_LOCK:
                    mLockBackButton = true;
                    break;
                case HW_ERROR:
                    handleHardwareAccelerationError();
                    break;
            }
            return true;
        }
    });

    private boolean canShowProgress() {
        return !mDragging && mShowing && mService != null &&  mService.isPlaying();
    }

    private void onPlaying() {
        stopLoading();
        //showOverlay(true);
        setESTracks();
        // updateNavStatus();
    }

    private void endReached() {
        if (mService == null)
            return;
        if (mService.getRepeatType() == PlaybackService.REPEAT_ONE){
            //seek(0);
            return;
        }
        if(mService.expand() == 0) {
            startLoading();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadMedia();
                }
            });
        } else {
            /* Exit player when reaching the end */
            mEndReached = true;
            exitOK();
        }
    }

    private void encounteredError() {
        if (isFinishing())
            return;
        //We may not have the permission to access files
        if (AndroidUtil.isMarshMallowOrLater() && mUri != null &&
                TextUtils.equals(mUri.getScheme(), "file") &&
                !Permissions.canReadStorage()) {
            Permissions.checkReadStoragePermission(this, true);
            return;
        }
        /* Encountered Error, exit player with a message */
        mAlertDialog = new AlertDialog.Builder(TVPlayerActivity.this)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        exit();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        exit();
                    }
                })
                .setTitle(R.string.encountered_error_title)
                .setMessage(R.string.encountered_error_message)
                .create();
        mAlertDialog.show();
    }

    @Override
    public void eventHardwareAccelerationError() {
        mHandler.sendEmptyMessage(HW_ERROR);
    }

    private void handleHardwareAccelerationError() {
        mHardwareAccelerationError = true;
        if (mSwitchingView)
            return;
        Toast.makeText(this, R.string.hardware_acceleration_error, Toast.LENGTH_LONG).show();
        final boolean wasPaused = !mService.isPlaying();
        final long oldTime = mService.getTime();
        mService.stop();
        if(!isFinishing()) {
            final MediaWrapper mw = new MediaWrapper(mUri);
            if (wasPaused)
                mw.addFlags(MediaWrapper.MEDIA_PAUSED);
            mw.addFlags(MediaWrapper.MEDIA_NO_HWACCEL);
            mw.addFlags(MediaWrapper.MEDIA_VIDEO);
            mService.load(mw);
        /*    if (oldTime > 0)
                //seek(oldTime);*/
        }
    }

    private void handleVout(int voutCount) {
        final IVLCVout vlcVout = mService.getVLCVout();
        if (vlcVout.areViewsAttached() && voutCount == 0 && !mEndReached) {
            /* Video track lost, open in audio mode */
            mSwitchingView = true;
            exit();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void changeSurfaceLayout() {
        int sw;
        int sh;

        // get screen size

        sw = getWindow().getDecorView().getWidth();
        sh = getWindow().getDecorView().getHeight();

        if (mService != null) {
            final IVLCVout vlcVout = mService.getVLCVout();
            vlcVout.setWindowSize(sw, sh);
        }

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
        FrameLayout surfaceFrame;

        surface = mSurfaceView;
        surfaceFrame = mSurfaceFrame;

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

/*

    private void doVolumeTouch(float y_changed) {
        if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_VOLUME)
            return;
        float delta = - ((y_changed / mSurfaceYDisplayRange) * mAudioMax);
        mVol += delta;
        int vol = (int) Math.min(Math.max(mVol, 0), mAudioMax);
        if (delta != 0f) {
            setAudioVolume(vol);
        }
    }*/

    private void setAudioVolume(int vol) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);

        /* Since android 4.3, the safe volume warning dialog is displayed only with the FLAG_SHOW_UI flag.
         * We don't want to always show the default UI volume, so show it only when volume is not set. */
        int newVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (vol != newVol)
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, AudioManager.FLAG_SHOW_UI);

        //mTouchAction = TOUCH_VOLUME;
        vol = vol * 100 / mAudioMax;
        showInfoWithVerticalBar(getString(R.string.volume) + "\n" + Integer.toString(vol) + '%', 1000, vol);
    }

    private void mute(boolean mute) {
        mMute = mute;
        if (mMute)
            mVolSave = mService.getVolume();
        mService.setVolume(mMute ? 0 : mVolSave);
    }

    private void updateMute () {
        mute(!mMute);
        showInfo(mMute ? R.string.sound_off : R.string.sound_on,1000);
    }

    private void resizeVideo() {
        if (mCurrentSize < SURFACE_ORIGINAL) {
            mCurrentSize++;
        } else {
            mCurrentSize = 0;
        }
        changeSurfaceLayout();
        switch (mCurrentSize) {
            case SURFACE_BEST_FIT:
                showInfo(R.string.surface_best_fit, 1000);
                break;
            case SURFACE_FIT_HORIZONTAL:
                showInfo(R.string.surface_fit_horizontal, 1000);
                break;
            case SURFACE_FIT_VERTICAL:
                showInfo(R.string.surface_fit_vertical, 1000);
                break;
            case SURFACE_FILL:
                showInfo(R.string.surface_fill, 1000);
                break;
            case SURFACE_16_9:
                showInfo("16:9", 1000);
                break;
            case SURFACE_4_3:
                showInfo("4:3", 1000);
                break;
            case SURFACE_ORIGINAL:
                showInfo(R.string.surface_original, 1000);
                break;
        }

    }

    private void setESTracks() {
        if (mLastAudioTrack >= -1) {
            mService.setAudioTrack(mLastAudioTrack);
            mLastAudioTrack = -2;
        }
        if (mLastSpuTrack >= -1) {
            mService.setSpuTrack(mLastSpuTrack);
            mLastSpuTrack = -2;
        }
    }

    /**
     *
     */
    private void play() {
        mService.play();
        if (mRootView != null)
            mRootView.setKeepScreenOn(true);
    }

    /**
     *
     */
    private void pause() {
        mService.pause();
        if (mRootView != null)
            mRootView.setKeepScreenOn(false);
    }

    @SuppressWarnings({ "unchecked" })
    private void loadMedia() {
        if (mService == null)
            return;

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
//        mUri = extras.getParcelable(PLAY_URL);

        boolean wasPaused;
        /*
         * If the activity has been paused by pressing the power button, then
         * pressing it again will show the lock screen.
         * But onResume will also be called, even if vlc-android is still in
         * the background.
         * To workaround this, pause playback if the lockscreen is displayed.
         */
        final KeyguardManager km = (KeyguardManager) VLCApplication.getAppContext().getSystemService(KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode())
            wasPaused = true;
        else
            wasPaused = mSettings.getBoolean(Preferences.VIDEO_PAUSED, false);
        //if (wasPaused)

        if (mUri != null) {
                /* prepare playback */
            mService.stop();
            final MediaWrapper mw = new MediaWrapper(mUri);
            if (wasPaused)
                mw.addFlags(MediaWrapper.MEDIA_PAUSED);
            if (mHardwareAccelerationError || intent.hasExtra(PLAY_DISABLE_HARDWARE))
                mw.addFlags(MediaWrapper.MEDIA_NO_HWACCEL);
            mw.removeFlags(MediaWrapper.MEDIA_FORCE_AUDIO);
            mw.addFlags(MediaWrapper.MEDIA_VIDEO);
            mService.addCallback(this);
            mService.load(mw);
            indexPosition = mService.getCurrentMediaPosition();
        }

    }

    @SuppressWarnings("deprecation")
    private int getScreenRotation(){
        WindowManager wm = (WindowManager) VLCApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
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
        WindowManager wm = (WindowManager) VLCApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
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

    /**
     * Start the video loading animation.
     */
    private void startLoading() {
        mIsLoading = true;

        loadingDialog.show();
    }

    /**
     * Stop the video loading animation.
     */
    private void stopLoading() {
        mIsLoading = false;

        loadingDialog.dismiss();
    }

    @Override
    public void onConnected(PlaybackService service) {
        mService = service;
        mHandler.sendEmptyMessage(START_PLAYBACK);
    }

    @Override
    public void onDisconnected() {
        mService = null;
        mHandler.sendEmptyMessage(AUDIO_SERVICE_CONNECTION_FAILED);
    }

    @Override
    public void onNewLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        if (width * height == 0)
            return;

        // store video size
        mVideoWidth = width;
        mVideoHeight = height;
        mVideoVisibleWidth  = visibleWidth;
        mVideoVisibleHeight = visibleHeight;
        mSarNum = sarNum;
        mSarDen = sarDen;
        changeSurfaceLayout();
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {
    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {
        mSurfacesAttached = false;
    }
}

