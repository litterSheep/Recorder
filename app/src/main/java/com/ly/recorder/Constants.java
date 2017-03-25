package com.ly.recorder;

import android.os.Environment;

import java.io.File;

/**
 * Created by ly on 2017/3/2 15:06.
 */

public class Constants {

    public static final String DB_NAME = "recorder.db";

    public static final int TYPE_IN = 1;//收入
    public static final int TYPE_OUT = 2;//支出
    public static final String[] TYPES_OUT = {"支出", "饭菜", "烟酒", "零食", "娱乐", "话费", "交通", "衣服", "房租", "水电", "其他支出"};
    public static final String[] TYPES_IN = {"收入", "工资", "红包", "投资", "人情", "其他收入"};


    public static final String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ly";
    public static final String CRASH_PATH = BASE_PATH + File.separator + "crash";

    public static final String PREFERENCES_FLAG = "isShowDate";
    public static final String PREFERENCES_FLAG_DIALOG = "isShowDialog";
    public static final String PREFERENCES_FLAG_BAR = "isShowBar";

}
