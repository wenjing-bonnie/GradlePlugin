package com.wj.gradle.plugin;

import android.app.Application;

import com.wj.appprotect.shell.LogUtils;
import com.wj.gradle.plugin.crash.CrashHandler;

/**
 * create by wenjing.liu at 2022/2/9
 */
public class GradlePluginApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.logV("GradlePluginApplication onCreate");
        LogUtils.logV(getApplicationInfo().className);
        LogUtils.logV(getApplicationInfo().name);
        LogUtils.logV(getPackageName());
        String crashFile = getCacheDir().getAbsolutePath() + "/crash.hprof";
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(crashFile));
    }
}
