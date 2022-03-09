package com.wj.gradle.plugin;

import android.app.Application;
import android.os.Message;
import android.util.Log;

import com.wj.appprotect.shell.LogUtils;
import com.wj.gradle.plugin.annotation.WjRouter;
import com.wj.gradle.plugin.crash.CrashHandler;

/**
 * create by wenjing.liu at 2022/2/9
 */
@WjRouter(key = "/application")
public class GradlePluginApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("", "GradlePluginApplication onCreate");
        // LogUtils.logV(getApplicationInfo().className);
        //  LogUtils.logV(getApplicationInfo().name);
        Log.d("", getPackageName());
        String crashFile = getCacheDir().getAbsolutePath() + "/crash.hprof";
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(crashFile));
    }
}
