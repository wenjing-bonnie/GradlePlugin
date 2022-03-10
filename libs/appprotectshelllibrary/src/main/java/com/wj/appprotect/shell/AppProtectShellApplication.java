package com.wj.appprotect.shell;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.wj.appprotect.shell.algroithm.java.AesFileAlgorithm;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO 加固plugin需要考虑：
 * 1:怎么给到原APP的Application:{@link #getOriginalApplicationName()}
 */
public class AppProtectShellApplication extends Application {
    private String TAG = "AppProtectShellApplication";
    private LoadApkAlreadyEncodeDexsUtils encodeDexsUtils = new LoadApkAlreadyEncodeDexsUtils();
    private Application originalApplication;


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
        List<File> decodeDexs = decodeAllDexs(unzipFilesPath);
        //通过hook主动去加载dex，同热修复。参照tentent/thinker热修复 SystemClassLoader…类进行加载
        //一开始的加密dex也会在dex数组中，反射dex数组，将解密之后的所有dex加入到dex数组中
        //通过classloader来加载到解密dex的类，就不会在去加密的dex
        //所以反射dex在内存中的数组：将解密的dex加载到dex数组中
        //classloader：首先会判断类是否存在，若存在则直接加载，否则通过classloader进行加载
        ReInstallDecodeDexsUtils.reInstallDexes(this, decodeDexs);
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
        handleBindOriginalApplication();
    }


    @Override
    public String getPackageName() {
        //强制改变packageName，让installContentProviders()执行createPackageContext()传入原APP的Application
        //这里不会影响到APP在使用Application通过getPackageName()获取包名，
        // 因为APP启动后已经替换成了原APP的Application
        if (!getOriginalApplicationName().isEmpty()) {
            return "";
        }
        return super.getPackageName();
    }

    @Override
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        if (getOriginalApplicationName().isEmpty()) {
            return super.createPackageContext(packageName, flags);
        }
        handleBindOriginalApplication();
        return originalApplication;
    }

    /**
     * 完成原APP的Application的创建->attach()->onCreate()
     */
    private void handleBindOriginalApplication() {
        if (originalApplication != null) {
            return;
        }
        if (getOriginalApplicationName().isEmpty()) {
            return;
        }
        originalApplication = ReplaceApplicationUtils.replaceApplication(this, getOriginalApplicationName());
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

        File descDirFile = ShellDirectoryUtils.getUnzipDirectory(context);
        String unzipFilesPath = encodeDexsUtils.unZipApk(apkFile, descDirFile.getPath());
        return unzipFilesPath;
    }

    /**
     * 找出所有的加密的dex文件，除去壳.dex，然后对加密dex进行解密
     *
     * @param unzipApkFilesPath
     */
    private List<File> decodeAllDexs(String unzipApkFilesPath) {
        File[] dexFiles = new File(unzipApkFilesPath).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".dex");
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
        List<File> dexList = new ArrayList();
        AesFileAlgorithm aesFileAlgorithm = new AesFileAlgorithm();
        for (int i = 0; i < dexFiles.length; i++) {
            File dex = dexFiles[i];
            if (!dex.getName().equals("classes.dex")) {
                dexList.add(aesFileAlgorithm.decrypt(dex, getDecryptFile(dex)));
            }
        }
        return dexList;
    }

    private File getDecryptFile(File dex) {
        File directory = ShellDirectoryUtils.getDecryptDirectory(getBaseContext());
        return new File(directory, dex.getName());
    }

    /**
     * 获取原APP的Application的名字
     * TODO 这里需要在进行优化
     * 因为通过gradle将原APP注册的Application替换了该解密壳的Application
     * 所以要将原APP的Application想方法保存下来，暂时先直接赋值字符串。
     * 另外还要判断若APP中没有配置Application，则直接使用"com.android.Application"
     *
     * @return
     */
    private String getOriginalApplicationName() {
        //先假设原Manifest中配置的是是名字
        String dexApplication = "com.wj.gradle.plugin.GradlePluginApplication";
        //否则设置为"com.android.Application"
        return dexApplication;
    }
}
