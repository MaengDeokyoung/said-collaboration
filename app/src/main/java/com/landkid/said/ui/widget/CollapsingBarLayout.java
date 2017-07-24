package com.landkid.said.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.landkid.said.R;
import com.landkid.said.util.ViewUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by SDS on 2017-03-09.
 */

@CoordinatorLayout.DefaultBehavior(CollapsingBarLayout.BarScrollBehavior.class)
public class CollapsingBarLayout extends CardView {

    public CollapsingBarLayout(Context context) {
        super(context);
    }

    public CollapsingBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CollapsingBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public CollapsingBarLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof FrameLayout.LayoutParams) {
            return new LayoutParams((FrameLayout.LayoutParams) p);
        } else if (p instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) p);
        }
        return new LayoutParams(p);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {

        @IntDef(flag=true, value={
                ORDER_FLAG_FIRST,
                ORDER_FLAG_SECOND,
                ORDER_FLAG_NOT_INCLUDED
        })

        @Retention(RetentionPolicy.SOURCE)
        public @interface OrderFlags {}

        public static final int ORDER_FLAG_NOT_INCLUDED = 0x0;

        public static final int ORDER_FLAG_FIRST = 0x1;

        public static final int ORDER_FLAG_SECOND = 0x2;

        int mOrderFlag = ORDER_FLAG_NOT_INCLUDED;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.OpusILCollapsingBarLayout_Layout);
            mOrderFlag = a.getInt(R.styleable.OpusILCollapsingBarLayout_Layout_layout_orderFlag, ORDER_FLAG_NOT_INCLUDED);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public LayoutParams(FrameLayout.LayoutParams source) {
            super(source);
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public LayoutParams(LayoutParams source) {
            super(source);
            mOrderFlag = source.mOrderFlag;
        }

        public void setOrderFlag(@OrderFlags int flags) {
            mOrderFlag = flags;
        }

        public @OrderFlags int getOrderFlag() {
            return mOrderFlag;
        }
    }

    private int getChildHeight(){
        int childHeights = 0;
        View child;
        for(int i = 0 ; i < getChildCount() ; i++){
            child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if(lp.getOrderFlag() == LayoutParams.ORDER_FLAG_FIRST || lp.getOrderFlag() == LayoutParams.ORDER_FLAG_SECOND){
                childHeights += child.getMeasuredHeight();
            }

        }
        return childHeights;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int desiredHeight = getChildHeight();
        int desiredWidth = ViewUtils.getScreenWidth(getContext());

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = desiredHeight;
        } else {
            height = desiredHeight;
        }

        if (width < 0) {
            width = 0;
        }

        if (height < 0) {
            height = 0;
        }

        setMeasuredDimension(width, height);
    }

    public static class ViewOffsetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
        private ViewOffsetHelper mViewOffsetHelper;
        private int mTempTopBottomOffset = 0;

        public ViewOffsetBehavior() {
        }

        public ViewOffsetBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
            parent.onLayoutChild(child, layoutDirection);
            if(mViewOffsetHelper == null) {
                mViewOffsetHelper = new ViewOffsetHelper(child);
            }

            //mViewOffsetHelper.onViewLayout();
            if(mTempTopBottomOffset != 0) {
                //mViewOffsetHelper.setTopAndBottomOffset(mTempTopBottomOffset);
                mTempTopBottomOffset = 0;
            }

            return true;
        }

        public boolean setTopAndBottomOffset(int offset) {
            if(mViewOffsetHelper != null) {
                return mViewOffsetHelper.setTopAndBottomOffset(offset);
            } else {
                mTempTopBottomOffset = offset;
                return false;
            }
        }

        public boolean setTopAndBottomOffset(int offset, boolean willUpdateOffset) {
            if(mViewOffsetHelper != null) {
                return mViewOffsetHelper.setTopAndBottomOffset(offset, willUpdateOffset);
            } else {
                mTempTopBottomOffset = offset;
                return false;
            }
        }


        public int getTopAndBottomOffset() {
            return mViewOffsetHelper != null?mViewOffsetHelper.getTopAndBottomOffset():0;
        }
    }

    public static class ViewOffsetHelper {
        private final View mView;
        private int mLayoutTop;
        private int mOffsetTop;

        public ViewOffsetHelper(View view) {
            mView = view;
        }

        public void onViewLayout() {
            mLayoutTop = mView.getTop();
            updateOffsets();
        }

        private void updateOffsets() {
            if(Build.VERSION.SDK_INT >= 22) {
                ViewCompat.setTranslationY(mView, (float) mOffsetTop);
            } else {
                ViewCompat.offsetTopAndBottom(mView, mOffsetTop - mView.getTop() - mLayoutTop);
            }
        }

        public boolean setTopAndBottomOffset(int offset) {
            if(mOffsetTop != offset) {
                mOffsetTop = offset;
                updateOffsets();
                return true;
            } else {
                return false;
            }
        }

        public boolean setTopAndBottomOffset(int offset, boolean willUpdateOffset) {
            if(mOffsetTop != offset) {
                mOffsetTop = offset;
                if(willUpdateOffset) {
                    updateOffsets();
                }
                return true;
            } else {
                return false;
            }
        }

        public int getTopAndBottomOffset() {
            return mOffsetTop;
        }
    }

    public static class BarScrollBehavior extends ViewOffsetBehavior<CollapsingBarLayout> {

        private static final String TAG = "BarScrollBehavior";
        private int mFirstBarHeight = 0;
        private int mSecondBarHeight = 0;
        boolean isOnSecondBar = true;

        private int mBarCount = 0;

        private View mVSecondBar;
        private View mVFirstBar;
        private boolean isInit = false;

        private int mTouchSlop;
        private int mMaxFlingVelocity;
        private int mMinFlingVelocity;
        private ViewFlingHelper mViewFlingHelper;
        private View mTargetView;

        private int mFirstBarOffset;
        private int mSecondBarOffset;
        private int mDependencyOffset;


        public BarScrollBehavior() {
            isInit = true;
        }

        public BarScrollBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
            isInit = true;
        }

        @Override
        public boolean onLayoutChild(CoordinatorLayout parent, CollapsingBarLayout collapsingBarLayout, int layoutDirection) {

            if(isInit) {
                if (mVSecondBar != null && isMultiBar()) {
                    mFirstBarOffset = 0;
                    mSecondBarOffset = mVFirstBar.getMeasuredHeight();
                    mDependencyOffset = mVFirstBar.getMeasuredHeight() + mVSecondBar.getMeasuredHeight();

                    mVSecondBar.setTranslationY(mSecondBarOffset);
                    mTargetView.setTranslationY(mDependencyOffset);

                    mSecondBarHeight = mVSecondBar.getMeasuredHeight();
                    mFirstBarHeight = mVFirstBar.getMeasuredHeight();
                } else {
                    mTargetView.setTranslationY(mVFirstBar.getMeasuredHeight());
                    mFirstBarHeight = mVFirstBar.getMeasuredHeight();
                }
            }

            final ViewConfiguration viewConfiguration = ViewConfiguration.get(parent.getContext());
            mTouchSlop = viewConfiguration.getScaledTouchSlop();
            mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
            mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();

            return super.onLayoutChild(parent, collapsingBarLayout, layoutDirection);
        }

        @Override
        public boolean onMeasureChild(CoordinatorLayout parent, CollapsingBarLayout collapsingBarLayout, int parentWidthMeasureSpec,
                                      int widthUsed, int parentHeightMeasureSpec, int heightUsed) {

            if(isInit) {
                mBarCount = 0;
                for (int i = 0; i < parent.getChildCount(); i++) {
                    if (parent.getChildAt(i) instanceof ScrollingView) {
                        mTargetView = parent.getChildAt(i);
                        parent.bringChildToFront(mTargetView);
                    } else if(parent.getChildAt(i) instanceof CollapsingBarLayout) {
                        CollapsingBarLayout barLayout = (CollapsingBarLayout) parent.getChildAt(i);
                        int childCount = barLayout.getChildCount();

                        if (childCount >= 2) {
                            mBarCount = 2;
                            for (int j = 0; j < childCount; j++) {

                                View child = barLayout.getChildAt(j);
                                CollapsingBarLayout.LayoutParams childLp = (CollapsingBarLayout.LayoutParams) child.getLayoutParams();

                                if(childLp.getOrderFlag() == LayoutParams.ORDER_FLAG_NOT_INCLUDED) {
                                    switch (j) {
                                        case 0:
                                            childLp.setOrderFlag(LayoutParams.ORDER_FLAG_FIRST);
                                            mVFirstBar = child;
                                            break;
                                        case 1:
                                            childLp.setOrderFlag(LayoutParams.ORDER_FLAG_SECOND);
                                            mVSecondBar = child;
                                            if(!collapsingBarLayout.getChildAt(1).equals(mVFirstBar))
                                                collapsingBarLayout.bringChildToFront(mVFirstBar);
                                            break;
                                    }
                                } else {

                                    boolean isLastIndex = j == (childCount - 1);

                                    switch (childLp.getOrderFlag()){
                                        case LayoutParams.ORDER_FLAG_FIRST:
                                            mVFirstBar = child;
                                            break;
                                        case LayoutParams.ORDER_FLAG_SECOND:
                                            mVSecondBar = child;
                                            break;
                                    }
                                    if (isLastIndex) {
                                        if(!collapsingBarLayout.getChildAt(1).equals(mVFirstBar))
                                            collapsingBarLayout.bringChildToFront(mVFirstBar);
                                    }
                                }
                            }
                        } else if (childCount == 1) {
                            mBarCount = 1;
                            mVFirstBar = barLayout.getChildAt(0);
                            CollapsingBarLayout.LayoutParams childLp = (CollapsingBarLayout.LayoutParams) mVFirstBar.getLayoutParams();
                            childLp.setOrderFlag(LayoutParams.ORDER_FLAG_FIRST);
                        }
                    }
                }
            }
            return super.onMeasureChild(parent, collapsingBarLayout, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
        }

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, CollapsingBarLayout collapsingBarLayout, View dependency) {
            return dependency instanceof ScrollingView;
        }

        @Override
        public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, CollapsingBarLayout collapsingBarLayout, View directTargetChild,
                                           View target, int nestedScrollAxes) {

            if (mViewFlingHelper != null) {
                mViewFlingHelper.cancel();
            }

            if (nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL) {
                return true;
            }

            return false;
        }

        @Override
        public void onNestedPreScroll(CoordinatorLayout parent, CollapsingBarLayout collapsingBarLayout, View target, int dx, final int dy, int[] consumed) {
            if (dy != 0) {
                isInit = false;
                target.getParent().requestDisallowInterceptTouchEvent(true);

                final int min = - (mBarCount > 1 ? mSecondBarHeight : mFirstBarHeight);
                final int max = 0;

                setOffset(collapsingBarLayout, min, max, dy, consumed);
            }
        }

        private int getFirstBarOffset(){
            return mFirstBarOffset;
        }

        private int getSecondBarOffset(){
            return mSecondBarOffset;
        }

        private int getDependencyOffset(){
            return mDependencyOffset;
        }

        private void setOffset(CollapsingBarLayout collapsingBarLayout, int min, int max, int dy){
            setOffset(collapsingBarLayout, min, max, dy, null);
        }

        private void updateOffset(CollapsingBarLayout collapsingBarLayout){
            mVFirstBar.setTranslationY(mFirstBarOffset);
            mVSecondBar.setTranslationY(mSecondBarOffset);
            mTargetView.setTranslationY(mDependencyOffset);

            mVSecondBar.getLayoutParams().height = mSecondBarHeight;
            mVFirstBar.getLayoutParams().height = mFirstBarHeight;
            collapsingBarLayout.getLayoutParams().height = mDependencyOffset;
            collapsingBarLayout.requestLayout();
        }

        private void setOffset(CollapsingBarLayout collapsingBarLayout, int min, int max, int dy, int [] consumed){

            int currentOffset;
            int newOffset;

            if(isMultiBar()) {

                boolean isBarScrolling = mVSecondBar.getTranslationY() >= min && mVSecondBar.getTranslationY() <= mFirstBarHeight;
                int heightDifference = mFirstBarHeight - mSecondBarHeight;
                if (isBarScrolling) {
                    int adjustedOffset = getSecondBarOffset() - dy;

                    if (isOnSecondBar) {
                        if (adjustedOffset <= heightDifference) {
                            isOnSecondBar = false;
                            adjustedOffset = heightDifference;
                        }
                        if (adjustedOffset > mFirstBarHeight) {
                            if(consumed != null) {
                                consumed[1] = (adjustedOffset - mFirstBarHeight) - dy;
                            }
                            adjustedOffset = mFirstBarHeight;
                        } else {
                            if(consumed != null) {
                                consumed[1] = dy;
                            }
                        }
                        newOffset = adjustedOffset;

                        mFirstBarOffset = 0;
                        mSecondBarOffset = newOffset;
                        mDependencyOffset = mSecondBarHeight + newOffset;

                    } else {

                        if (adjustedOffset > heightDifference) {
                            isOnSecondBar = true;
                            adjustedOffset = heightDifference;
                        }

                        currentOffset = getTopAndBottomOffset();
                        newOffset = Math.min(Math.max(- mFirstBarHeight, adjustedOffset - heightDifference), max);
                        setTopAndBottomOffset(newOffset, false);

                        if(consumed != null) {
                            consumed[1] = currentOffset - newOffset;
                        }

                        mFirstBarOffset = newOffset;
                        mSecondBarOffset = newOffset + heightDifference;
                        mDependencyOffset = mFirstBarHeight + newOffset;

                    }

                    updateOffset(collapsingBarLayout);
                }
            } else {
                currentOffset = getTopAndBottomOffset();
                newOffset = Math.min(Math.max(min, currentOffset - dy), max);
                if(consumed != null) {
                    consumed[1] = currentOffset - newOffset;
                }
                setTopAndBottomOffset(newOffset);
                mTargetView.setTranslationY(mFirstBarHeight + newOffset);
            }
        }

        @Override
        public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, final CollapsingBarLayout collapsingBarLayout, View target, float velocityX, float velocityY) {
            isInit = false;

            if (mViewFlingHelper != null) {
                mViewFlingHelper.cancel();
            } else {
                mViewFlingHelper = new ViewFlingHelper(coordinatorLayout);
            }

            mViewFlingHelper.fling((int) velocityY);
            return true;

        }

        private boolean isMultiBar(){
            return mBarCount > 1;
        }

        private class ViewFlingHelper implements Runnable {
            private final ScrollerCompat mScroller;
            private final CoordinatorLayout mCoordinatorLayout;
            private int mLastFlingY = 0;


            public ViewFlingHelper(CoordinatorLayout coordinatorLayout) {
                mScroller = ScrollerCompat.create(coordinatorLayout.getContext());
                mCoordinatorLayout = coordinatorLayout;
            }

            private boolean isOnBarTopOrBottomEdge(int min){
                return isMultiBar() ?
                        (mVSecondBar.getTranslationY() == min || mVSecondBar.getTranslationY() == mFirstBarHeight) :
                        (getTopAndBottomOffset() == 0 || getTopAndBottomOffset() == - mFirstBarHeight);
            }

            @Override
            public void run() {
                if (mScroller.computeScrollOffset()) {
                    int dy = (mScroller.getCurrY() - mLastFlingY);
                    CollapsingBarLayout collapsingBarLayout = (CollapsingBarLayout) mVFirstBar.getParent();

                    final int min = - (mVSecondBar != null ? mSecondBarHeight : mFirstBarHeight);
                    final int max = 0;

                    setOffset(collapsingBarLayout, min, max, dy);

                    if(isOnBarTopOrBottomEdge(min)){
                        mTargetView.scrollBy(0, dy);
                    }

                    final boolean scrollerFinished = mScroller.isFinished();

                    if (scrollerFinished) {
                        return;
                    }

                    ViewCompat.postOnAnimation(mCoordinatorLayout, this);

                    mLastFlingY = mScroller.getCurrY();
                }
            }

            public void cancel() {
                mScroller.abortAnimation();
            }

            public void fling(int velocity){

                velocity = Math.max(- mMaxFlingVelocity, Math.min(velocity, mMaxFlingVelocity));
                mScroller.fling(0, 0, 0, velocity, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
                ViewCompat.postOnAnimation(mCoordinatorLayout, this);
                mLastFlingY = 0;

            }
        }
    }

    public static class CollapsingBarScrollBehavior extends ViewOffsetBehavior {

        int contentsOffset;

        public CollapsingBarScrollBehavior() {
        }

        public CollapsingBarScrollBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public int getContentsOffset() {
            return contentsOffset;
        }

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

            CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams)dependency.getLayoutParams()).getBehavior();
            if(behavior instanceof BarScrollBehavior) {
                int headerOffset = ((BarScrollBehavior)behavior).getTopAndBottomOffset();
                contentsOffset = dependency.getHeight() + headerOffset;
                setTopAndBottomOffset(contentsOffset, false);
            }
            return false;
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
            return dependency instanceof CollapsingBarLayout;
        }
    }
}
