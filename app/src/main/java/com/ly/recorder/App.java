package com.ly.recorder;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.ly.recorder.db.MigrationHelper;
import com.ly.recorder.db.greendao.DaoMaster;
import com.ly.recorder.db.greendao.DaoSession;
import com.ly.recorder.ui.MainActivity;
import com.ly.recorder.utils.AppUtil;
import com.ly.recorder.utils.CrashHandler;
import com.ly.recorder.utils.logger.LogLevel;
import com.ly.recorder.utils.logger.Logger;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by ly on 2017/3/2 14:41.
 */

public class App extends Application {

    private static App instance;
    private MigrationHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;


        CrashHandler.getInstance().init(getApplicationContext());

        Logger.init("MyLog")
                .logLevel(BuildConfig.DEBUG ? LogLevel.FULL : LogLevel.NONE)//日志开关
                //.hideThreadInfo()
                .methodCount(3);

        setDatabase();

        initBugly();
    }

    private void initBugly(){
        // 获取当前包名
        String packageName = getPackageName();
        // 获取当前进程名
        String processName = AppUtil.getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
        //CrashReport.initCrashReport(this, "c65aa5b316", true, strategy);
        // 如果通过“AndroidManifest.xml”来配置APP信息，初始化方法如下
        // CrashReport.initCrashReport(context, strategy);
        strategy.setAppReportDelay(2000);

        Beta.canShowUpgradeActs.add(MainActivity.class);
        Beta.initDelay = 2000;
        //Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        Bugly.init(getApplicationContext(), "c65aa5b316", BuildConfig.DEBUG,strategy);
    }

    /**
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new MigrationHelper(this, Constants.DB_NAME, null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        if (mDaoSession == null)
            setDatabase();
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

}
