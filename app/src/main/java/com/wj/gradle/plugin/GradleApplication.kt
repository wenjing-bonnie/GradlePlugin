package com.wj.gradle.plugin

import android.app.Application
import android.util.Log

/**
 * Created by wenjing.liu on 2021/12/20 in J1.
 *
 * @author wenjing.liu
 */
class GradleApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        Log.d("onCreate", "onCreate")
    }


}