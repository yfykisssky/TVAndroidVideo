package com.android.tvvideo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by yangfengyuan on 16/7/22.
 */
public class MarqueeText extends TextView implements Runnable {

    private int currentScrollX;
    private boolean isStop = false;
    private int textWidth;
    private Context context;
    private String text;
    int width;

    public MarqueeText(Context context) {
        super(context);

        this.context=context;

        this.setVisibility(View.VISIBLE);

    }
    public MarqueeText(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context=context;

        this.setVisibility(View.VISIBLE);

    }
    public MarqueeText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.context=context;

        this.setVisibility(View.VISIBLE);

    }

    public void setShowText(String text) {
        super.setText(text,BufferType.NORMAL);

        this.text=text;

        this.setTextColor(Color.parseColor("#00000000"));
    }


    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        Paint mPaint = new Paint();
        mPaint.setStrokeWidth(5);
        mPaint.setTextSize(50);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextAlign(Paint.Align.LEFT);

        Rect bounds = new Rect();
        mPaint.getTextBounds(text, 0,text.length(), bounds);

        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;

        canvas.drawText(text,0,baseline,mPaint);

        textWidth =bounds.width();
    }

    @Override
    public void run() {
        currentScrollX += 2;
        scrollTo(currentScrollX, 0);

        if (isStop) {
            return;
        }

        if(currentScrollX>textWidth){
            scrollTo(-width,0);
            currentScrollX = -width;
        }

        postDelayed(this, 15);
    }

    public void startScroll() {
        isStop = false;
        this.removeCallbacks(this);
        post(this);
    }

    public void stopScroll() {
        isStop = true;
    }

    public void startFor0() {

        this.measure(0,0);

        width=((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();

        this.postDelayed(new Runnable() {
            @Override
            public void run() {

                MarqueeText.this.setVisibility(View.VISIBLE);

                scrollTo(-width,0);
                currentScrollX = -width;
                startScroll();
            }
        },100);

    }

}
