package com.wj.gradle.plugin.cache

import android.util.LruCache

/**
 * Created by wenjing.liu on 2021/12/22 in J1.
 *
 * 内存缓存
 *
 * @author wenjing.liu
 */
open class StatMemoryLruCache() :
    LruCache<String, String>((Runtime.getRuntime().maxMemory() / 8).toInt()) {


//    private lateinit var softReference
//
//        init {
//            softReference = LinkedHashMap<String,String>()
//        }
}