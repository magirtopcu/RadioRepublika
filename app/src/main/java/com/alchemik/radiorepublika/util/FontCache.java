package com.alchemik.radiorepublika.util;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Leszek Jasek on 23.04.2016.
 */
public class FontCache {
    private static Map<String, Typeface> fontCache = new HashMap<>();

    @Nullable
    public static Typeface getTypeFace(String fontName, Context context) {

        Typeface typeface = fontCache.get(fontName);

        if (typeface  == null) {
            try {
                fontCache.put(fontName, Typeface.createFromAsset(context.getAssets(), "fonts/" + fontName));
            } catch (Exception e) {
                return null;
            }
        }
        return typeface;
    }
}
