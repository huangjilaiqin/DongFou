package com.lessask.dongfou;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class CircleView extends View {

    private Paint mBgPaint = new Paint();

    PaintFlagsDrawFilter pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);

    public CircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mBgPaint.setColor(Color.WHITE);
        mBgPaint.setAntiAlias(true);
    }

    public CircleView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mBgPaint.setColor(Color.WHITE);
        mBgPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int max = Math.max(measuredWidth, measuredHeight);
        setMeasuredDimension(max, max);
    }

    @Override
    public void setBackgroundColor(int color) {
        // TODO Auto-generated method stub
        mBgPaint.setColor(color);
    }

    @Override
    public void draw(Canvas canvas) {
        // TODO Auto-generated method stub
        canvas.setDrawFilter(pfd);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, Math.max(getWidth(), getHeight()) / 2, mBgPaint);
        //Log.e(CircleView.class.getSimpleName(), "draw circle");
        super.draw(canvas);
    }
}
