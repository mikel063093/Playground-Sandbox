package com.tudorluca.sandbox.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by tudor on 08/06/16.
 */
public class UiUtils {

    public static float dpToPx(Context context, float dp) {

        return dp * getDensityRatio(context);
    }

    public static float getDensityRatio(Context context) {
        final DisplayMetrics metrics = getDisplayMetrics(context);
        return (metrics.densityDpi / 160f);
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        final DisplayMetrics metrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        return metrics;
    }
}
