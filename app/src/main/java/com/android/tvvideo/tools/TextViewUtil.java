package com.android.tvvideo.tools;

import android.graphics.Paint;
import android.widget.TextView;

/**
 * Created by yfykisssky on 16/7/27.
 */
public class TextViewUtil {

    String text;
    // 总共可以放多少个字
    int count = 0;
    Paint paint = new Paint();
    // textView全部字符的宽度
    float textTotalWidth = 0.0f;
    // textView一个字的宽度
    float textWidth = 0.0f;

    public void setText(String data, TextView rightTextView, TextView bottomTextView) {

        this.text=data;

        int width=820;
        int height=1080;
        // 屏幕的高度
        int screenWidth = 0;

        /**
         * 获取一个字的宽度
         */
        textWidth = rightTextView.getTextSize();
        paint.setTextSize(textWidth);

        // 一行字体的高度
        int lineHeight = rightTextView.getLineHeight();
        // 可以放多少行
        int lineCount = (int) Math.ceil((double)width/ (double) lineHeight);
        // 一行的宽度
        float rowWidth =1100;
        // 一行可以放多少个字
        int columnCount = (int) (rowWidth / textWidth);

        // 总共字体数等于 行数*每行个数
        count = lineCount * columnCount;
        // 一个TextView中所有字符串的宽度和（字体数*每个字的宽度）
        textTotalWidth = (float) ((float) count * textWidth);

        measureText();
        rightTextView.setText(data.substring(0, count+345));

        bottomTextView.setText(data.substring(count+345));
    }

    /**
     * 测量已经填充的长度，计算其剩下的长度
     */
    private void measureText() {
        String string = text.substring(0, count);
        float size = paint.measureText(string);
        int remainCount = (int) ((textTotalWidth - size) / textWidth);
        if (remainCount > 0) {
            count += remainCount;
            measureText();
        }
    }
}

