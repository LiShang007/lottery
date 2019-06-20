package com.lishang.lottery.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LotterySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mHolder = null; //控制对象
    private Canvas mCanvas; //画布
    private boolean isCanvas;//
    private int lotteryResult = -1;//设置抽奖结果
    private int lotteryCurrent; //当前抽奖的位置
    private int MAX_TRANSTER = 10;//最大旋转次数 默认10
    private List<Rect> rects = new ArrayList<>(); //用于存放奖品位置
    private int margin = 0; //奖品间距
    private boolean isClickLottery; //点击的是否是抽奖区域
    private boolean isLottering; //抽奖中
    private OnLotteryListener listener;
    private LotteryAdapter adapter; //抽奖适配器

    private Handler mHandler = new Handler(Looper.getMainLooper()); //主线程

    public LotterySurfaceView(Context context) {
        this(context, null);
    }

    public LotterySurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LotterySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHolder = this.getHolder();
        mHolder.addCallback(this);
        //设置背景透明
        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    /**
     * 重新测量
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int size = Math.min(widthSize, heightSize);
        if (widthMode == MeasureSpec.AT_MOST) {
            //wrap_content
            widthSize = size;
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            //wrap_content
            heightSize = size;
        }
        setMeasuredDimension(widthSize, heightSize);

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isCanvas = true;

        initPrize();

        AsyncTask.SERIAL_EXECUTOR.execute(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isCanvas = false;
        mHolder.removeCallback(this);
    }

    /**
     * 设置奖品距离
     *
     * @param margin
     */
    public void setMargin(int margin) {
        this.margin = margin;
    }

    /**
     * 设置中奖结果
     *
     * @param lotteryResult
     */
    public void setLotteryResult(int lotteryResult) {
        this.lotteryResult = lotteryResult;
    }

    /**
     * 获取中奖结果
     *
     * @return
     */
    public int getLotteryResult() {
        return lotteryResult;
    }

    /**
     * 获取当前抽奖位置
     *
     * @return
     */
    public int getLotteryCurrent() {
        return lotteryCurrent;
    }

    /**
     * 设置当前抽奖位置
     *
     * @param lotteryCurrent
     */
    public void setLotteryCurrent(int lotteryCurrent) {
        this.lotteryCurrent = lotteryCurrent;
    }

    /**
     * 获取奖品间隔
     *
     * @return
     */
    public int getMargin() {
        return margin;
    }

    /**
     * 是否在抽奖中
     *
     * @return
     */
    public boolean isLottering() {
        return isLottering;
    }

    @Override
    public void run() {
        while (isCanvas) {
            mCanvas = mHolder.lockCanvas();
            drawPrize();
            mHolder.unlockCanvasAndPost(mCanvas); // 完成画画，把画布显示在屏幕上

            if (isLottering) {
                drawLotteryResult();
            }
        }
    }

    private void initPrize() {
        //绘制奖品
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();

        //去除padding后的最小长度
        int widthSize = getMeasuredWidth() - left - right;
        int heightSize = getMeasuredHeight() - top - bottom;
        int width = Math.min(widthSize, heightSize);

        //居中显示
        if (widthSize - width > 0) {
            left += (widthSize - width) / 2;
        }
        if (heightSize - width > 0) {
            top += (heightSize - width) / 2;
        }

        //奖品大小
        int size = Math.round((width - 2 * margin) / 3.0f);

        rects.removeAll(rects);
        for (int i = 0; i < 9; i++) {
            Rect rect = new Rect();
            if (i < 3) {
                rect.left = (size + margin) * i;
                rect.top = top;

            } else if (i < 5) {
                rect.left = (size + margin) * 2;
                rect.top = top + (size + margin) * (i - 2);
            } else if (i < 7) {
                rect.left = (size + margin) * (2 - i / 3);
                rect.top = top + (size + margin) * 2;
            } else {
                rect.left = (size + margin) * (i - 7);
                rect.top = top + (size + margin);
            }
            rect.left += left;
            rect.right = rect.left + size;
            rect.bottom = rect.top + size;

            rects.add(rect);
        }
    }


    private void drawPrize() {

        //绘制奖品
        for (int i = 0; i < rects.size(); i++) {
            Rect rect = rects.get(i);

            if (lotteryResult != -1 && !isLottering && lotteryResult == i) {
                if (adapter != null) {
                    adapter.onDrawLottery(mCanvas, rect, i);
                }
            } else {
                if (adapter != null) {
                    adapter.onDrawPrize(mCanvas, rect, i);
                }
            }

        }
    }

    private void drawLotteryResult() {


        int count = (int) Math.round(Math.random() * MAX_TRANSTER + 1);


        int off = 0;
        if (lotteryCurrent - lotteryResult <= 0) {
            //5 - 7 < 0   +2 差两格
            off = lotteryResult - lotteryCurrent;

        } else {
            //7 - 5 >0  +6 = 8- 7+ 5
            off = rects.size() - 1 - lotteryCurrent + lotteryResult;
        }

        int total = count * (rects.size() - 1) + off;//跑几格子
        int sleep = 50;
        for (int i = 0; i < total; i++) {
            mCanvas = mHolder.lockCanvas();

            drawPrize();
            int size = rects.size() - 1;

            int index = (lotteryCurrent + i) % (size);
            Rect rect = rects.get(index);


            if (adapter != null) {
                adapter.onDrawLottery(mCanvas, rect, index);
            }

            mHolder.unlockCanvasAndPost(mCanvas); // 完成画画，把画布显示在屏幕上

            try {
                Thread.sleep(sleep);
                if (total - i < (size) + (size / 2)) {
                    sleep += 5;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
        lotteryCurrent = lotteryResult;
        if (listener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onLotteryResult(lotteryResult);
                }
            });
        }
        isLottering = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Rect rect = rects.get(rects.size() - 1);
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isClickLottery = rect.contains(x, y);
                break;
            case MotionEvent.ACTION_UP:
                if (isClickLottery && rect.contains(x, y)) {
                    if (isLottering) {
                        Log.d("LotterySurfaceView", "lottery is running");
                        return true;
                    }
                    if (listener != null) {
                        listener.onStart(this);
                    }
                    isLottering = true;

                }
                break;
        }
        return true;
    }

    public void setOnLotteryListener(OnLotteryListener listener) {
        this.listener = listener;
    }

    public void setLotteryAdapter(LotteryAdapter adapter) {
        this.adapter = adapter;
    }


    public interface OnLotteryListener {
        /**
         * 点击抽奖按钮
         *
         * @param view
         */
        void onStart(LotterySurfaceView view);

        /**
         * 抽奖结果
         *
         * @param result
         */
        void onLotteryResult(int result);
    }

    public abstract static class LotteryAdapter {
        /**
         * 绘制奖品
         *
         * @param canvas
         * @param rect
         * @param position
         */
        public abstract void onDrawPrize(Canvas canvas, Rect rect, int position);

        /**
         * 抽奖的效果
         *
         * @param canvas
         * @param rect
         * @param position
         */
        public abstract void onDrawLottery(Canvas canvas, Rect rect, int position);
    }

}
