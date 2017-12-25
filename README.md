# CustomView4
自定义控件学习4,一个圆形音量调节控件<br>
效果:一个圆形的音量调节控件,中间有一张图片<br>
用到的Canvas的方法有
>canvas.drawArc(...)绘制弧形<br>
>canvas.drawBitmap(...)绘制图片<br>

实现思路:一,自定义一些属性,在构造方法中获取,比如:第一层颜色,第二层颜色,圆环宽度,圆环分为多少个item(音量调节范围)

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
    
二,在onDraw(...)里面进行界面的绘制,涉及到一些坐标点的计算,圆环和其内切正方形(这个地方画出来一张图来还是比较好理解的)等

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

