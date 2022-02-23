package com.wj.gradle.plugin;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.wj.appprotect.shell.LogUtils;

import androidx.annotation.Nullable;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("",getApplication().toString());
        String info = null;
        //LogUtils.logV("" + info.toString());
    }
}