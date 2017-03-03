package com.ly.recorder.utils;

import android.content.Context;

/**
 * Created by ly on 2017/2/10 15:34.
 */

public class ScreenUtil {

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = context.getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = context.getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }

    public static int getScreenWidth(Context context) {
        return context.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getApplicationContext().getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenDpi(Context context) {
        return context.getApplicationContext().getResources().getDisplayMetrics().densityDpi;
    }

}
