package com.landkid.said.ui.drawable;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by SDS on 2017-04-24.
 */
public class GooeyDrawable extends Drawable implements Animatable {

    public static final long FRAME_DURATION = 1000 / 60;
    private static final String TAG = "CircleDrawable";
    private final Context mContext;

    private int mDuration;

    private int mRunState = RUN_STATE_STOPPED;
    private float mProgressAnim = 0;

    private static final int RUN_STATE_STOPPED = 0;
    private static final int RUN_STATE_STARTING = 1;
    private static final int RUN_STATE_STARTED = 2;
    private static final int RUN_STATE_RUNNING = 3;
    private static final int RUN_STATE_STOPPING = 4;
    private int mFrame;

    private int mStrokePx = 0;
    private int mPadding;

    private int mFirstMoveX;
    private int mFirstMoveY;

    private int mSecondMoveX;
    private int mSecondMoveY;

    private Interpolator mInterpolator;

    private int mColor;
    private Paint mPaint;

    private long mLastUpdateTime;
    private int strokeWidth = 5;
    private int circleRadius;
    private int mHeight;
    private int mWidth;
    private float mTotalProgress = 1000.0f;

    Viewport viewport;
    static final int DEFAULT_VIEWPORT_X = 220;
    static final int DEFAULT_VIEWPORT_Y = 100;

    class Viewport {
        int x;
        int y;

        Viewport(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    private int fraction;

    @Override
    public void setBounds(int left, int top, int right, int bottom) {

        mWidth = right - left;
        mHeight = bottom - top;

        fraction = mWidth / 220;

        super.setBounds(left, top, right, bottom);
    }

    public GooeyDrawable(Context context, int color) {
        mContext = context;
        mInterpolator = new DecelerateInterpolator();
        mColor = color;
        mDuration = 5000;
        mPadding = 50;
        viewport = new Viewport(DEFAULT_VIEWPORT_X, DEFAULT_VIEWPORT_Y);
    }

    Interpolator getInterpolator(){
        return mInterpolator;
    }

    public static float dpToPx(float size, Context context){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, context.getResources().getDisplayMetrics());
    }

    public GooeyDrawable(Context context) {
        this(context, 0xFF0e82f3);
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        Path path = new Path();
        path.moveTo(dpToPx(50, mContext), dpToPx(50, mContext));

        switch (mRunState){
            case RUN_STATE_STARTING:
                mRunState = RUN_STATE_STARTED;
                break;
            case RUN_STATE_STARTED:
                draw(path, canvas, 0);
                break;

            case RUN_STATE_STOPPING:
                mProgressAnim = mTotalProgress / 2;
                mProgressAnim2 = mTotalProgress / 2;
                draw(path, canvas, 0);
                mRunState = RUN_STATE_STOPPED;
                break;
        }
    }

    int sizeWithFraction(int size){

        return fraction * size;
    };

    float sizeWithFraction(float size){

        return fraction * size;
    };

    double getSinOffset(){
        return Math.sin((Math.PI * 2 / 5) * mProgressAnim / (mTotalProgress));
    }

    double getCosOffset(){
        return Math.cos((Math.PI * 2 / 5) * mProgressAnim / (mTotalProgress));
    }

    double getTanOffset(){
        return Math.tan((Math.PI * 2 / 5) * mProgressAnim / (mTotalProgress));
    }


    float getCircleCenterOffset(){
        return 50 + mProgressAnim * 2 / 10.0f + mPadding;
    }

    float getTopLeftX(){
        return sizeWithFraction((float) (50 * (1 + getSinOffset())));
    }

    float getTopLeftY(){
        return sizeWithFraction((float) (50 * (1 - getCosOffset())));
    }

    float getTopCenterX(){

        return (getTopLeftX() + getTopRightX()) / 2;
    }

    float getTopCenterY(){

        return (float) (((getTopRightX() - getTopLeftX()) / 2) * getTanOffset() + getTopLeftY());
    }

    float getTopRightX(){
        return sizeWithFraction((float) (getCircleCenterOffset() - 50 * getSinOffset()));
    }

    float getTopRightY(){
        return sizeWithFraction((float) (50 * (1 - getCosOffset())));
    }


    float getBottomLeftX(){
        return sizeWithFraction((float) (50 * (1 + getSinOffset())));
    }

    float getBottomLeftY(){
        return sizeWithFraction(100) - getTopLeftY();
    }

    float getBottomCenterX(){

        return (getBottomLeftX() + getBottomRightX()) / 2;
    }

    float getBottomCenterY(){

        return sizeWithFraction(100) - getTopCenterY();
    }

    float getBottomRightX(){
        return sizeWithFraction((float) (getCircleCenterOffset() - 50 * getSinOffset()));
    }

    float getBottomRightY(){
        return sizeWithFraction(100) - getTopRightY();
    }

//    float getBottomLeftX(){
//        return ;
//    }

//    float getBottomRightX(){
//        return ;
//    }



    void drawGooeyEffect(Path path, Canvas canvas){
        path.addCircle(sizeWithFraction(50), sizeWithFraction(50), sizeWithFraction(50), Path.Direction.CW);
        path.addCircle(sizeWithFraction((int) getCircleCenterOffset()), sizeWithFraction(50), sizeWithFraction(50), Path.Direction.CW);

        path.moveTo(
                getTopLeftX(),
                getTopLeftY());

        path.quadTo(
                getTopCenterX(),
                getTopCenterY(),
                getTopRightX(),
                getTopRightY());

        path.lineTo(
                getBottomRightX(),
                getBottomRightY());

        path.quadTo(
                getBottomCenterX(),
                getBottomCenterY(),
                getBottomLeftX(),
                getBottomLeftY());

        path.lineTo(
                getTopLeftX(),
                getTopLeftY());


        Path ovalPath = new Path();

        canvas.drawPath(ovalPath, mPaint);

        canvas.drawPath(path, mPaint);
    }

    void drawSeparationEffect(Path path, Canvas canvas){

        path.addCircle(sizeWithFraction(50), sizeWithFraction(50), sizeWithFraction(50), Path.Direction.CW);
        path.addCircle(sizeWithFraction(150 + mPadding), sizeWithFraction(50), sizeWithFraction(50), Path.Direction.CW);

        Path ovalPath = new Path();
        ovalPath.addArc(
                sizeWithFraction(- mPadding / 2 + (int) (mProgressAnim * mPadding / mTotalProgress)),
                0,
                sizeWithFraction(100 + mPadding / 2 - (int) (mProgressAnim * mPadding / mTotalProgress)),
                sizeWithFraction(100),
                -90,
                180);
        ovalPath.addArc(
                sizeWithFraction(100 + mPadding / 2 + (int) (mProgressAnim * mPadding / mTotalProgress)),
                0,
                sizeWithFraction(200 + mPadding + mPadding / 2 - (int) (mProgressAnim * mPadding / mTotalProgress)),
                sizeWithFraction(100),
                90,
                180);

        canvas.drawPath(ovalPath, mPaint);
        canvas.drawPath(path, mPaint);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void draw(Path path, Canvas canvas, int gravity){
        if(interpolatedFraction <= 1) {
            drawGooeyEffect(path, canvas);
        } else {
            drawSeparationEffect(path, canvas);
        }
    }


    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    private final Runnable mUpdater = new Runnable() {

        @Override
        public void run() {
            update();
        }

    };

    long startTime = 0;

    float mProgressAnim2;

    float interpolatedFraction;

    private void update() {
        long curTime = SystemClock.uptimeMillis();
        mLastUpdateTime = curTime;
        mFrame += 1;
        interpolatedFraction = mFrame / ((mDuration / 2) / (FRAME_DURATION * 1.0f));

        if(interpolatedFraction <= 1) {
            mProgressAnim = (int)((mTotalProgress) * new AccelerateInterpolator().getInterpolation(interpolatedFraction)) / 2.0f;
            if(isRunning())
                scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
            invalidateSelf();
        } else if(interpolatedFraction < 2) {
            mProgressAnim = (int)((mTotalProgress) * new DecelerateInterpolator().getInterpolation(interpolatedFraction - 1)) / 2.0f;
            if(isRunning())
                scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
            invalidateSelf();
        } else {
            stop();
        }
    }

    private void resetAnimation() {
        mFrame = 0;
        mProgressAnim = 0;
        mProgressAnim2 = 0;
    }

    @Override
    public void start() {
        if (isRunning())
            return;
        mRunState = RUN_STATE_STARTING;
        resetAnimation();
        scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
        invalidateSelf();

    }

    @Override
    public void stop() {
        if (!isRunning())
            return;
        resetAnimation();
        mRunState = RUN_STATE_STOPPING;
        unscheduleSelf(mUpdater);
        invalidateSelf();
    }

    @Override
    public void scheduleSelf(Runnable what, long when) {
        super.scheduleSelf(what, when);
    }

    @Override
    public boolean isRunning() {
        return mRunState != RUN_STATE_STOPPED;
    }

}
