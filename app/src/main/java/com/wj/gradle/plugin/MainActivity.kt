package com.wj.gradle.plugin

import android.app.Activity
import android.os.Bundle

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val code = ByteCode()
        code.sumMethod(1, 5)
        // code.stringMethod()
    }
}