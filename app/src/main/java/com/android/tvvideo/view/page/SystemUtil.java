package com.android.tvvideo.view.page;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by leo on 16/4/6.
 */
public class SystemUtil {

    /**
     * dp to px
     *
     * @param dp
     * @param context
     * @return
     */
    public static int dpToPx(Context context, float dp) {
        return (int) applyDimension(context, TypedValue.COMPLEX_UNIT_DIP, dp);
    }

    /**
     * 单位转换
     *
     * @param context
     * @param unit    TypedValue.COMPLEX_UNIT_DIP
     * @param value   px
     * @return
     */
    public static float applyDimension(Context context, int unit, float value) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(unit, value, displayMetrics);
    }


    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }


 /*   *//**
     * 设置状态栏透明
     *
     * @param activity
     *//*
    @TargetApi(19)
    public static void setStatusDrawable(Activity activity, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setStatusColor(activity, drawable);
    }*/



}
