package com.landkid.said.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.landkid.said.R;
import com.landkid.said.ui.drawable.GooeyDrawable;
import com.landkid.said.util.ResourceUtils;

/**
 * Created by landkid on 2017. 6. 25..
 */

public class GooeyFloatingActionButton extends FrameLayout {

    private int parentCircleRadius,
            childCircleRadius,
            parentDrawableResId,
            childDrawableResId,
            childMenuResId,
            offset;

    private CardView mParentButton;
    private CardView [] mChildButtons;
    private ImageView [] mGooeyBackgrounds;

    private int childCount;
    private Drawable[] childDrawable;
    private OnOptionItemClickListener mOnOptionItemClickListener;

    private OnParentItemClickListener mParentItemClickListener;

    public void setOnOptionItemClickListener(OnOptionItemClickListener onOptionItemClickListener) {
        this.mOnOptionItemClickListener = onOptionItemClickListener;
    }

    public OnOptionItemClickListener getOnOptionItemClickListener() {
        return mOnOptionItemClickListener;
    }

    public View getParentButton(){
        return mParentButton;
    }

    public View [] getChildButtons(){
        return mChildButtons;
    }

    public GooeyFloatingActionButton(Context context) {
        this(context, null, 0);
    }

    public GooeyFloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GooeyFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainAttribute(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    void obtainAttribute(Context context, AttributeSet attrs, int defStyleAttr){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GooeyFloatingActionButton, defStyleAttr, 0);

        parentCircleRadius = a.getDimensionPixelOffset(R.styleable.GooeyFloatingActionButton_parentButtonRadius, 0);
        childCircleRadius = a.getDimensionPixelOffset(R.styleable.GooeyFloatingActionButton_childButtonRadius, 0);
        parentDrawableResId = a.getResourceId(R.styleable.GooeyFloatingActionButton_parentDrawable, 0);
        childDrawableResId = a.getResourceId(R.styleable.GooeyFloatingActionButton_childDrawable, 0);
        childMenuResId = a.getResourceId(R.styleable.GooeyFloatingActionButton_childMenu, 0);
        offset = a.getDimensionPixelOffset(R.styleable.GooeyFloatingActionButton_offset, 0);
    };

    void initView(Context context, AttributeSet attrs, int defStyleAttr){
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        MenuInflater menuInflater = new MenuInflater(getContext());
        PopupMenu p  = new PopupMenu(getContext(), null);
        Menu menu = p.getMenu();
        menuInflater.inflate(childMenuResId, menu);

        childCount = menu.size();

        childDrawable = new Drawable[childCount];
        mGooeyBackgrounds = new ImageView[childCount];
        mChildButtons = new CardView[childCount];

        mParentButton = new CardView(context);
        LayoutParams parentLp = new LayoutParams(parentCircleRadius * 2, parentCircleRadius * 2);
        parentLp.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
        mParentButton.setLayoutParams(parentLp);
        mParentButton.setCardBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.colorAccent, context.getTheme()));
        mParentButton.setRadius(parentCircleRadius);
        mParentButton.setCardElevation(8 * getContext().getResources().getDisplayMetrics().density);

        ImageView parentIcon = new ImageView(context);
        CardView.LayoutParams parentIconLP = new CardView.LayoutParams((int) (parentCircleRadius * 2 * 7 / 16.0f), (int) (parentCircleRadius * 2 * 7 / 16.0f));
        parentIconLP.gravity = Gravity.CENTER;
        parentIcon.setLayoutParams(parentIconLP);
        parentIcon.setImageResource(parentDrawableResId);

        mParentButton.addView(parentIcon);
        for(int i = 0 ; i < menu.size() ; i++) {

            ImageView gooeyBackground = new ImageView(getContext());
            LayoutParams gooeyLp;
            if (i == 0) {
                gooeyLp = new LayoutParams(parentCircleRadius * 2,
                        (int) ((parentCircleRadius + childCircleRadius) * 2 + offset + ResourceUtils.dpToPx(2, getContext())));
                gooeyLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                gooeyBackground.setLayoutParams(gooeyLp);
                gooeyBackground.setImageDrawable(new GooeyDrawable(getContext(),
                        parentCircleRadius * 2,
                        (int) ((parentCircleRadius + childCircleRadius) * 2 + offset + ResourceUtils.dpToPx(2, getContext())),
                        ResourcesCompat.getColor(context.getResources(), R.color.colorAccent, context.getTheme()),
                        new DecelerateInterpolator(),
                        300,
                        parentCircleRadius,
                        childCircleRadius,
                        offset));
            } else {
                gooeyLp = new LayoutParams(childCircleRadius * 2,
                        (int) ((childCircleRadius + childCircleRadius) * 2 + offset + ResourceUtils.dpToPx(2, getContext())));
                gooeyLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                gooeyLp.bottomMargin = parentCircleRadius - childCircleRadius;
                gooeyBackground.setLayoutParams(gooeyLp);
                gooeyBackground.setImageDrawable(new GooeyDrawable(getContext(),
                        childCircleRadius * 2,
                        (int) ((childCircleRadius + childCircleRadius) * 2 + offset + ResourceUtils.dpToPx(2, getContext())),
                        ResourcesCompat.getColor(context.getResources(), R.color.colorAccent, context.getTheme()),
                        new DecelerateInterpolator(),
                        300,
                        childCircleRadius,
                        childCircleRadius,
                        offset));

                if(i == 1){
                    GooeyDrawable postGooeyDrawable = (GooeyDrawable) gooeyBackground.getDrawable();
                    postGooeyDrawable.setGooeyed(true);
                }
            }

            mGooeyBackgrounds[i] = gooeyBackground;
        }

        for(int i = 0 ; i < menu.size() ; i++){
            MenuItem menuItem = menu.getItem(i);
            childDrawable[i] = menuItem.getIcon();

            CardView childButton = new CardView(context);
            LayoutParams childLp = new LayoutParams(childCircleRadius * 2, childCircleRadius * 2);
            childLp.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
            childLp.bottomMargin = parentCircleRadius - childCircleRadius;
            childButton.setLayoutParams(childLp);
            childButton.setCardBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.colorAccent, context.getTheme()));
            childButton.setRadius(childCircleRadius);
            childButton.setCardElevation(0 * getContext().getResources().getDisplayMetrics().density);
            childButton.setId(menuItem.getItemId());
            childButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnOptionItemClickListener != null) {
                        if(mOnOptionItemClickListener.onItemClick(v, v.getId())) {
                            collapseAll();
                            mParentItemClickListener.setEnabled(false);

                        }
                        Log.d(v.getId() + "", "onClick: " + "clicked");
                    }
                }
            });

            ImageView childIcon = new ImageView(context);
            CardView.LayoutParams childIconLP = new CardView.LayoutParams((int) (childCircleRadius * 2 * 7 / 16.0f), (int) (childCircleRadius * 2 * 7 / 16.0f));
            childIconLP.gravity = Gravity.CENTER;
            childIcon.setLayoutParams(childIconLP);
            childIcon.setImageDrawable(childDrawable[i]);

            childButton.addView(childIcon);
            childButton.setVisibility(GONE);


            mChildButtons[i] = childButton;
            //mChildButtons[i].setVisibility(INVISIBLE);
            //gooeyDrawable = (GooeyDrawable) gooeyBackground.getDrawable();
        }
        for(int i = menu.size() - 1 ; i >= 0 ; i--) {
            addView(mGooeyBackgrounds[i]);
        }

        for(int i = menu.size() - 1 ; i >= 0 ; i--) {
            addView(mChildButtons[i]);
        }

        addView(mParentButton);

        mParentItemClickListener = new OnParentItemClickListener();

        mParentButton.setOnClickListener(mParentItemClickListener);

    }

    class OnParentItemClickListener implements View.OnClickListener {

        public boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public void onClick(View view) {
            mParentButton.setEnabled(false);
            if(!enabled) {
                expandAll();
            } else {
                collapseAll();
            }
            enabled = !enabled;
        }
    }

    public int getParentCircleRadius() {
        return parentCircleRadius;
    }

    public void expandAll(){
        setEnabled(false);
        float parentOffset = ((GooeyDrawable) mGooeyBackgrounds[0].getDrawable()).getTranslationOffset();

        expand(0, parentOffset);
    }

    private void expand(final int position, final float parentOffset){
        mGooeyBackgrounds[position].setVisibility(VISIBLE);
        final GooeyDrawable gooeyDrawable = (GooeyDrawable) mGooeyBackgrounds[position].getDrawable();
        gooeyDrawable.setDuration(200).start();

        mChildButtons[position].setVisibility(VISIBLE);
        mChildButtons[position].animate()
                .translationY(parentOffset + gooeyDrawable.getTranslationOffset() * position)
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        mGooeyBackgrounds[position].setVisibility(VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(position < childCount - 1) {
                            mGooeyBackgrounds[position + 1]
                                    .setTranslationY(mGooeyBackgrounds[position].getTranslationY() + gooeyDrawable.getTranslationOffset());

                            mChildButtons[position + 1]
                                    .setTranslationY(mChildButtons[position].getTranslationY());

                            expand(position + 1, parentOffset);
                        } else {
                            mParentButton.setEnabled(true);
                        }
                        if(position > 0){
                            GooeyDrawable postGooeyDrawable = (GooeyDrawable) mGooeyBackgrounds[position - 1].getDrawable();

                            if(!postGooeyDrawable.isGooeyed())
                                mGooeyBackgrounds[position - 1].setVisibility(GONE);
                        }
                    }
                })
                .setInterpolator(new LinearInterpolator())
                .setStartDelay(0)
                .setDuration(gooeyDrawable.getDuration() / 2)
                .start();
    }

    public void collapseAll(){

        final float parentOffset = ((GooeyDrawable) mGooeyBackgrounds[0].getDrawable()).getTranslationOffset();
        collapse(childCount - 1, parentOffset);

    }

    private void collapse(final int position, final float parentOffset){
        final GooeyDrawable gooeyDrawable = (GooeyDrawable) mGooeyBackgrounds[position].getDrawable();
        if(position == childCount - 1)
            gooeyDrawable.setDuration(150).reverse();

        mChildButtons[position].animate()
                .translationY(parentOffset * (position != 0 ? 1 : 0) + gooeyDrawable.getTranslationOffset() * (position - 1 >= 0 ? position - 1 : 0))
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        if(position < childCount - 1) {
                            mChildButtons[position + 1].setVisibility(GONE);
                            mGooeyBackgrounds[position + 1].setVisibility(GONE);
                        }
                        if(position > 0) {
                            mGooeyBackgrounds[position - 1].setVisibility(VISIBLE);
                            mChildButtons[position - 1].setVisibility(VISIBLE);
                            final GooeyDrawable gooeyDrawable = (GooeyDrawable) mGooeyBackgrounds[position - 1].getDrawable();
                            gooeyDrawable.setDuration(150);
                            gooeyDrawable.reverse();
                        }

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mChildButtons[position].setTranslationY(0);
                        if(position != 0){
                            collapse(position - 1, parentOffset);
                        } else {
                            mParentButton.setEnabled(true);
                            mGooeyBackgrounds[position].setVisibility(GONE);
                            mChildButtons[position].setVisibility(GONE);

                        }
                    }
                })
                .setInterpolator(new LinearInterpolator())
                .setStartDelay(position == childCount - 1 ? gooeyDrawable.getDuration() / 2: 0)
                .setDuration(gooeyDrawable.getDuration() / 2)
                .start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = parentCircleRadius * 2;
        int h = (int) ((parentCircleRadius * 2 + (childCircleRadius * 2 + offset) * childCount + ResourceUtils.dpToPx(2, getContext())));
        setMeasuredDimension(w, h);
    }

    public interface OnOptionItemClickListener{
        boolean onItemClick(View view, int itemId);
    }
}
