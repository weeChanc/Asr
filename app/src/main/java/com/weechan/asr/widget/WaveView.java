package com.weechan.asr.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * @author 214652773@qq.com
 * @user c
 * @create 2018/12/29 14:10
 */

public class WaveView extends View {

    private Paint mPaint;
    private float lineWidth = 3f;
    private int mode = 1; // normal
    private final static int LAST_MODE = 0; // display last

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.BLACK);
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    private Path path;

    public WaveView(Context context) {
        super(context);
    }

    public Path getPath() {
        return path;
    }


    public void setPath(Path path) {
        this.path = path;
    }

    public Path setWaves(List<Short> waves, boolean invalidate) {

        if (waves == null) {
            invalidate();
            return null;
        }

        Path path = new Path();
        float x = 0;
        float ratio = (float) (getHeight() / Math.pow(2, 16));

        if(mode == 0){
            if(waves.size() > 1200){
                for(int i = waves.size() -  1200 ; i < waves.size() ; i++ ){
                    short wave = waves.get(i);
                    float startY = (getHeight() / 2 - wave * ratio);
                    path.moveTo(x, startY);
                    path.lineTo(x, startY + wave * ratio);
                    x += lineWidth/6;
                }
            }else{
                for (Short wave : waves) {
                    float startY = (getHeight() / 2 - wave * ratio);
                    path.moveTo(x, startY);
                    path.lineTo(x, startY + wave * ratio);
                    x += lineWidth/6;
                }
            }
        }else{
            for (Short wave : waves) {
                float startY = (getHeight() / 2 - wave * ratio);
                path.moveTo(x, startY);
                path.lineTo(x, startY + wave * ratio);
                x += lineWidth/6;
            }
        }

        setPath(path);
        if(invalidate){
            invalidate();
            setMeasuredDimension((int) x,getHeight());
        }

        return path;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int modelW = MeasureSpec.getMode(widthMeasureSpec);
        int modelH = MeasureSpec.getMode(heightMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);

        if (modelH == MeasureSpec.AT_MOST) {
            h = dp2px(84);
        }

        if (modelW == MeasureSpec.AT_MOST) {
            View view = (View) getParent();
            if (view != null) {
                w = view.getMeasuredWidth();
            } else {
                w = dp2px(240);
            }
        }

        setMeasuredDimension(w, h);

//        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }

    private int dp2px(final float dpValue) {
        final float scale = this.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (path != null)
            canvas.drawPath(path, mPaint);
    }

}
