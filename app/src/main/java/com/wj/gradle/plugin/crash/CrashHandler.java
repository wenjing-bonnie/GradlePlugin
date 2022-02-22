package com.wj.gradle.plugin.crash;

import android.os.Debug;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import androidx.annotation.NonNull;

/**
 * create by wenjing.liu at 2022/2/22
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private String fileName;

    public CrashHandler(String name){
        this.fileName = name;
    }
    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        //Toast.makeText()
        Log.d("CrashHandler", getErrorInfo(e));
        try {
            Debug.dumpHprofData(fileName);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private String getErrorInfo(Throwable e) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        e.printStackTrace(pw);
        pw.close();
        return writer.toString();
    }
}
