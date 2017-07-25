package com.landkid.said.ui.listener;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by landkid on 2017. 7. 25..
 */

public class SubScrollListener implements NestedScrollView.OnScrollChangeListener {

    boolean isAnimated;
    CardView imageCard;
    Context context;

    public SubScrollListener(Context context, CardView target){
        this.context = context;
        this.imageCard = target;
    }

    private Resources getResources(){
        return context.getResources();
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        imageCard.setPivotX(imageCard.getMeasuredWidth() - 20 * getResources().getDisplayMetrics().density);
        imageCard.setPivotY(20 * getResources().getDisplayMetrics().density);
        if (!isAnimated) {
            if (scrollY > imageCard.getMeasuredHeight()) {

                if (scrollY > oldScrollY) {

                    imageCard.animate()
                            .scaleX(0.5f)
                            .scaleY(0.5f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    isAnimated = true;

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    isAnimated = false;

                                }
                            })
                            .setInterpolator(new DecelerateInterpolator())
                            .setDuration(300)
                            .start();
                }

            } else {

                if (scrollY < oldScrollY) {

                    imageCard.animate()
                            .scaleX(1)
                            .scaleY(1)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    isAnimated = true;

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    isAnimated = false;

                                }
                            })
                            .setInterpolator(new DecelerateInterpolator())
                            .setDuration(300)
                            .start();
                }
            }
        }
        if (scrollY > 0) {
            imageCard.setCardElevation(8 * getResources().getDisplayMetrics().density);

        } else {
            ObjectAnimator elevationAnimator = ObjectAnimator.ofFloat(imageCard, "cardElevation", 8 * getResources().getDisplayMetrics().density, 0);
            elevationAnimator.setDuration(300).start();
            //imageCard.setCardElevation(0);

        }
        imageCard.setTranslationY(scrollY);
    }
}