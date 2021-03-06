# MuliTouchdemo
##这是我的一个简单的可以两根手指触膜使图片放大缩小旋转的简单的自定义View的Demo
##几个需要注意的地方
###1.给自定义View添加自定义属性,即在xml中写入的属性
* 1.在res的values中添加attrs文件,如下
```javascript
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!--name属性名取阅读性高的名字-->
    <declare-styleable name="MutiView">
        <!--name属性名取阅读性高的名字   reference是资源文件id-->
        <attr name="src" format="reference"/>
        <attr name="textColor" format="color"/>
    </declare-styleable>
</resources>
```
[更多属性设置,点击这里](http://blog.csdn.net/heng615975867/article/details/12834833)
* 2.创建自定义View控件
通常在创建自定义View控件时,都是递归创建,如下
```javascript
 public MutiView(Context context) {
        this(context, null);
    }

  public MutiView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

  public MutiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
```
这样就可以只需要在一个构造方法里初始化属性了,初始化属性如下:
```javascript
  public MutiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //得到自定属性的数组
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MutiView);
        //得到src属性
        Drawable drawable = array.getDrawable(R.styleable.MutiView_src);
        //得到color属性
        int color = array.getColor(R.styleable.MutiView_textColor, 0x000);
    }
```
###2.GestureDetector手势监听器
GestureDetector中两个监听器,一个处理单击事件消息的OnGestureListener和一个处理双击事件的OnDoubleTapListener.

代码如下:
```javascript
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
```

然后在自定View的构造方法中初始化,代码如下:
```javascript
  public MutiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //设置手势监听器
        mGestureDetector = new GestureDetector(context, mGestureListener);
        //设置双击监听器
        mGestureDetector.setOnDoubleTapListener(mDoubleTapListener);
    }
```

###3.点击事件处理
```javascript
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
                    MotionEvent obtain = MotionEvent.obtain(
                      event.getDownTime(), 
                      event.getEventTime(), 
                      MotionEvent.ACTION_CANCEL, 
                      event.getX(), 
                      event.getY(), 
                      event.getMetaState());
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
```
###4.测量方法
```javascript
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
```
##总结
这个双手放大缩小的自定义View相对而言比较简单,技术有限,写的也比较乱.
