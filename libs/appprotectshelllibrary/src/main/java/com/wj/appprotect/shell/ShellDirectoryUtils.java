package com.wj.appprotect.shell;

import android.content.Context;

import java.io.File;

/**
 * create by wenjing.liu at 2022/3/9
 */
public class ShellDirectoryUtils {


    public static File getUnzipDirectory(Context context) {
        return new File(getDecryptRootDirectory(context) + "/unzip");
    }

    public static File getDecryptDirectory(Context context) {
        return new File(getUnzipDirectory(context).getPath() + "/decrypt");
    }

    public static File getOptimizedDirectory(Context context) {
        return new File(getDecryptRootDirectory(context) + "/odx");
    }

    /**
     * 一开始使用{@link Context#getCacheDir()}提示IOException No original dex files found for dex location
     *
     * @param context
     * @return
     */
    private static String getDecryptRootDirectory(Context context) {
        return context.getFilesDir().getPath();
    }
}
