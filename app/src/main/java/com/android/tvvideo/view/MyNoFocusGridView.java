package com.android.tvvideo.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by yangfengyuan on 2016/11/16.
 */

public class MyNoFocusGridView extends GridView {

    public MyNoFocusGridView(Context context) {
        super(context);
    }

    public MyNoFocusGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyNoFocusGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 屏蔽android4.4 setAdapter时View抢焦点的BUG
     */
    @Override
    public boolean isInTouchMode() {
        if(19 == Build.VERSION.SDK_INT){
            return !(hasFocus() && !super.isInTouchMode());
        }else{
            return super.isInTouchMode();
        }
    }

}
