package com.wj.appprotect.shell;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.wj.appprotect.shell.algroithm.AesFileAlgorithm;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Arrays;

public class AppProtectShellApplication extends Application {
    private String TAG = "AppProtectShellApplication";
    private LoadApkAlreadyEncodeDexsUtils encodeDexsUtils = new LoadApkAlreadyEncodeDexsUtils();


    /**
     * 最早执行的代码，在这之前，其实最早执行的是注册的contentprovider
     *
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //TODO 需要优化整个过程：这些内容要写到native/so文件中，下面的内容应该通过加载native或so文件来完成对应的操作

        //第一步：解压.apk文件
        String unzipFilesPath = unzipApk(base);
        //第二步：找出所有的加密的dex文件，除去壳.dex.对加密dex进行解密,写回到解压文件夹内
        File[] decodeDexs = decodeAllDexs(unzipFilesPath);
        //通过hook主动去加载dex，同热修复。参照tentent/thinker热修复 SystemClassLoader…类进行加载
        //一开始的加密dex也会在dex数组中，反射dex数组，将解密之后的所有dex加入到dex数组中
        //通过classloader来加载到解密dex的类，就不会在去加密的dex
        //所以反射dex在内存中的数组：将解密的dex加载到dex数组中
        //classloader：首先会判断类是否存在，若存在则直接加载，否则通过classloader进行加载
        ReInstallDecodeDexsUtils.reInstallDexes(this, Arrays.asList(decodeDexs));
        //在onCreate()中需要使用hook将原来的application加载进来
        //

    }

    /**
     * 通过hook将原应用的application加载进来
     * 涉及到ActivityThread反射替换成原应用的application
     * mInitalAppliation
     */
    @Override
    public void onCreate() {
        super.onCreate();
        ReplaceApplicationUtils.replaceApplication(this);
    }

    /**
     * 解压.apk文件
     *
     * @param context
     * @return
     */
    private String unzipApk(Context context) {
        File apkFile = new File(getApplicationInfo().sourceDir);
        Log.d(TAG, apkFile.getAbsolutePath());
        String descDirPath = context.getCacheDir() + "/unzip";
        String unzipFilesPath = encodeDexsUtils.unZipApk(apkFile, descDirPath);
        LogUtils.logV("unzipFilesPath = \n" + unzipFilesPath);
        return unzipFilesPath;
    }

    /**
     * 找出所有的加密的dex文件，除去壳.dex，然后对加密dex进行解密
     *
     * @param unzipFilesPath
     */
    private File[] decodeAllDexs(String unzipFilesPath) {
        File[] dexFiles = new File(unzipFilesPath).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".dex") && !name.equals("shell.dex");
            }
        });
        if (dexFiles == null) {
            try {
                throw new InvalidObjectException("Invalid apk !");
            } catch (InvalidObjectException e) {
                e.printStackTrace();
            }
            return null;
        }
        for (File dex : dexFiles) {
            // LogUtils.logV("dex = \n" + dex.getName());
            AesFileAlgorithm aesFileAlgorithm = new AesFileAlgorithm();
            aesFileAlgorithm.decrypt(dex);
        }
        return dexFiles;
    }
}
