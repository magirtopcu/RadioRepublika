package com.alchemik.radiorepublika.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.alchemik.radiorepublika.R;
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

    @TargetApi(21)
    public CustomFontTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyFont(context);
    }

    public void applyFont(Context context) {
        Typeface tf = FontCache.getTypeFace("Oswald.ttf", context);
        if (tf != null) {
            setTypeface(tf);
        }
    }
}
