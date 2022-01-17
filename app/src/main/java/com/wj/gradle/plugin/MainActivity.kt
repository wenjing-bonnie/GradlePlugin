package com.wj.gradle.plugin

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val code = ByteCode()
        code.sumMethod(1, 5)
        // code.stringMethod()
        val imageView = findViewById<ImageView>(R.id.iv_glide)

        Glide.with(this).load("http://...(url address)")
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .
            .into(imageView)
    }
}