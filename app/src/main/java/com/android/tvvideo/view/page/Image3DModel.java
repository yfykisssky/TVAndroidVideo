package com.android.tvvideo.view.page;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.android.tvvideo.R;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by leo on 16/5/19.
 * 图片ViewModel
 */
public class Image3DModel implements LayoutId {

    int viewId;

    public Image3DModel(int viewId) {
        this.viewId=viewId;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_page, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.img);

        imageView.setBackground(getDrawble(context,viewId));

        return view;
    }

    private Drawable getDrawble(Context context,int resId){
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
