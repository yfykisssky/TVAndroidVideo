package com.android.tvvideo.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by yfykisssky on 16/7/22.
 */
public class ReFousListView extends ListView {

    public ReFousListView(Context context) {
        super(context);
    }

    public ReFousListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReFousListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        int lastSelectItem = getSelectedItemPosition();
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            setSelection(lastSelectItem);
        }
    }
}
