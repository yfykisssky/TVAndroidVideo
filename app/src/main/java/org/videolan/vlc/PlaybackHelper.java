package org.videolan.vlc;

import android.content.Context;
import android.support.annotation.MainThread;

import java.util.ArrayList;

/**
 * Created by yangfengyuan on 16/8/10.
 */
public class PlaybackHelper {

    private ArrayList<PlaybackService.Client.Callback> mFragmentCallbacks = new ArrayList<PlaybackService.Client.Callback>();
    final private PlaybackService.Client.Callback mActivityCallback;
    private PlaybackService.Client mClient;
    protected PlaybackService mService;

    public PlaybackHelper(Context context, PlaybackService.Client.Callback activityCallback) {
        mClient = new PlaybackService.Client(context, mClientCallback);
        mActivityCallback = activityCallback;
    }

    @MainThread
    public void onStart() {
        mClient.connect();
    }

    @MainThread
    public void onStop() {
        mClientCallback.onDisconnected();
        mClient.disconnect();
    }

    private final  PlaybackService.Client.Callback mClientCallback = new PlaybackService.Client.Callback() {
        @Override
        public void onConnected(PlaybackService service) {
            mService = service;
            mActivityCallback.onConnected(service);
            for (PlaybackService.Client.Callback connectCb : mFragmentCallbacks)
                connectCb.onConnected(mService);
        }

        @Override
        public void onDisconnected() {
            mService = null;
            mActivityCallback.onDisconnected();
            for (PlaybackService.Client.Callback connectCb : mFragmentCallbacks)
                connectCb.onDisconnected();
        }
    };
}
