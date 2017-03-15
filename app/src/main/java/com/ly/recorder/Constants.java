package com.ly.recorder;

import android.os.Environment;

import java.io.File;

/**
 * Created by ly on 2017/3/2 15:06.
 */

public class Constants {

    public static final String DB_NAME = "recorder.db";

    public static final String[] TYPES_OUT = {"饭菜", "烟酒", "零食", "娱乐", "话费", "交通", "衣服", "房租", "水电", "其他"};
    public static final String[] TYPES_IN = {"饭菜", "烟酒", "零食", "娱乐", "话费", "交通", "衣服", "房租", "水电", "其他"};


    public static final String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ly";
    public static final String CRASH_PATH = BASE_PATH + File.separator + "crash";

    public static final String PREFRENCES_FLAG = "isShow";

}
