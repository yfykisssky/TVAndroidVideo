/*****************************************************************************
 * VideoPlayerActivity.java
 *****************************************************************************
 * Copyright © 2011-2014 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package org.videolan.vlc.video;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tvvideo.R;
import com.android.tvvideo.base.BaseActivity;
import com.android.tvvideo.view.LoadingDialog;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.AndroidUtil;
import org.videolan.vlc.PlaybackHelper;
import org.videolan.vlc.PlaybackService;
import org.videolan.vlc.VLCApplication;
import org.videolan.vlc.media.MediaWrapper;
import org.videolan.vlc.util.AndroidDevices;
import org.videolan.vlc.util.Permissions;
import org.videolan.vlc.util.Preferences;
import org.videolan.vlc.util.Strings;
import org.videolan.vlc.util.Util;
import org.videolan.vlc.util.VLCInstance;

import java.lang.reflect.Method;

public class VideoPlayerActivity extends BaseActivity implements IVLCVout.Callback,
        GestureDetector.OnDoubleTapListener, LibVLC.HardwareAccelerationError,
        PlaybackService.Client.Callback, PlaybackService.Callback {

    public final static String TAG = "VLC/VideoPlayerActivity";

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
    private View mOverlayProgress;
    private View mOverlayButtons;
    private static final int OVERLAY_TIMEOUT = 4000;
    private static final int OVERLAY_INFINITE = -1;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private static final int FADE_OUT_INFO = 3;
    private static final int START_PLAYBACK = 4;
    private static final int AUDIO_SERVICE_CONNECTION_FAILED = 5;
    private static final int RESET_BACK_LOCK = 6;
    private static final int CHECK_VIDEO_TRACKS = 7;
    private static final int HW_ERROR = 1000; // TODO REMOVE

    private boolean mDragging;
    private boolean mShowing;
    private int mUiVisibility = -1;
    private SeekBar mSeekbar;
    private TextView mTime;
    private TextView mLength;
    private TextView mInfo;
    private View mVerticalBar;
    private View mVerticalBarProgress;
    private boolean mIsLoading;
    private LoadingDialog loadingDialog;
    private ImageView mPlayPause;
    private ImageView mRewind;
    private ImageView mForward;
    private View mObjectFocused;
    private boolean mDisplayRemainingTime = false;
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

    /* for getTime and seek */
    private long mForcedTime = -1;
    private long mLastTime = -1;

    private OnLayoutChangeListener mOnLayoutChangeListener;
    //private AlertDialog mAlertDialog;

    private static LibVLC LibVLC() {
        return VLCInstance.get();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActivityName(this.getClass().getName());

        if (!VLCInstance.testCompatibleCPU(this)) {
            exit();
            return;
        }

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        /* Services and miscellaneous */
        mAudioManager = (AudioManager) VLCApplication.getAppContext().getSystemService(AUDIO_SERVICE);
        mAudioMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        setContentView(R.layout.activity_player);

        mRootView = findViewById(R.id.player_root);

        loadingDialog=new LoadingDialog(this);

        mOverlayProgress = findViewById(R.id.progress_overlay);
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams)mOverlayProgress.getLayoutParams();
        if (AndroidDevices.isPhone() || !AndroidDevices.hasNavBar()) {
            layoutParams.width = LayoutParams.MATCH_PARENT;
        } else {
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        }
        mOverlayProgress.setLayoutParams(layoutParams);
        mOverlayButtons =  findViewById(R.id.player_overlay_buttons);

        // Position and remaining time
        mTime = (TextView) findViewById(R.id.player_overlay_time);
        mLength = (TextView) findViewById(R.id.player_overlay_length);

        // the info textView is not on the overlay
        mInfo = (TextView) findViewById(R.id.player_overlay_textinfo);
        mVerticalBar = findViewById(R.id.verticalbar);
        mVerticalBarProgress = findViewById(R.id.verticalbar_progress);

        mScreenOrientation = Integer.valueOf(mSettings.getString("screen_orientation_value", "4" /*SCREEN_ORIENTATION_SENSOR*/));

        mPlayPause = (ImageView) findViewById(R.id.player_overlay_play);

        if (mSettings.getBoolean("enable_seek_buttons", false))initSeekButton();

        mSurfaceView = (SurfaceView) findViewById(R.id.player_surface);

        mSurfaceFrame = (FrameLayout) findViewById(R.id.player_surface_frame);

        mSeekbar = (SeekBar) findViewById(R.id.player_overlay_seekbar);

        dimStatusBar(false);
        startLoading();

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


        resetHudLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSwitchingView = false;

        /*
         * Set listeners here to avoid NPE when activity is closing
         */
        mSeekbar.setOnSeekBarChangeListener(mSeekListener);

        mPlayPause.setOnClickListener(mPlayPauseListener);
        mLength.setOnClickListener(mRemainingTimeListener);
        mTime.setOnClickListener(mRemainingTimeListener);

        if (mIsLocked && mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR)
            setRequestedOrientation(mScreenOrientationLock);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if (mPlaybackStarted) {
            Uri uri = intent.hasExtra(PLAY_URL) ? (Uri) intent.getExtras().getParcelable(PLAY_URL) : intent.getData();
            if (uri == null || uri.equals(mUri))
                return;
            stopPlayback();
            startPlayback();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPause() {
        super.onPause();
        hideOverlay(true);
        mSeekbar.setOnSeekBarChangeListener(null);
        mPlayPause.setOnClickListener(null);
        mPlayPause.setOnLongClickListener(null);
        mLength.setOnClickListener(null);
        mTime.setOnClickListener(null);

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
        resetHudLayout();
    }

    public void resetHudLayout() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mOverlayButtons.getLayoutParams();
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            layoutParams.addRule(RelativeLayout.BELOW, R.id.player_overlay_length);
            layoutParams.addRule(RelativeLayout.RIGHT_OF, 0);
            layoutParams.addRule(RelativeLayout.LEFT_OF, 0);
        } else {
            layoutParams.addRule(RelativeLayout.BELOW, R.id.player_overlay_seekbar);
            layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.player_overlay_time);
            layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.player_overlay_length);
        }
        mOverlayButtons.setLayoutParams(layoutParams);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mHelper.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();

      /*  if (mAlertDialog != null && mAlertDialog.isShowing())
            mAlertDialog.dismiss();*/
        stopPlayback();
        if (mService != null)
            mService.removeCallback(this);
        mHelper.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mAudioManager = null;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void startPlayback() {
        /* start playback only when audio service and both surfaces are ready */
        if (mPlaybackStarted || mService == null)
            return;

        mPlaybackStarted = true;

        if (AndroidUtil.isICSOrLater())
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                    new OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if (visibility == mUiVisibility)
                                return;
                            if (visibility == View.SYSTEM_UI_FLAG_VISIBLE && !mShowing && !isFinishing()) {
                                showOverlay();
                            }
                            mUiVisibility = visibility;
                        }
                    }
            );

        if (AndroidUtil.isHoneycombOrLater()) {
            if (mOnLayoutChangeListener == null) {
                mOnLayoutChangeListener = new OnLayoutChangeListener() {
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
            Log.d(TAG, "mLocation = \"" + mUri + "\"");
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
        showOverlay();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mLockBackButton) {
            mLockBackButton = false;
            mHandler.sendEmptyMessageDelayed(RESET_BACK_LOCK, 2000);
            Toast.makeText(this, getString(R.string.back_quit_lock), Toast.LENGTH_SHORT).show();
        }else if (true&& mShowing && !mIsLocked) {
            hideOverlay(true);
        } else
            exitOK();
    }

    @Override
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
       /*     case KeyEvent.KEYCODE_SPACE:
                if (mIsNavMenu)
                    return navigateDvdMenu(keyCode);
                else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) //prevent conflict with remote control
                    return super.onKeyDown(keyCode, event);
                else
                    doPlayPause();
                return true;*/
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
    }

    private boolean navigateDvdMenu(int keyCode) {
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
    }

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
                    VideoPlayerActivity.this, android.R.anim.fade_out));
        mInfo.setVisibility(View.INVISIBLE);

        if (mVerticalBar != null) {
            if (mVerticalBar.getVisibility() == View.VISIBLE) {
                mVerticalBar.startAnimation(AnimationUtils.loadAnimation(
                        VideoPlayerActivity.this, android.R.anim.fade_out));
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
            doPlayPause();
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
                updateNavStatus();
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
            case MediaPlayer.Event.Paused:
                updateOverlayPausePlay();
                break;
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
                updateNavStatus();
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
            case MediaPlayer.Event.SeekableChanged:
                updateSeekable(event.getSeekable());
                break;
            case MediaPlayer.Event.PausableChanged:
                updatePausable(event.getPausable());
                break;
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
                case FADE_OUT:
                    hideOverlay(false);
                    break;
                case SHOW_PROGRESS:
                    int pos = setOverlayProgress();
                    if (canShowProgress()) {
                        msg = mHandler.obtainMessage(SHOW_PROGRESS);
                        mHandler.sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
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
        showOverlay(true);
        setESTracks();
        updateNavStatus();
    }

    private void endReached() {
        if (mService == null)
            return;
        if (mService.getRepeatType() == PlaybackService.REPEAT_ONE){
            seek(0);
            return;
        }
        if(mService.expand() == 0) {
            startLoading();
            Log.d(TAG, "Found a video playlist, expanding it");
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
      /*  *//* Encountered Error, exit player with a message *//*
        mAlertDialog = new AlertDialog.Builder(VideoPlayerActivity.this)
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
        mAlertDialog.show();*/

        exit();
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
            if (oldTime > 0)
                seek(oldTime);
        }
    }

    private void handleVout(int voutCount) {
        final IVLCVout vlcVout = mService.getVLCVout();
        if (vlcVout.areViewsAttached() && voutCount == 0 && !mEndReached) {
            /* Video track lost, open in audio mode */
            Log.i(TAG, "Video track lost, switching to audio");
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
        FrameLayout surfaceFrame;

        surface = mSurfaceView;
        surfaceFrame = mSurfaceFrame;

        // set display size
        LayoutParams lp = surface.getLayoutParams();
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

    /**
     * handle changes of the seekbar (slicer)
     */
    private final OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mDragging = true;
            showOverlayTimeout(OVERLAY_INFINITE);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mDragging = false;
            showOverlay(true);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!isFinishing() && fromUser && mService.isSeekable()) {
                seek(progress);
                setOverlayProgress();
                mTime.setText(Strings.millisToString(progress));
                showInfo(Strings.millisToString(progress), 1000);
            }
        }
    };


    private void showNavMenu() {
        if (mMenuIdx >= 0)
            mService.setTitleIdx(mMenuIdx);
    }

    /**
     *
     */
    private final OnClickListener mPlayPauseListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            doPlayPause();
        }
    };

    private void updateSeekable(boolean seekable) {
        if (mRewind != null) {
            mRewind.setEnabled(seekable);
            mRewind.setImageResource(seekable
                    ? R.drawable.ic_rewind_circle
                    : R.drawable.ic_rewind_circle_disable_o);
        }
        if (mForward != null){
            mForward.setEnabled(seekable);
            mForward.setImageResource(seekable
                    ? R.drawable.ic_forward_circle
                    : R.drawable.ic_forward_circle_disable_o);
        }
        mSeekbar.setEnabled(seekable);
    }

    private void updatePausable(boolean pausable) {
        mPlayPause.setEnabled(pausable);
        if (!pausable)
            mPlayPause.setImageResource(R.drawable.ic_play_circle_disable_o);
        else {
            mDetector = new GestureDetectorCompat(this, mGestureListener);
            mDetector.setOnDoubleTapListener(this);
        }
    }

    private void doPlayPause() {
        if (!mService.isPausable())
            return;

        if (mService.isPlaying()) {
            pause();
            showOverlayTimeout(OVERLAY_INFINITE);
        } else {
            play();
            showOverlayTimeout(OVERLAY_TIMEOUT);
        }
    }

    private long getTime() {
        long time = mService.getTime();
        if (mForcedTime != -1 && mLastTime != -1) {
            /* XXX: After a seek, mService.getTime can return the position before or after
             * the seek position. Therefore we return mForcedTime in order to avoid the seekBar
             * to move between seek position and the actual position.
             * We have to wait for a valid position (that is after the seek position).
             * to re-init mLastTime and mForcedTime to -1 and return the actual position.
             */
            if (mLastTime > mForcedTime) {
                if (time <= mLastTime && time > mForcedTime || time > mLastTime)
                    mLastTime = mForcedTime = -1;
            } else {
                if (time > mForcedTime)
                    mLastTime = mForcedTime = -1;
            }
        }
        return mForcedTime == -1 ? time : mForcedTime;
    }

    private void seek(long position) {
        seek(position, mService.getLength());
    }

    private void seek(long position, float length) {
        mForcedTime = position;
        mLastTime = mService.getTime();
        if (length == 0f)
            mService.setTime(position);
        else
            mService.setPosition(position / length);
    }

    private void seekDelta(int delta) {
        // unseekable stream
        if(mService.getLength() <= 0 || !mService.isSeekable()) return;

        long position = getTime() + delta;
        if (position < 0) position = 0;
        seek(position);
        showOverlay();
    }

    private void initSeekButton() {
        mRewind = (ImageView) findViewById(R.id.player_overlay_rewind);
        mForward = (ImageView) findViewById(R.id.player_overlay_forward);
        mRewind.setVisibility(View.VISIBLE);
        mForward.setVisibility(View.VISIBLE);
        mRewind.setOnClickListener(mRewindListener);
        mForward.setOnClickListener(mForwardListener);
    }


    private final OnClickListener mRewindListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            seekDelta(-30000);
        }
    };

    private final OnClickListener mForwardListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            seekDelta(30000);
        }
    };


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
        showOverlay();
    }

    private final OnClickListener mRemainingTimeListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mDisplayRemainingTime = !mDisplayRemainingTime;
            showOverlay();
        }
    };

    /**
     * show overlay
     * @param forceCheck: adjust the timeout in function of playing state
     */
    private void showOverlay(boolean forceCheck) {
        if (forceCheck)
            mOverlayTimeout = 0;
        showOverlayTimeout(0);
    }

    /**
     * show overlay with the previous timeout value
     */
    private void showOverlay() {
        showOverlay(false);
    }


    /**
     * show overlay
     */
    private void showOverlayTimeout(int timeout) {
        if (mService == null)
            return;
        if (timeout != 0)
            mOverlayTimeout = timeout;
        if (mOverlayTimeout == 0)
            mOverlayTimeout = mService.isPlaying() ? OVERLAY_TIMEOUT : OVERLAY_INFINITE;
        if (mIsNavMenu){
            mShowing = true;
            return;
        }
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        if (!mShowing) {
            mShowing = true;
            if (!mIsLocked) {
                mPlayPause.setVisibility(View.VISIBLE);
                if (mRewind != null)
                    mRewind.setVisibility(View.VISIBLE);
                if (mForward != null)
                    mForward.setVisibility(View.VISIBLE);
            }
            dimStatusBar(false);
            mOverlayProgress.setVisibility(View.VISIBLE);
        }
        mHandler.removeMessages(FADE_OUT);
        if (mOverlayTimeout != OVERLAY_INFINITE)
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), mOverlayTimeout);
        updateOverlayPausePlay();
        if (!(mObjectFocused == null)) {
            if (mObjectFocused.isFocusable())
                mObjectFocused.requestFocus();
            mObjectFocused =  null;
        }
    }


    /**
     * hider overlay
     */
    private void hideOverlay(boolean fromUser) {
        if (mShowing) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.removeMessages(SHOW_PROGRESS);
            Log.i(TAG, "remove View!");
            mObjectFocused = getCurrentFocus();

            if (!fromUser && !mIsLocked) {
                mOverlayProgress.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                mPlayPause.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                if (mRewind != null)
                    mRewind.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                if (mForward != null)
                    mForward.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
            }
            mOverlayProgress.setVisibility(View.INVISIBLE);
            mPlayPause.setVisibility(View.INVISIBLE);
            if (mRewind != null)
                mRewind.setVisibility(View.INVISIBLE);
            if (mForward != null)
                mForward.setVisibility(View.INVISIBLE);
            mShowing = false;
            dimStatusBar(true);
        } else if (!fromUser) {
            /*
             * Try to hide the Nav Bar again.
             * It seems that you can't hide the Nav Bar if you previously
             * showed it in the last 1-2 seconds.
             */
            dimStatusBar(true);
        }
    }

    /**
     * Dim the status bar and/or navigation icons when needed on Android 3.x.
     * Hide it on Android 4.0 and later
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void dimStatusBar(boolean dim) {
        if (!AndroidUtil.isHoneycombOrLater() || mIsNavMenu)
            return;
        int visibility = 0;
        int navbar = 0;

        if (!AndroidDevices.hasCombBar() && AndroidUtil.isJellyBeanOrLater()) {
            visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            navbar = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        }
        visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (dim || mIsLocked) {
            navbar |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            if (!AndroidDevices.hasCombBar()) {
                navbar |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                if (AndroidUtil.isKitKatOrLater())
                    visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;
                visibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
        }
        if (!dim) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            visibility |= View.SYSTEM_UI_FLAG_VISIBLE;
        }

        if (AndroidDevices.hasNavBar())
            visibility |= navbar;
        getWindow().getDecorView().setSystemUiVisibility(visibility);
    }

    private void updateOverlayPausePlay() {
        if (mService == null)
            return;
        if (mService.isPausable())
            mPlayPause.setImageResource(mService.isPlaying() ? R.drawable.ic_pause_circle
                    : R.drawable.ic_play_circle);
    }

    /**
     * update the overlay
     */
    private int setOverlayProgress() {
        if (mService == null) {
            return 0;
        }
        int time = (int) getTime();
        int length = (int) mService.getLength();
        // Update all view elements
        mSeekbar.setMax(length);
        mSeekbar.setProgress(time);
        if (time >= 0) mTime.setText(Strings.millisToString(time));
        if (length >= 0) mLength.setText(mDisplayRemainingTime && length > 0
                ? "-" + '\u00A0' + Strings.millisToString(length - time)
                : Strings.millisToString(length));

        return time;
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
        mUri = extras.getParcelable(PLAY_URL);

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
        if (wasPaused)
            Log.d(TAG, "Video was previously paused, resuming in paused mode");

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
        mOverlayProgress.setVisibility(View.INVISIBLE);

        loadingDialog.show();
    }

    /**
     * Stop the video loading animation.
     */
    private void stopLoading() {
        mIsLoading = false;
        mOverlayProgress.setVisibility(View.VISIBLE);

        loadingDialog.dismiss();
    }

    private void updateNavStatus() {
        mIsNavMenu = false;
        mMenuIdx = -1;

        final MediaPlayer.Title[] titles = mService.getTitles();
        if (titles != null) {
            final int currentIdx = mService.getTitleIdx();
            for (int i = 0; i < titles.length; ++i) {
                final MediaPlayer.Title title = titles[i];
                if (title.menu) {
                    mMenuIdx = i;
                    break;
                }
            }
            mIsNavMenu = mMenuIdx == currentIdx;
        }

        if (mIsNavMenu) {
            /*
             * Keep the overlay hidden in order to have touch events directly
             * transmitted to navigation handling.
             */
            hideOverlay(false);
        }
        else if (mMenuIdx != -1)
            setESTracks();
    }

    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {}

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {}

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

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
