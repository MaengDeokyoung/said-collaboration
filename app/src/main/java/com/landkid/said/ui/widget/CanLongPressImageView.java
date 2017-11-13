package com.landkid.said.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by sds on 2017. 8. 23..
 */

public class CanLongPressImageView extends AppCompatImageView {

    private OnPressListener mOnPressListener;

    public CanLongPressImageView(Context context) {
        this(context, null, 0);
    }

    public CanLongPressImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanLongPressImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    GestureDetector mGestureDetector;

    private void initView(Context context) {
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return mOnPressListener != null && mOnPressListener.onSingleTapUp(motionEvent);
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                if(mOnPressListener != null)
                    mOnPressListener.onLongPress(motionEvent);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return mGestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    public interface OnPressListener {
        void onLongPress(MotionEvent event);
        boolean onSingleTapUp(MotionEvent event);
    }

    public void setOnPressListener(OnPressListener onLongPressListener){
        mOnPressListener = onLongPressListener;
    }

}
