package com.wj.appprotect.shell;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;

public class AppProtectShellApplication extends Application {
    private String TAG = "AppProtectShellApplication";


    /**
     * 最早执行的代码，在这之前，其实最早执行的是注册的contentprovider
     *
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //解压.apk文件
        File apkFile = new File(getApplicationInfo().sourceDir);
        Log.d(TAG, apkFile.getAbsolutePath());

        //找出所有的dex文件，除去壳.dex

        //写回到解压文件夹内

        //通过hook主动去加载dex，同热修复。参照tentent/thinker热修复 SystemClassLoader…类进行加载
        //一开始的加密dex也会在dex数组中，反射dex数组，将解密之后的所有dex加入到dex数组中
        //通过classloader来加载到解密dex的类，就不会在去加密的dex
        //所以反射dex在内存中的数组：将解密的dex加载到dex数组中
        //classloader：首先会判断类是否存在，若存在则直接加载，否则通过classloader进行加载


        //在onCreate()中需要使用hook将原来的application加载进来
    }

    /**
     * 通过hook将原应用的application加载进来
     * 涉及到ActviityThread反射替换成原应用的application
     * mInitalAppliation
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
