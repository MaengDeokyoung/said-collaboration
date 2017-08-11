package com.landkid.said.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.landkid.said.util.ViewUtils;

/**
 * Created by sds on 2017. 8. 3..
 */

public class DragDismissLayout extends FrameLayout {

    float startEventX;
    float startTranslationX;
    float currentEventX;
    float previousEventX;

    private static final String TAG = "DragDismissLayout";
    int touchSlop;
    boolean draggingLeft = false;

    OnDragDismissListener mOnDragDismissListener;


    public DragDismissLayout(@NonNull Context context) {
        this(context, null, 0);
    }

    public DragDismissLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragDismissLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ViewConfiguration viewConfiguration =  ViewConfiguration.get(context);
        touchSlop = viewConfiguration.getScaledTouchSlop();
    }

    public void setOnDragDismissListener(OnDragDismissListener onDragDismissListener) {
        this.mOnDragDismissListener = onDragDismissListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.getActionIndex();
                currentEventX = ev.getX(pointerIndex) + getTranslationX() + touchSlop;

                if (currentEventX - startEventX < 0)
                    setTranslationX(currentEventX - startEventX);

                previousEventX = currentEventX;
                break;
            }

            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {

                draggingLeft = false;

                if (animate() != null)
                    animate().cancel();

                int screenWidth = ViewUtils.getScreenWidth(getContext());

                if(!(Math.abs(getTranslationX()) > screenWidth / 4)){
                    animate()
                        .translationX(0)
                        .setDuration(300)
                        .setListener(null)
                        .setInterpolator(new LinearOutSlowInInterpolator())
                        .start();
                } else {
                    animate()
                        .translationX( - screenWidth)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if(mOnDragDismissListener != null)
                                    mOnDragDismissListener.onDismiss();
                            }
                        })
                        .setInterpolator(new LinearOutSlowInInterpolator())
                        .start();
                }

                break;
            }
        }

        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = ev.getActionIndex();
                startEventX = ev.getX(pointerIndex);
                startTranslationX = getTranslationX();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.getActionIndex();
                currentEventX = ev.getX(pointerIndex);
                return currentEventX - startEventX < -touchSlop;
            }
        }

        return super.onInterceptTouchEvent(ev);
    }

    public interface OnDragDismissListener {
        void onDismiss();
    }
}
