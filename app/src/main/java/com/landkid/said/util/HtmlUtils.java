package com.landkid.said.util;

import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.widget.TextView;

import in.uncod.android.bypass.style.TouchableUrlSpan;

/**
 * Created by sds on 2017. 7. 25..
 */

public class HtmlUtils {

    private HtmlUtils() { }

    public static void setTextWithNiceLinks(TextView textView, CharSequence input) {
        textView.setText(input);
        textView.setMovementMethod(LinkTouchMovementMethod.getInstance());
        textView.setFocusable(false);
        textView.setClickable(false);
        textView.setLongClickable(false);
    }


    public static SpannableStringBuilder fromHtml(String input) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return (SpannableStringBuilder) Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return (SpannableStringBuilder) Html.fromHtml(input);
        }
    }

    public static SpannableStringBuilder parseHtml(String input, ColorStateList linkTextColor, @ColorInt int linkHighlightColor) {
        SpannableStringBuilder spanned = fromHtml(input);

        while (spanned.charAt(spanned.length() - 1) == '\n') {
            spanned = spanned.delete(spanned.length() - 1, spanned.length());
        }

        return linkify(spanned, linkTextColor, linkHighlightColor);
    }


    private static SpannableStringBuilder linkify(CharSequence input, ColorStateList linkTextColor, @ColorInt int linkHighlightColor) {
        final SpannableString links = new SpannableString(input); // copy of input

        final URLSpan[] urlSpans = links.getSpans(0, links.length(), URLSpan.class);

        final SpannableStringBuilder ssb = new SpannableStringBuilder(input);
        for (URLSpan urlSpan : urlSpans) {
            ssb.removeSpan(urlSpan);
            ssb.setSpan(new TouchableUrlSpan(urlSpan.getURL(), linkTextColor, linkHighlightColor),
                    links.getSpanStart(urlSpan),
                    links.getSpanEnd(urlSpan),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return ssb;
    }


}
