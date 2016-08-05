package com.android.tvvideo.view.page;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.android.tvvideo.R;
import com.android.tvvideo.tools.ViewTool;


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

        imageView.setBackground(ViewTool.getDrawble(context,viewId));

        return view;
    }
}
