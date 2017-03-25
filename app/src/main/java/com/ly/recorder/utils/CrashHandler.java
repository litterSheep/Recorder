package com.ly.recorder.utils;

import android.content.Context;
import android.os.Debug;
import android.os.Process;

import com.ly.recorder.Constants;
import com.ly.recorder.utils.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;


/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类 来接管程序,并记录 发送错误报告.
 */
public class CrashHandler implements UncaughtExceptionHandler {

    /**
     * Debug Log tag
     */
    public static final String TAG = "CrashHandler";
    public static final boolean CONFIG_CRASH_HANDLER_DEBUG = false;
    /**
     * 错误报告文件的扩展名
     */
    private static final String CRASH_REPORTER_EXTENSION = ".log";
    /**
     * CrashHandler实例
     */
    private static CrashHandler INSTANCE;
    /**
     * 系统默认的UncaughtException处理类
     */
    private UncaughtExceptionHandler mDefaultHandler;
    /**
     * 使用Properties来保存设备的信息和错误堆栈信息
     */
    private Properties mDeviceCrashInfo = new Properties();
    private Context ctx;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     *
     * @param ctx
     */
    public void init(Context ctx) {
        this.ctx = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null || CONFIG_CRASH_HANDLER_DEBUG) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // Sleep一会后结束程序
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Logger.e("Error : " + e.toString());
            }

            Process.killProcess(Process.myPid());
            System.exit(10);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        /*
         * final String msg = ex.getLocalizedMessage(); // 使用Toast来显示异常信息 new
		 * Thread() {
		 *
		 * @Override public void run() { Looper.prepare(); try {
		 * XLToast.showToast(mContext, XLToastType.XLTOAST_TYPE_ALARM, "程序出错啦:"
		 * + msg); } catch(Exception e) {
		 *
		 * } Looper.loop(); } }.start();
		 */

        // 保存错误报告文件
        saveCrashInfoToFile(ex);

        // 如果是OOM异常，手机内存快照
        collectionDumpHprofDataIfOOM(ex);

        System.exit(0);

        return false;
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return
     */
    private String saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        String result = "\nstack:\n" + info.toString();
        Logger.e(result);

        printWriter.close();

        try {
            String path = Constants.CRASH_PATH;
            File pathFile = new File(path);
            if (!pathFile.exists() || !pathFile.isDirectory()) {
                pathFile.mkdirs();
            }

            String fileName = path + File.separator + "crash-" + TimeUtil.getCurrentTime("yyyy-MM-dd HH-mm-ss") + CRASH_REPORTER_EXTENSION;
            FileOutputStream trace = new FileOutputStream(new File(fileName));
            mDeviceCrashInfo.store(trace, "");
            trace.write(result.getBytes());
            trace.flush();
            trace.close();
            return fileName;
        } catch (Exception e) {
            Logger.e("an error occured while writing report file..." + e.toString());
        }
        return null;
    }

    /**
     * 如果是OOM错误，则保存崩溃时的内存快照，供分析使用
     *
     * @param ex
     */
    public void collectionDumpHprofDataIfOOM(Throwable ex) {
        // 如果是OOM错误，则保存崩溃时的内存快照，供分析使用
        if (isOOM(ex)) {
            try {
                String path = Constants.CRASH_PATH + File.separator;
                String fileName = path + "crash-" + TimeUtil.getCurrentTime("yyyy-MM-dd HH-mm-ss") + ".hprof";
                Debug.dumpHprofData(fileName);
            } catch (IOException e) {
                Logger.e("couldn’t dump hprof,  an error occurs while opening or writing files.");
            } catch (UnsupportedOperationException e) {
                Logger.e("couldn’t dump hprof,  the VM was built without HPROF support.");
            }
        }
    }

    /**
     * 检测这个抛出对象是否为OOM Error
     *
     * @param throwable
     * @return
     */
    private boolean isOOM(Throwable throwable) {

        if (null != throwable && OutOfMemoryError.class.getName().equals(throwable.getClass().getName())) {
            return true;
        } else {
            Throwable cause = throwable.getCause();
            if (cause != null) {
                return isOOM(cause);
            }
            return false;
        }
    }

}
