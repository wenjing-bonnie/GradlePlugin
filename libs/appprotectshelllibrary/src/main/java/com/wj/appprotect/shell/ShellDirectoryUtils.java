package com.wj.appprotect.shell;

import android.content.Context;

import java.io.File;

/**
 * create by wenjing.liu at 2022/3/9
 */
public class ShellDirectoryUtils {


    public static File getUnzipDirectory(Context context) {
      File directory =   new File(getDecryptRootDirectory(context) + "/unzip");
        createAndDeleteDirectory(directory);
        return directory;
    }

    public static File getDecryptDirectory(Context context) {
        File directory = new File(getUnzipDirectory(context).getPath() + "/decrypt");
        createAndDeleteDirectory(directory);
        return directory;
    }

    public static File getOptimizedDirectory(Context context) {
        File directory = new File(getDecryptRootDirectory(context) + "/odx");
        createAndDeleteDirectory(directory);
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

    private static void createAndDeleteDirectory(File directory){
        if (directory.exists()) {
            directory.delete();
        } else {
            directory.mkdirs();
        }
    }
}
