package com.weechan.asr.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.Nullable;

public class TextBoard extends View {
    private Paint mPaint;
    private int maxPos;

    private SparseArray<String> texts;

    public TextBoard(Context context) {
        super(context);
    }

    public TextBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setTextSize(dp2px(4));
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(dp2px(24));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        texts = new SparseArray<>();

    }


    public void setTexts(SparseArray<String> texts) {
        this.texts = texts;
        maxPos = texts.keyAt(texts.size() - 1);
        invalidate();
    }

    private Rect textBound = new Rect();
    private int width;
    private int height;

    @Override
    protected void onDraw(Canvas canvas) {

        if(isInEditMode()){
            texts.put(200, "text");
            texts.put(3200, "view");
            texts.put(4300," ");
            maxPos = 4300;
        }


        for (int i = 0; i < texts.size(); i++) {
            int pos = texts.keyAt(i);
            String text = texts.get(pos);
            mPaint.getTextBounds(text, 0, text.length(), textBound);
            canvas.drawText(text, pos * ((width + 0.0f) / maxPos) - textBound.width() / 2f, height / 2f + textBound.height() / 2f, mPaint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED){
            height = dp2px(24);
            width = MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(width,height);
            Log.e("TextBoard","IN");
        }else{
            height = getMeasuredHeight();
            width = getMeasuredWidth();
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        }

    }

    private int dp2px(final float dpValue) {
        final float scale = this.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
