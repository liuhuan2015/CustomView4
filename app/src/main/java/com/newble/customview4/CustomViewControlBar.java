package com.newble.customview4;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Date: 2017/12/25 10:14
 * Description:环形音量调节控件
 * <p>
 * 思路:一,画出一个圆环 canvas.drawArc(...),圆环中间是一个图片
 * 这里里面涉及到一个坐标计算的问题,计算圆环内部图片区域的Rect,
 * 这里分为两种情况,图片比较大,区域可以与圆环形成内切关系;图片较小,无法和圆环形成内切关系.
 * 绘制图片的方法:canvas.drawBitmap(...)
 * 二,根据手势增加圆环数值,重绘圆环
 */

public class CustomViewControlBar extends View {
    /***第一圈的颜色**/
    private int mFirstColor;
    /***第二圈的颜色**/
    private int mSecondColor;
    /***圆的宽度**/
    private int mCircleWidth;
    /***画笔**/
    private Paint mPaint;
    /***当前进度**/
    private int mCurrentCount = 3;
    /***中间的图片**/
    private Bitmap mImage;
    /***每个块块间的间隙**/
    private int mSplitSize;
    /***个数**/
    private int mCount;

    private Rect mRect;


    public CustomViewControlBar(Context context) {
        this(context, null);
    }

    public CustomViewControlBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomViewControlBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomViewControlBar, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CustomViewControlBar_firstColor:
                    mFirstColor = a.getColor(attr, Color.GREEN);
                    break;
                case R.styleable.CustomViewControlBar_secondColor:
                    mSecondColor = a.getColor(attr, Color.CYAN);
                    break;
                case R.styleable.CustomViewControlBar_circleWidth:
                    mCircleWidth = a.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomViewControlBar_dotCount:
                    mCount = a.getInt(attr, 20);
                    break;
                case R.styleable.CustomViewControlBar_splitSize:
                    mSplitSize = a.getInt(attr, 20);
                    break;
                case R.styleable.CustomViewControlBar_bg:
                    mImage = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, 0));
                    break;
            }
        }
        a.recycle();

        mPaint = new Paint();
        mRect = new Rect();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setStrokeWidth(mCircleWidth);//设置圆环的宽度
        mPaint.setStrokeCap(Paint.Cap.ROUND);//定义线段断点形状为圆头
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);//设置空心
        int center = getWidth() / 2;//获取圆心的x坐标值
        int radius = center - mCircleWidth / 2;//半径
        /**画块块**/
        drawOval(canvas, center, radius);
        /***计算内切正方形的位置**/
        int relRadius = radius - mCircleWidth / 2;//获得内圆的半径
        /***内切正方形的左部位置**/
        mRect.left = (int) (relRadius - Math.sqrt(2) * 1.0f / 2 * relRadius) + mCircleWidth;
        /***内切正方形的顶部位置**/
        mRect.top = (int) (relRadius - Math.sqrt(2) * 1.0f / 2 * relRadius) + mCircleWidth;

        mRect.bottom = (int) (mRect.left + Math.sqrt(2) * relRadius);
        mRect.right = (int) (mRect.left + Math.sqrt(2) * relRadius);

        /**
         * 如果图片比较小,那么根据图片的尺寸放置到正中心
         */
        if (mImage.getWidth() < Math.sqrt(2) * relRadius) {
            mRect.left = (int) (mRect.left + Math.sqrt(2) * relRadius * 1.0f / 2 - mImage.getWidth() * 1.0f / 2);
            mRect.top = (int) (mRect.top + Math.sqrt(2) * relRadius * 1.0f / 2 - mImage.getHeight() * 1.0f / 2);
            mRect.right = mRect.left + mImage.getWidth();
            mRect.bottom = mRect.top + mImage.getHeight();
        }
        canvas.drawBitmap(mImage, null, mRect, mPaint);
    }

    /**
     * 根据参数画出每个小块
     *
     * @param canvas
     * @param center
     * @param radius
     */
    private void drawOval(Canvas canvas, int center, int radius) {
        /***根据需要画的个数以及间隙计算每个块块所占的比例**/
        float itemSize = (360 * 1.0f - mCount * mSplitSize) / mCount;

        RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius);//用于定义圆弧形状和大小的界限

        mPaint.setColor(mFirstColor);//设置圆弧的颜色
        for (int i = 0; i < mCount; i++) {
            canvas.drawArc(oval, i * (itemSize + mSplitSize), itemSize, false, mPaint);//根据进度绘制圆弧
        }

        mPaint.setColor(mSecondColor);
        for (int i = 0; i < mCurrentCount; i++) {
            canvas.drawArc(oval, i * (itemSize + mSplitSize), itemSize, false, mPaint);

        }
    }

    /**
     * 当前数量+1
     */
    public void up() {
        mCurrentCount++;
        postInvalidate();
    }

    /**
     * 当前数量-1
     */
    public void down() {
        mCurrentCount--;
        postInvalidate();
    }

    private int xDown, xUp;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                xUp = (int) event.getY();
                if (xUp > xDown) {
                    //下滑
                    down();
                } else {
                    //上滑
                    up();
                }
                break;
        }
        return true;
    }
}
