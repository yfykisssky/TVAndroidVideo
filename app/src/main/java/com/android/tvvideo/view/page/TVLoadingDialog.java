package com.android.tvvideo.view.page;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.tvvideo.R;


/**
 * Created by yangfengyuan on 16/7/26.
 */
public class TVLoadingDialog extends Dialog {

    public TVLoadingDialog(Context context) {
        super(context, R.style.Base_Dialog);

        initDialog(context);
    }

    public TVLoadingDialog(Context context, int themeResId) {
        super(context, R.style.Base_Dialog);

        initDialog(context);
    }

    private void initDialog(Context context){

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.dialog_tv_loading);

        ImageView imageView= (ImageView) this.findViewById(R.id.loading);

        imageView.setImageResource(R.drawable.loading);

        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);

        imageView.startAnimation(hyperspaceJumpAnimation);

        this.setCancelable(false);
    }


}
