package com.landkid.said.util;

import android.view.animation.Interpolator;

/**
 * Created by landkid on 2017. 7. 21..
 */

public class EaseOutElasticInterpolator implements Interpolator {

    @Override
    public float getInterpolation(float input) {
        float p = 0.3f;
        float PI = (float) Math.PI;
        float a = (float) Math.pow(2, -10 * input);
        float b = (float) Math.sin((input - p / 4) * ( 2 * PI ) / p);

        return a * b + 1;
    }
}
