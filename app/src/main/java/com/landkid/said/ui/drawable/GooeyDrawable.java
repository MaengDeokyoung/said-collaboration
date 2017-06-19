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

import com.landkid.said.R;
import com.landkid.said.util.ResourceUtils;

/**
 * Created by SDS on 2017-04-24.
 */
public class GooeyDrawable extends Drawable implements Animatable {

    public static final long FRAME_DURATION = 1000 / 60;
    private static final String TAG = "CircleDrawable";
    private final Context mContext;

    private boolean reverse = false;

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

    private Interpolator mInterpolator;

    private int mColor;
    private Paint mPaint;

    private int mHeight;
    private int mWidth;
    private float mTotalProgress = 1000.0f;

    Viewport viewport;
    static final int DEFAULT_VIEWPORT_X = 220;
    static final int DEFAULT_VIEWPORT_Y = 100;

    int mParentCircleRadius;
    int mChildCircleRadius;

    class Viewport {
        int x;
        int y;

        Viewport(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    private float fraction;

    @Override
    public void setBounds(int left, int top, int right, int bottom) {

        mWidth = right - left;
        mHeight = bottom - top;

        fraction = mWidth / 100.0f;

        super.setBounds(left, top, right, bottom);
    }

    public GooeyDrawable(Context context, int color) {
        mContext = context;
        mInterpolator = new DecelerateInterpolator();
        mColor = color;
        mDuration = 500;
        mPadding = 20;
        mParentCircleRadius = 50;
        mChildCircleRadius = 40;
        viewport = new Viewport(DEFAULT_VIEWPORT_X, DEFAULT_VIEWPORT_Y);
    }

    public float getFraction() {
        return fraction;
    }

    public int getDuration() {
        return mDuration;
    }

    Interpolator getInterpolator(){
        return mInterpolator;
    }

    public static float dpToPx(float size, Context context){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, context.getResources().getDisplayMetrics());
    }

    public GooeyDrawable(Context context) {
        this(context, ResourceUtils.getColor(R.color.colorAccent, context));
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        Path path = new Path();
        //path.moveTo(dpToPx(50, mContext), dpToPx(50, mContext));

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
            default:
                draw(path, canvas, 0);
                break;
        }
    }

    void drawGooeyEffect(Path path, Canvas canvas){
        path.addCircle(sizeWithFraction(mParentCircleRadius), sizeWithFraction(mParentCircleRadius), sizeWithFraction(mParentCircleRadius), Path.Direction.CW);
        path.addCircle(sizeWithFraction((int) getCircleCenterOffset()), sizeWithFraction(mParentCircleRadius), sizeWithFraction(mChildCircleRadius), Path.Direction.CW);

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
        canvas.save();
        canvas.rotate(-90, sizeWithFraction(mParentCircleRadius), sizeWithFraction(mParentCircleRadius));
        canvas.translate(sizeWithFraction(-120), 0);
        canvas.drawPath(ovalPath, mPaint);
        canvas.drawPath(path, mPaint);
    }

    void drawSeparationEffect(Path path, Canvas canvas){

        path.addCircle(
                sizeWithFraction(mParentCircleRadius),
                sizeWithFraction(mParentCircleRadius),
                sizeWithFraction(mParentCircleRadius),
                Path.Direction.CW);
        path.addCircle(
                sizeWithFraction(mParentCircleRadius * 2 + mChildCircleRadius + mPadding),
                sizeWithFraction(mParentCircleRadius),
                sizeWithFraction(mChildCircleRadius),
                Path.Direction.CW);

        Path ovalPath = new Path();
        ovalPath.addArc(
                sizeWithFraction(- mPadding / 2 + (int) (mProgressAnim * mPadding / mTotalProgress)),
                0,
                sizeWithFraction(mParentCircleRadius * 2 + mPadding / 2 - (int) (mProgressAnim * mPadding / mTotalProgress)),
                sizeWithFraction(mParentCircleRadius * 2),
                -90,
                180);
        ovalPath.addArc(
                sizeWithFraction(mParentCircleRadius * 2 + mPadding / 2 + (int) (mProgressAnim * mPadding / mTotalProgress)),
                sizeWithFraction(getDifferC1AndC2()),
                sizeWithFraction(mParentCircleRadius * 2 + mChildCircleRadius * 2 + mPadding + mPadding / 2 - (int) (mProgressAnim * mPadding / mTotalProgress)),
                sizeWithFraction(mParentCircleRadius * 2 - getDifferC1AndC2()),
                90,
                180);

        canvas.save();
        canvas.rotate(-90, sizeWithFraction(mParentCircleRadius), sizeWithFraction(mParentCircleRadius));
        canvas.translate(sizeWithFraction(-120), 0);
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
        if(!reverse) {
            mFrame += 1;
        } else {
            mFrame -= 1;
        }

        interpolatedFraction = mFrame / ((mDuration / 2) / (FRAME_DURATION * 1.0f));

        if (0 < interpolatedFraction && interpolatedFraction <= 1) {
            mProgressAnim = (int) ((mTotalProgress) * new AccelerateInterpolator().getInterpolation(interpolatedFraction)) / 2.0f;
            if (isRunning())
                scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
            invalidateSelf();
        } else if (1 < interpolatedFraction && interpolatedFraction < 2) {
            mProgressAnim = (int) ((mTotalProgress) * new DecelerateInterpolator().getInterpolation(interpolatedFraction - 1)) / 2.0f;
            if (isRunning())
                scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
            invalidateSelf();
        } else {
            stop();
        }
    }

    private void resetAnimation() {
        if(!reverse){
            mFrame = 0;
        } else {
            mFrame = (int) ((mDuration) / (FRAME_DURATION * 1.0f));
        }

        mProgressAnim = 0;
    }

    public void reverse() {
        if (isRunning())
            return;
        reverse = true;
        mRunState = RUN_STATE_STARTING;
        resetAnimation();
        scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
        invalidateSelf();

    }

    @Override
    public void start() {
        if (isRunning())
            return;
        reverse = false;
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
        mRunState = RUN_STATE_STOPPED;
        unscheduleSelf(mUpdater);
        //invalidateSelf();
    }

    @Override
    public void scheduleSelf(Runnable what, long when) {
        super.scheduleSelf(what, when);
    }

    @Override
    public boolean isRunning() {
        return mRunState != RUN_STATE_STOPPED;
    }

    int getDifferC1AndC2(){
        return mParentCircleRadius - mChildCircleRadius;
    }

    float sizeWithFraction(float size){

        return fraction * size;
    };

    double getSinOffset(){
        return Math.sin((Math.PI * 2 / 4) * mProgressAnim / (mTotalProgress));
    }

    double getCosOffset(){
        return Math.cos((Math.PI * 2 / 4) * mProgressAnim / (mTotalProgress));
    }

    double getTanOffset(){
        return Math.tan((Math.PI * 2 / 4) * mProgressAnim / (mTotalProgress));
    }


    float getCircleCenterOffset(){
        return mParentCircleRadius + mProgressAnim * 2 / 10.0f + mPadding * mProgressAnim * 2 / mTotalProgress;
    }

    float getTopLeftX(){
        return sizeWithFraction((float) (mParentCircleRadius * (1 + getSinOffset())));
    }

    float getTopLeftY(){
        return sizeWithFraction((float) (mParentCircleRadius * (1 - getCosOffset())));
    }

    float getTopCenterX(){

        return (getTopLeftX() + getTopRightX()) * 0.5f;
    }

    float getTopCenterY(){

        return (float) (((getTopRightX() - getTopLeftX()) / 2) * getTanOffset() + getTopLeftY());
    }

    float getTopRightX(){
        return sizeWithFraction((float) (getCircleCenterOffset() - mChildCircleRadius * getSinOffset()));
    }

    float getTopRightY(){
        return sizeWithFraction((float) (mChildCircleRadius * (1 - getCosOffset())) + getDifferC1AndC2());
    }


    float getBottomLeftX(){
        return sizeWithFraction((float) (mParentCircleRadius * (1 + getSinOffset())));
    }

    float getBottomLeftY(){
        return sizeWithFraction(mParentCircleRadius * 2) - getTopLeftY();
    }

    float getBottomCenterX(){

        return getTopCenterX();
    }

    float getBottomCenterY(){

        return sizeWithFraction(mParentCircleRadius * 2) - getTopCenterY();
    }

    float getBottomRightX(){
        return sizeWithFraction((float) (getCircleCenterOffset() - mChildCircleRadius * getSinOffset()));
    }

    float getBottomRightY(){
        return sizeWithFraction(mParentCircleRadius * 2) - getTopRightY();
    }

}
