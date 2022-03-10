package com.wj.appprotect.shell;

import android.content.Context;

import java.io.File;

/**
 * create by wenjing.liu at 2022/3/9
 * <p>
 * 所有的文件夹
 */
public class ShellDirectoryUtils {

    /**
     * 解压缩文件夹
     *
     * @param context
     * @return
     */
    public static File getUnzipDirectory(Context context) {
        File directory = new File(getDecryptRootDirectory(context) + "/unzip");
        createDirectory(directory);
        return directory;
    }

    /**
     * 解密文件夹
     *
     * @param context
     * @return
     */
    public static File getDecryptDirectory(Context context) {
        File directory = new File(getUnzipDirectory(context).getPath() + "/decrypt");
        createDirectory(directory);
        return directory;
    }

    /**
     * odx文件夹
     *
     * @param context
     * @return
     */
    public static File getOptimizedDirectory(Context context) {
        File directory = new File(getDecryptRootDirectory(context) + "/odx");
        createDirectory(directory);
        return directory;
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

    private static void createDirectory(File directory) {
        if (directory.exists()) {
            return;
        }
        directory.mkdirs();

    }
}
