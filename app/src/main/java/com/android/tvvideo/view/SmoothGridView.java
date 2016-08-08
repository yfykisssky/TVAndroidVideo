package com.android.tvvideo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;

/**
 * Created by yfykisssky on 16/7/20.
 */
public class SmoothGridView extends GridView {

    private boolean isScroll = false;
    private int position = 0;
    //  private int iCount;
    private int iColumns;
    private int iFirstView;
    private int iLastView;
    private int iselecte;
    private boolean isFirst = true;
    static int iPageNum = 0;

    public SmoothGridView(Context context) {
        super(context);

        // TODO Auto-generated constructor stub
    }

    @SuppressLint("NewApi")
    public SmoothGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public SmoothGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    // public void setScrollToScrenPosition(int position, boolean isScroll) {
    // this.position = position;
    // this.isScroll = isScroll;
    // }

    @SuppressLint("NewApi")
    @Override
    protected void layoutChildren() {
        // TODO Auto-generated method stub
        super.layoutChildren();

        if (isFirst) {
            iFirstView = getFirstVisiblePosition();
            iLastView = getLastVisiblePosition();
            iPageNum = iLastView - iFirstView + 1;
            iColumns = getNumColumns();
            Log.e(VIEW_LOG_TAG, "iPageNum---->>>" + iPageNum);
            isFirst = false;
        }

        // iCount = getCount();

        iselecte = getSelectedItemPosition();
        Log.e(VIEW_LOG_TAG, "iFirstView----->>>" + iFirstView
                + "iLastView--->>>" + iLastView + "iselecte-->>>" + iselecte);

        this.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(iPageNum==0){
                    return false;
                }

                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.e(VIEW_LOG_TAG, "iColumns---->>>" + iColumns);
                    if (iselecte % iPageNum >= iPageNum - iColumns
                            && iselecte % iPageNum < iPageNum) {
                        Log.e(VIEW_LOG_TAG, "iselecte---->>>" + iselecte);
                        position = iselecte + iColumns;
                        isScroll = true;
                    }
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (iselecte % iPageNum >= 0
                            && iselecte % iPageNum < iColumns) {
                        Log.e(VIEW_LOG_TAG, "iselecte---->>>" + iselecte);
                        isScroll = true;
                        position = ((iselecte - iPageNum) / iColumns)
                                * iColumns;
                    }
                }
                return false;
            }
        });
        if (!isScroll) {
            return;
        }
        isScroll = false;
        Log.e(VIEW_LOG_TAG, "position---->>>" + position);
        smoothScrollToPositionFromTop(position, 20, 500);
    }
}

