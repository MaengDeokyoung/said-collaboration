package com.landkid.said.ui.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.landkid.said.R;
import com.landkid.said.util.ResourceUtils;

/**
 * Created by sds on 2017. 7. 26..
 */

public class ChangeColor extends Transition {

    boolean reverse;

    public ChangeColor(Animatable animatable) {
        super();
    }

    public ChangeColor(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChangeColor);
        reverse = a.getBoolean(R.styleable.ChangeColor_reverse, false);
        a.recycle();
    }


    @Override
    public void captureStartValues(TransitionValues transitionValues) {

    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {

    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot,
                                   TransitionValues startValues,
                                   TransitionValues endValues) {

        if (endValues == null
                || !(endValues.view instanceof FloatingActionButton)) return null;

        final FloatingActionButton fab = (FloatingActionButton) endValues.view;


        int startColor;
        int endColor;
        float startElevation;
        float endElevation;

        if(reverse){
            startColor = ContextCompat.getColor(sceneRoot.getContext(), R.color.fab);
            endColor = ContextCompat.getColor(sceneRoot.getContext(), R.color.colorAccent);
            startElevation = ResourceUtils.dpToPx(4, fab.getContext());
            endElevation = 0;
        } else {
            startColor = ContextCompat.getColor(sceneRoot.getContext(), R.color.colorAccent);
            endColor = ContextCompat.getColor(sceneRoot.getContext(), R.color.fab);
            startElevation = 0;
            endElevation = ResourceUtils.dpToPx(4, fab.getContext());
        }

        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator colorChange = ValueAnimator.ofInt(startColor, endColor);

        colorChange.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int colorInt = (int) valueAnimator.getAnimatedValue();
                fab.setBackgroundColor(colorInt);
                fab.setBackgroundTintList(new ColorStateList(new int[][]{
                    new int[]{android.R.attr.state_enabled},
                }, new int[]{
                        colorInt
                }));
            }
        });
        colorChange.setEvaluator(new ArgbEvaluator());
        colorChange.setDuration(300);


        ObjectAnimator elevationChange = ObjectAnimator.ofFloat(fab, "elevation", startElevation, endElevation);
        elevationChange.setDuration(300);

        animatorSet.playTogether(colorChange, elevationChange);
        return animatorSet;
    }
}
