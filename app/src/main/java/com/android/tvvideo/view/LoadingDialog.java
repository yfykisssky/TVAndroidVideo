package com.android.tvvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.hospital.video.R;


/**
 * Created by yangfengyuan on 16/7/26.
 */
public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        super(context, R.style.Base_Dialog);

        initDialog(context);
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, R.style.Base_Dialog);

        initDialog(context);
    }

    private void initDialog(Context context){

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.dialog_loading);

        ImageView imageView= (ImageView) this.findViewById(R.id.loading);

        imageView.setImageResource(R.drawable.loading);

        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);

        imageView.startAnimation(hyperspaceJumpAnimation);

        this.setCancelable(false);
    }


}
