package com.android.tvvideo.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yangfengyuan on 16/8/5.
 */
public class ViewTool {

    public static Drawable getDrawble(Context context, int resId){
        BitmapFactory.Options opt = new BitmapFactory.Options();

        opt.inPreferredConfig = Bitmap.Config.RGB_565;

        opt.inPurgeable = true;

        opt.inInputShareable = true;

        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);

        Bitmap bitmap = BitmapFactory.decodeStream(is,null, opt);

        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new BitmapDrawable(context.getResources(),bitmap);
    }
}
