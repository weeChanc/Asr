package com.weechan.asr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * @author 214652773@qq.com
 * @user c
 * @create 2018/12/29 14:10
 */

public class WaveView extends View {

    private List<Short> waves;

    public WaveView(Context context, List<Short> waves) {
        super(context);
        this.waves = waves;
    }

    Paint mPaint;
    private float lineWidth = 4;

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.BLACK);
    }

    public List<Short> getWaves() {
        return waves;
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

    public Path setWaves(List<Short> waves,boolean invalidate) {

        if (waves == null || width == 0 ) {
            invalidate();
            return null;
        }

        lineWidth = (float) ((width + 0.0) / waves.size());
        Path path = new Path();
        float x = 0;
        float ratio = (float) (getHeight() / Math.pow(2, 16));
        for (Short wave : waves) {
            float startY = (getHeight() / 2 - wave * ratio);
            path.moveTo(x, startY);
            path.lineTo(x, startY + wave * ratio);
            x += lineWidth;
        }

        if(invalidate){
            this.path = path;
            invalidate();
        }
        return path;
    }

    int width = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (path != null)
            canvas.drawPath(path, mPaint);
    }

}
