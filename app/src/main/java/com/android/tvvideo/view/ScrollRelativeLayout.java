package com.android.tvvideo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by yangfengyuan on 16/7/29.
 */
public class ScrollRelativeLayout extends RelativeLayout{

    private boolean isShow=true;

    public boolean isShow() {
        return isShow;
    }

    public ScrollRelativeLayout(Context context) {
        super(context);
    }

    public ScrollRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void show(){

        this.setFocusable(false);

        this.measure(0,0);

        final int width=this.getMeasuredWidth()/10;

        if(!isShow){

            isShow=true;

            new Thread(new Runnable() {
                @Override
                public void run() {

                    for(int c=width;c>=0;c--){

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        final int finalC = c;
                        ScrollRelativeLayout.this.post(new Runnable() {
                            @Override
                            public void run() {

                                ScrollRelativeLayout.this.scrollTo(finalC *10,0);

                            }
                        });

                    }

                    ScrollRelativeLayout.this.post(new Runnable() {
                        @Override
                        public void run() {
                            ScrollRelativeLayout.this.setFocusable(true);
                        }
                    });

                }
            }).start();

        }
    }

    public void hide(){

        this.setFocusable(false);

        this.measure(0,0);

        final int width=this.getMeasuredWidth()/10;

        if(isShow) {

            isShow=false;

            new Thread(new Runnable() {
                @Override
                public void run() {

                    for (int c = 1; c <= width; c++) {

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        final int finalC = c;
                        ScrollRelativeLayout.this.post(new Runnable() {
                            @Override
                            public void run() {

                                ScrollRelativeLayout.this.scrollTo(finalC * 10, 0);

                            }
                        });

                    }

                    ScrollRelativeLayout.this.post(new Runnable() {
                        @Override
                        public void run() {
                            ScrollRelativeLayout.this.setFocusable(true);
                        }
                    });

                }
            }).start();

        }

    }

}
