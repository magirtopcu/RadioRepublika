package com.alchemik.radiorepublika.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.alchemik.radiorepublika.util.FontCache;

/**
 * TODO: document your custom view class.
 */
public class CustomFontTextView extends TextView {

    public CustomFontTextView(Context context) {
        super(context);
        applyFont(context);
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyFont(context);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyFont(context);
    }


    public void applyFont(Context context) {
        Typeface tf = FontCache.getTypeFace("Oswald.ttf", context);
        if (tf != null) {
            setTypeface(tf);
        }
    }
}
