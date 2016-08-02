package com.android.tvvideo.view.page;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.hospital.video.R;


/**
 * Created by leo on 16/5/19.
 * 图片ViewModel
 */
public class Image3DModel implements LayoutId {

    int viewId;

    public Image3DModel(int viewId) {
        this.viewId=viewId;
    }

    @Override
    public View getView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_page, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.img);
        //PhotoLoader.display(context, imageView, imageInfo.getImageUrl(), context.getResources().getDrawable(R.mipmap.ic_loading));*/
        Resources res=context.getResources();
        Bitmap bmp= BitmapFactory.decodeResource(res,viewId);
        //ImageView view=new ImageView(context);

        imageView.setImageBitmap(bmp);
        //List<ImageView> list=new ArrayList<>();
        return view;
    }
}
