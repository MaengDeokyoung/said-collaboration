package com.landkid.said.ui.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

public class GooeyDrawable extends Drawable implements Animatable {

    private static final long FRAME_DURATION = 1000 / 60;
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

    private float mPadding;

    private Interpolator mInterpolator;

    private int mColor;
    private Paint mPaint;

    private int mHeight;
    private int mWidth;
    private float mTotalProgress = 10000.0f;

    private Viewport mViewport;

    private float mParentCircleRadius;
    private float mChildCircleRadius;
    private float fraction = 0.0f;

    private boolean isGooeyed;

    private int mGravity;


    public boolean isGooeyed(){
        return isGooeyed;
    }

    public void setGooeyed(boolean isGooeyed){
        this.isGooeyed = isGooeyed;
    }


    private class Viewport {
        float width;
        float height;

        Viewport(float width, float height){
            this.width = width;
            this.height = height;
        }
    }

    @Override
    public int getIntrinsicWidth() {
        return mWidth != 0 ? mWidth : super.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return  mHeight != 0 ? mHeight : super.getIntrinsicHeight();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {

        mWidth = right - left;
        mHeight = bottom - top;
        fraction =  fraction == 0 ? mWidth / 100.0f : fraction;
        mViewport = new Viewport(relativeSize(mParentCircleRadius * 2 + mPadding + mChildCircleRadius * 2), relativeSize(mParentCircleRadius));
        super.setBounds(0, 0, (int) mViewport.width, (int)  mViewport.height);
    }

    public GooeyDrawable(Context context,
                         int color,
                         Interpolator interpolator,
                         int duration,
                         float parentCircleRadius,
                         float childCircleRadius,
                         int padding) {
        mContext = context;
        mInterpolator = interpolator;
        mColor = color;
        mDuration = duration;
        mPadding = padding;
        mParentCircleRadius = parentCircleRadius;
        mChildCircleRadius = childCircleRadius;
        mGravity = Gravity.TOP;
    }

    public GooeyDrawable(Context context,
                         int width,
                         int height,
                         int color,
                         Interpolator interpolator,
                         int duration,
                         float parentCircleRadius,
                         float childCircleRadius,
                         float padding) {
        mContext = context;
        mWidth = width;
        mHeight = height;
        fraction = width / 100.0f;
        mInterpolator = interpolator;
        mColor = color;
        mDuration = duration;
        mPadding = padding / fraction;
        mParentCircleRadius = parentCircleRadius / fraction;
        mChildCircleRadius = childCircleRadius / fraction;
        mGravity = Gravity.TOP;
    }

    public float getTranslationOffset(){
        return - relativeSize(mParentCircleRadius + mChildCircleRadius + mPadding);
    }

    public float getFraction() {
        return fraction;
    }

    public int getDuration() {
        return mDuration;
    }

    public GooeyDrawable setDuration(int duration) {
        mDuration = duration;
        return this;
    }

    public Interpolator getInterpolator(){
        return mInterpolator;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        Path path = new Path();

        switch (mRunState){
            case RUN_STATE_STARTING:
                mRunState = RUN_STATE_STARTED;
                draw(path, canvas, mGravity);
                break;
            case RUN_STATE_STARTED:
                draw(path, canvas, mGravity);
                break;
            case RUN_STATE_STOPPING:
                mProgressAnim = mTotalProgress;
                draw(path, canvas, mGravity);
                mRunState = RUN_STATE_STOPPED;
                break;
            case RUN_STATE_STOPPED:
                mProgressAnim = mTotalProgress;
                draw(path, canvas, mGravity);
                break;
            default:
                mProgressAnim = 0;
                draw(path, canvas, mGravity);
                break;
        }
    }

    private void drawGooeyEffect(Path path, Canvas canvas){
        path.addCircle(
                relativeSize(mParentCircleRadius),
                relativeSize(mParentCircleRadius),
                relativeSize(mParentCircleRadius),
                Path.Direction.CW);

        path.addCircle(relativeSize(
                mParentCircleRadius + getCircleCenterOffset()),
                relativeSize(mParentCircleRadius),
                relativeSize(mChildCircleRadius),
                Path.Direction.CW);

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
        canvas.rotate(-90, relativeSize(mParentCircleRadius), relativeSize(mParentCircleRadius));
        canvas.translate(relativeSize(- (mHeight / fraction - mParentCircleRadius * 2)), 0);
        canvas.drawPath(ovalPath, mPaint);
        canvas.drawPath(path, mPaint);
    }

    private void drawSeparationEffect(Path path, Canvas canvas){

        path.addCircle(
                relativeSize(mParentCircleRadius),
                relativeSize(mParentCircleRadius),
                relativeSize(mParentCircleRadius),
                Path.Direction.CW);
        path.addCircle(
                relativeSize(mParentCircleRadius * 2 + mChildCircleRadius + mPadding),
                relativeSize(mParentCircleRadius),
                relativeSize(mChildCircleRadius),
                Path.Direction.CW);

        Path ovalPath = new Path();
        ovalPath.addArc(
                relativeSize(- mPadding / 2 + (int) ((mProgressAnim - mTotalProgress / 2 ) * mPadding / mTotalProgress)),
                0,
                relativeSize(mParentCircleRadius * 2 + mPadding / 2 - (int) ((mProgressAnim - mTotalProgress / 2) * mPadding / mTotalProgress)),
                relativeSize(mParentCircleRadius * 2),
                -90,
                180);
        ovalPath.addArc(
                relativeSize(mParentCircleRadius * 2 + mPadding / 2 + (int) ((mProgressAnim - mTotalProgress / 2) * mPadding / mTotalProgress)),
                relativeSize(getDifferC1AndC2()),
                relativeSize(mParentCircleRadius * 2 + mChildCircleRadius * 2 + mPadding + mPadding / 2 - (int) ((mProgressAnim - mTotalProgress) * mPadding / mTotalProgress)),
                relativeSize(mParentCircleRadius * 2 - getDifferC1AndC2()),
                90,
                180);

        canvas.save();
        canvas.rotate(-90, relativeSize(mParentCircleRadius), relativeSize(mParentCircleRadius));
        canvas.translate(relativeSize( - (mHeight / fraction - mParentCircleRadius * 2)), 0);
        canvas.drawPath(ovalPath, mPaint);
        canvas.drawPath(path, mPaint);
    }

    private void draw(Path path, Canvas canvas, int gravity){

        switch (gravity){
            case Gravity.TOP:
                if(mCurrentValue <= 1) {
                    drawGooeyEffect(path, canvas);
                } else if(!isGooeyed) {
                    drawSeparationEffect(path, canvas);
                } else {
                    mProgressAnim = mTotalProgress / 2;
                    drawGooeyEffect(path, canvas);
                }
                break;
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

    private float mCurrentValue;

    private void update() {
        if(!reverse) mFrame += 1; else mFrame -= 1;

        mCurrentValue = mFrame / ((mDuration / 2) / (FRAME_DURATION * 1.0f));

        if(0 <= mCurrentValue && mCurrentValue < 2) {

            if (0 <= mCurrentValue && mCurrentValue <= 1) {
                mProgressAnim = (int) ((mTotalProgress) * mInterpolator.getInterpolation(mCurrentValue)) / 2.0f;
            } else if (1 < mCurrentValue && mCurrentValue < 2) {
                mProgressAnim = (int) ((mTotalProgress) * new LinearInterpolator().getInterpolation(mCurrentValue - 1)) / 2.0f + mTotalProgress / 2;
            }

            if (isRunning()) scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);

            invalidateSelf();

        } else {
            stop();
        }
    }

    private void resetAnimation() {
        if(!reverse){
            mFrame = 0;
            mProgressAnim = 0;
        } else {
            mFrame = (int) (mDuration / (FRAME_DURATION * 1.0f));
            mProgressAnim = mTotalProgress;
        }
    }

    public void reverse() {
        if (isRunning())
            return;
        reverse = true;
        mInterpolator = new LinearInterpolator();
        invalidate(RUN_STATE_STARTING);
    }

    @Override
    public void start() {
        if (isRunning())
            return;
        reverse = false;
        mInterpolator = new LinearInterpolator();
        invalidate(RUN_STATE_STARTING);
    }

    @Override
    public void stop() {
        if (!isRunning())
            return;
        invalidate(RUN_STATE_STOPPED);
    }

    private void invalidate(int runState){

        resetAnimation();
        mRunState = runState;
        switch (runState){
            case RUN_STATE_STARTING:
                scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
                break;
            case RUN_STATE_STOPPED:
                unscheduleSelf(mUpdater);
                break;
        }
        invalidateSelf();
    }

    @Override
    public boolean isRunning() {
        return mRunState != RUN_STATE_STOPPED;
    }

    private float getDifferC1AndC2(){
        return mParentCircleRadius - mChildCircleRadius;
    }

    private float relativeSize(float size){

        return fraction * size;
    }

    private double getSinOffset(){
        return Math.sin((Math.PI * 2 / 4) * mProgressAnim / (mTotalProgress));
    }

    private double getCosOffset(){
        return Math.cos((Math.PI * 2 / 4) * mProgressAnim / (mTotalProgress));
    }

    private double getTanOffset(){
        return Math.tan((Math.PI * 2 / 4) * mProgressAnim / (mTotalProgress));
    }

    private float getCircleCenterOffset(){
        return (mParentCircleRadius + mChildCircleRadius + mPadding) * mProgressAnim * 2 / mTotalProgress;
    }

    private float getTopLeftX(){
        return relativeSize((float) (mParentCircleRadius * (1 + getSinOffset())));
    }

    private float getTopLeftY(){
        return relativeSize((float) (mParentCircleRadius * (1 - getCosOffset())));
    }

    private float getTopCenterX(){
        return (getTopLeftX()) + (getTopRightX() - getTopLeftX()) * mParentCircleRadius / (mParentCircleRadius + mChildCircleRadius);
    }

    private float getTopCenterY(){

        return (float) (((getTopRightX() - getTopLeftX()) / 2) * getTanOffset() + getTopLeftY());
    }

    private float getTopRightX(){
        return relativeSize((float) (mParentCircleRadius + getCircleCenterOffset() - mChildCircleRadius * getSinOffset()));
    }

    private float getTopRightY(){
        return relativeSize((float) (mChildCircleRadius * (1 - getCosOffset())) + getDifferC1AndC2());
    }

    private float getBottomLeftX(){
        return relativeSize((float) (mParentCircleRadius * (1 + getSinOffset())));
    }

    private float getBottomLeftY(){
        return relativeSize(mParentCircleRadius * 2) - getTopLeftY();
    }

    private float getBottomCenterX(){
        return getTopCenterX();
    }

    private float getBottomCenterY(){
        return relativeSize(mParentCircleRadius * 2) - getTopCenterY();
    }

    private float getBottomRightX(){
        return relativeSize((float) (mParentCircleRadius + getCircleCenterOffset() - mChildCircleRadius * getSinOffset()));
    }

    private float getBottomRightY(){
        return relativeSize(mParentCircleRadius * 2) - getTopRightY();
    }

}
