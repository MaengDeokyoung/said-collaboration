package com.landkid.said.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;

/**
 * Created by SDS on 2016-11-16.
 */
public class ResourceUtils {

    private ResourceUtils(){ }

    public static float dpToPx(int size, Context context){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, context.getResources().getDisplayMetrics());
    }

    public static float dpToPx(float size, Context context){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, context.getResources().getDisplayMetrics());
    }

    public static float spToPx(int size, Context context){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, context.getResources().getDisplayMetrics());
    }

    public static int getColor(int colorRes, Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return context.getResources().getColor(colorRes);
        }
        else{
            return context.getResources().getColor(colorRes, context.getTheme());
        }
    }

    public static int getColorInt(ColorStateList colorStateList, int defaultColor, int[] drawableState){
        if(colorStateList != null){
            return colorStateList.getColorForState(drawableState, 0);
        }
        else {
            return defaultColor;
        }
    }

    public static Drawable getDrawable(int drawableRes, Context context){
        return ResourcesCompat.getDrawable(context.getResources(), drawableRes, context.getTheme());
    }

    public static int getDimensionPixelSize(int dimenRes, Context context){
        return context.getResources().getDimensionPixelSize(dimenRes);
    }

    public static float getDimension(int dimenRes, Context context){
        return context.getResources().getDimension(dimenRes);
    }

    public static String getString(int stringRes, Context context){
        return context.getResources().getString(stringRes);
    }
}
