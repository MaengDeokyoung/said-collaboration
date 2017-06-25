package com.landkid.said.ui.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
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
    private CardView mChildButton;
    private ImageView mGooeyBackground;
    private CardView [] mChildButtons;
    private ImageView [] mGooeyBackgrounds;

    private int childCount;
    private Drawable[] childDrawable;

    GooeyDrawable gooeyDrawable;

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

        //setLayoutParams(new LayoutParams(parentCircleRadius * 2, (parentCircleRadius + childCircleRadius) * 2 + offset));

        MenuInflater menuInflater = new MenuInflater(getContext());
        PopupMenu p  = new PopupMenu(getContext(), null);
        Menu menu = p.getMenu();
        menuInflater.inflate(childMenuResId, menu);

        childDrawable = new Drawable[menu.size()];

        for(int i = 0 ; i < menu.size() ; i++){
            MenuItem menuItem = menu.getItem(i);
            childDrawable[i] = menuItem.getIcon();
        }

        mParentButton = new CardView(context);
        LayoutParams parentLp = new LayoutParams(parentCircleRadius * 2, parentCircleRadius * 2);
        parentLp.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
        mParentButton.setLayoutParams(parentLp);
        mParentButton.setCardBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.colorAccent, context.getTheme()));
        mParentButton.setRadius(parentCircleRadius);
        mParentButton.setCardElevation(0);

        ImageView parentIcon = new ImageView(context);
        CardView.LayoutParams parentIconLP = new CardView.LayoutParams((int) (parentCircleRadius * 2 * 7 / 16.0f), (int) (parentCircleRadius * 2 * 7 / 16.0f));
        parentIconLP.gravity = Gravity.CENTER;
        parentIcon.setLayoutParams(parentIconLP);
        parentIcon.setImageResource(parentDrawableResId);

        mParentButton.addView(parentIcon);

        mGooeyBackground = new ImageView(getContext());
        mGooeyBackground.setLayoutParams(new LayoutParams(parentCircleRadius * 2, (int) ((parentCircleRadius + childCircleRadius) * 2 + offset + ResourceUtils.dpToPx(2, getContext()))));
        mGooeyBackground.setImageDrawable(new GooeyDrawable(getContext(),
                parentCircleRadius * 2,
                (int) ((parentCircleRadius + childCircleRadius) * 2 + offset + ResourceUtils.dpToPx(2, getContext())),
                ResourcesCompat.getColor(context.getResources(), R.color.colorAccent, context.getTheme()),
                new DecelerateInterpolator(),
                500,
                parentCircleRadius,
                childCircleRadius,
                offset));

        addView(mGooeyBackground);

        mChildButton = new CardView(context);
        LayoutParams childLp = new LayoutParams(childCircleRadius * 2, childCircleRadius * 2);
        childLp.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
        childLp.bottomMargin = parentCircleRadius - childCircleRadius;
        mChildButton.setLayoutParams(childLp);
        mChildButton.setCardBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.colorAccent, context.getTheme()));
        mChildButton.setRadius(childCircleRadius);
        mChildButton.setCardElevation(0);

        ImageView childIcon = new ImageView(context);
        CardView.LayoutParams childIconLP = new CardView.LayoutParams((int) (childCircleRadius * 2 * 7 / 16.0f), (int) (childCircleRadius * 2 * 7 / 16.0f));
        childIconLP.gravity = Gravity.CENTER;
        childIcon.setLayoutParams(childIconLP);
        childIcon.setImageResource(childDrawableResId);

        mChildButton.addView(childIcon);

        addView(mChildButton);

        gooeyDrawable = (GooeyDrawable) mGooeyBackground.getDrawable();


        addView(mParentButton);

        mParentButton.setOnClickListener(new View.OnClickListener() {

            boolean enabled = false;

            @Override
            public void onClick(View view) {
                if(!enabled) {
                    expand();
                } else {
                    collapse();
                }
                enabled = !enabled;

//                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
//                intent.putExtra("cx", view.getX() + view.getWidth() / 2);
//                intent.putExtra("cy", view.getY() + view.getHeight() / 2);
//                startActivity(intent);
            }
        });

    }

    void expand(){
        gooeyDrawable = (GooeyDrawable) mGooeyBackground.getDrawable();
        gooeyDrawable.stop();
        gooeyDrawable.start();
        mChildButton.setTranslationY(0);
        mChildButton.animate()
                .translationY(gooeyDrawable.getTranslationOffset())
                .setStartDelay(0)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(gooeyDrawable.getDuration() / 2)
                .start();
    }

    void collapse(){
        gooeyDrawable = (GooeyDrawable) mGooeyBackground.getDrawable();
        gooeyDrawable.stop();
        gooeyDrawable.reverse();
        mChildButton.setTranslationY(gooeyDrawable.getTranslationOffset());
        mChildButton.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setStartDelay(gooeyDrawable.getDuration() / 2)
                .setDuration(gooeyDrawable.getDuration() / 2)
                .start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = parentCircleRadius * 2;
        int h = (int) ((parentCircleRadius + childCircleRadius) * 2 + offset + ResourceUtils.dpToPx(2, getContext()));

        setMeasuredDimension(w, h);
    }


    //    <ImageView
//    android:id="@+id/gooey_background"
//    android:layout_width="65dp"
//    android:layout_height="143dp"
//    android:layout_gravity="bottom|end"
//    android:elevation="8dp"
//    android:layout_margin="20dp"/>
//
//    <android.support.v7.widget.CardView
//    android:id="@+id/fab2"
//    android:layout_width="52dp"
//    android:layout_height="52dp"
//    android:layout_gravity="bottom|end"
//    android:elevation="8dp"
//    android:layout_margin="26dp"
//    app:cardCornerRadius="26dp"
//    app:cardElevation="8dp"
//    app:cardBackgroundColor="@color/colorAccent">
//
//        <ImageView
//    android:layout_width="24dp"
//    android:layout_height="24dp"
//    android:layout_gravity="center"
//    android:src="@drawable/ic_color_palette_white"/>
//
//    </android.support.v7.widget.CardView>
//
//
//    <android.support.v7.widget.CardView
//    android:id="@+id/fab1"
//    android:layout_width="64dp"
//    android:layout_height="64dp"
//    android:layout_gravity="bottom|end"
//    android:elevation="8dp"
//    android:layout_margin="20dp"
//    app:cardCornerRadius="32dp"
//    app:cardElevation="8dp"
//    app:cardBackgroundColor="@color/colorAccent">
//
//        <ImageView
//    android:layout_width="28dp"
//    android:layout_height="28dp"
//    android:layout_gravity="center"
//    android:src="@drawable/ic_search_vector"/>
//
//    </android.support.v7.widget.CardView>
}
