package com.ly.recorder.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by ly on 2017/2/10 15:34.
 */
public class ToastUtil {

    public static void showToast(Context context, String text) {
        if (!TextUtils.isEmpty(text))
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}
