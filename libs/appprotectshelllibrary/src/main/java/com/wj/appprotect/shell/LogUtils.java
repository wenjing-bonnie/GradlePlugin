package com.wj.appprotect.shell;

import android.util.Log;

public class LogUtils {

    private static boolean DEBUG = true;
    private static String TAG = "AppProtectShellApplication";


    public static void logV(String msg) {
        Log.v(TAG, msg);
    }
}
