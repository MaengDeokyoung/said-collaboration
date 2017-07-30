package com.landkid.said.util;

import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import in.uncod.android.bypass.style.TouchableUrlSpan;

/**
 * Created by sds on 2017. 7. 25..
 */

public class HtmlUtils {

    private HtmlUtils() { }

    public static void setTextWithLinks(TextView textView, CharSequence input) {
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

        while (spanned.length() - 1 >= 0 && spanned.charAt(spanned.length() - 1) == '\n') {
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
    private static class LinkTouchMovementMethod extends LinkMovementMethod {


        private static LinkTouchMovementMethod instance;
        private TouchableUrlSpan pressedSpan;

        public static MovementMethod getInstance() {
            if (instance == null)
                instance = new LinkTouchMovementMethod();

            return instance;
        }

        @Override
        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
            boolean handled = false;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                pressedSpan = getPressedSpan(textView, spannable, event);
                if (pressedSpan != null) {
                    pressedSpan.setPressed(true);
                    Selection.setSelection(spannable, spannable.getSpanStart(pressedSpan),
                            spannable.getSpanEnd(pressedSpan));
                    handled = true;
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                TouchableUrlSpan touchedSpan = getPressedSpan(textView, spannable, event);
                if (pressedSpan != null && touchedSpan != pressedSpan) {
                    pressedSpan.setPressed(false);
                    pressedSpan = null;
                    Selection.removeSelection(spannable);
                }
            } else {
                if (pressedSpan != null) {
                    pressedSpan.setPressed(false);
                    super.onTouchEvent(textView, spannable, event);
                    handled = true;
                }
                pressedSpan = null;
                Selection.removeSelection(spannable);
            }
            return handled;
        }

        private TouchableUrlSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent
                event) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= textView.getTotalPaddingLeft();
            y -= textView.getTotalPaddingTop();

            x += textView.getScrollX();
            y += textView.getScrollY();

            Layout layout = textView.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            TouchableUrlSpan[] link = spannable.getSpans(off, off, TouchableUrlSpan.class);
            TouchableUrlSpan touchedSpan = null;
            if (link.length > 0) {
                touchedSpan = link[0];
            }
            return touchedSpan;
        }

    }

}
