package com.ly.recorder.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * 像素转换工具
 */
public class PixelUtil {

    /**
     * dp转 px.
     *
     * @param value the value
     * @return the int
     */
    public static int dp2px(Context mContext, float value) {
        final float scale = mContext.getResources().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }

    /**
     * px转dp.
     *
     * @param value the value
     * @return the int
     */
    public static int px2dp(Context mContext, float value) {
        final float scale = mContext.getResources().getDisplayMetrics().densityDpi;
        return (int) ((value * 160) / scale + 0.5f);
    }

    /**
     * sp转px.
     *
     * @param value the value
     * @return the int
     */
    public static int sp2px(Context mContext, float value) {
        Resources r;
        if (mContext == null) {
            r = Resources.getSystem();
        } else {
            r = mContext.getResources();
        }
        float spvalue = value * r.getDisplayMetrics().scaledDensity;
        return (int) (spvalue + 0.5f);
    }

    /**
     * px转sp.
     *
     * @param value the value
     * @return the int
     */
    public static int px2sp(Context mContext, float value) {
        final float scale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (value / scale + 0.5f);
    }

    /**
     * value--->DIP
     * 这个方法是转变为标准尺寸的一个函数,这里COMPLEX_UNIT_DIP是单位，180是数值，也就是180dip。
     *
     * @param context
     * @param value   需要的大小
     * @return
     */
    public static int getAbsluteDip(Context context, int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value, context.getResources().getDisplayMetrics());
    }
}