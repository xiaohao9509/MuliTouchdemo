package com.nick.mulitouchdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;

/**
 * Created by Administrator on 2016/4/16.
 * User: Nick
 * Date: 2016/4/16
 * Email: 305812387@qq.com
 * Project: MuliTouchDemo
 */
public class MutiView extends View {

    private static final String TAG = MutiView.class.getSimpleName();
    private Matrix mMatrix;
    private Bitmap mBitmap;
    private float lastY = -1;
    private float lastX = -1;
    private float mLastDis;
    private float mLastDegrees;
    private boolean firstDouble;
    private float[] firstScale = new float[9];
    private GestureDetector.OnDoubleTapListener mDoubleTapListener = new GestureDetector.OnDoubleTapListener() {

        //超过500毫秒 调用单击完成事件
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //单击
            Log.i(TAG, "OnDoubleTapListener: onSingleTapConfirmed: 单击");
            return false;
        }

        //双击事件
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(TAG, "OnDoubleTapListener: onDoubleTap: 双击");
//            mMatrix.postScale(2, 2, e.getX(), e.getY());
//            mMatrix.postRotate(45,e.getX(),e.getY());
            float[] values = new float[9];
            mMatrix.getValues(values);
            if (!firstDouble) {
                firstDouble = true;
                firstScale = values;
            }
            System.out.println(Arrays.toString(values));
            if (values[0] > 10) {
//                mMatrix.setScale(1, 1, e.getX(), e.getY());
                mMatrix.setValues(firstScale);
                firstDouble = false;
            } else {
                mMatrix.postScale(2, 2, e.getX(), e.getY());

            }
            return false;
        }

        //双击抬起
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.i(TAG, "OnDoubleTapListener: onDoubleTapEvent: 双击手指离开屏幕");
            return false;
        }
    };

    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            Log.i(TAG, "OnGestureListener: onDown: 手指按下屏幕");
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.i(TAG, "OnGestureListener: onShowPress: 手指按下屏幕且未达到长按时间");
        }

        //单击事件抬起
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.i(TAG, "OnGestureListener: onSingleTapUp: 单击后手指离开屏幕");
            return false;
        }

        //滚动事件
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i(TAG, "OnGestureListener: onScroll: 手指在屏幕上滑动");
            mMatrix.postTranslate(-distanceX, -distanceY);
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i(TAG, "OnGestureListener: onLongPress: 手指长按");

        }

        //抛动事件,手指离开,惯性滑动
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.i(TAG, "OnGestureListener: onFling: 手指离开屏幕后,惯性滑动");
            return false;
        }
    };
    private GestureDetector mGestureDetector;


    public MutiView(Context context) {
        this(context, null);
    }

    public MutiView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MutiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMatrix = new Matrix();
//        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MutiView);
        Drawable drawable = array.getDrawable(R.styleable.MutiView_src);
        int color = array.getColor(R.styleable.MutiView_textColor, 0x000);
        if (drawable != null && drawable instanceof BitmapDrawable) {
            mBitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        //设置手势监听器
        mGestureDetector = new GestureDetector(context, mGestureListener);
        //设置双击监听器
        mGestureDetector.setOnDoubleTapListener(mDoubleTapListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, mMatrix, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        if (event.getPointerCount() == 1) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    lastY = event.getY();
//                    lastX = event.getX();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    if (lastX != -1 && lastY != -1) {
//                        mMatrix.postTranslate(event.getX() - lastX, event.getY() - lastY);
//                    }
//                    lastX = event.getX();
//                    lastY = event.getY();
//                    break;
//            }
        } else if (event.getPointerCount() == 2) {
            //得到多点触控事件
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_DOWN:
//                    lastX = -1;
//                    lastY = -1;
                    //当第二根手指按下时,取消以前的单击事件
                    MotionEvent obtain = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_CANCEL, event.getX(), event.getY(), event.getMetaState());
                    mGestureDetector.onTouchEvent(obtain);
                    //获得第一点与第二点的距离
                    mLastDis = (float) Math.sqrt(Math.pow(event.getX(0) - event.getX(1), 2) + Math.pow(event.getY(0) - event.getY(1), 2));
                    //获得第一点与第二点的角度差
                    mLastDegrees = (float) Math.toDegrees(Math.atan2(event.getY(0) - event.getY(1), event.getX(0) - event.getX(1)));
                    break;
                case MotionEvent.ACTION_MOVE:
                    float px = (event.getX(0) + event.getX(1)) / 2;
                    float py = (event.getY(0) + event.getY(1)) / 2;
                    //获得第一点和第二点的距离
                    float dis = (float) Math.sqrt(Math.pow(event.getX(0) - event.getX(1), 2) + Math.pow(event.getY(0) - event.getY(1), 2));
                    //获得第一点与第二点的角度差
                    float degrees = (float) Math.toDegrees(Math.atan2(event.getY(0) - event.getY(1), event.getX(0) - event.getX(1)));
                    //设置放大缩小的的倍数
                    mMatrix.postScale(dis / mLastDis, dis / mLastDis, px, py);
                    //设置旋转角度
                    mMatrix.postRotate(degrees - mLastDegrees, px, py);
                    //保存这次的滑动距离和旋转角度
                    mLastDis = dis;
                    mLastDegrees = degrees;
                    break;
            }
        }
        //重回控件
        invalidate();
        return true;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = 0;

        if (mBitmap != null) {
            width = mBitmap.getWidth();
            height = mBitmap.getHeight();
        }

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heigthMode = MeasureSpec.getMode(heightMeasureSpec);

        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                widthSize = Math.min(width, widthSize);
                break;
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.UNSPECIFIED:
                widthSize = width;
                break;
        }
        switch (heigthMode) {
            case MeasureSpec.AT_MOST:
                heightSize = Math.min(heightSize, height);
                break;
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.UNSPECIFIED:
                heightSize = height;
                break;
        }
        if (mBitmap != null) {
            float min = Math.min(widthSize / (float) width, heightSize / (float) height);
            System.out.println(getMeasuredHeight() + ":" + heightSize * min);

            mMatrix.setScale(min, min, 0, getMeasuredHeight() / 2 - heightSize * min / 6);
        }

        setMeasuredDimension(widthSize, heightSize);

    }
}






