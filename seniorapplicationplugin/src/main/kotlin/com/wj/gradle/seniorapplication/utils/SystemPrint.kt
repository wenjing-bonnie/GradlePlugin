package com.wj.gradle.manifest.utils

import com.android.build.gradle.internal.cxx.logging.errorln
import com.android.build.gradle.internal.cxx.logging.warnln
import com.wj.gradle.manifest.tasks.others.IncrementalOnDefaultTask
import com.wj.gradle.seniorapplication.tasks.lazy.LazyConfigurationTask

/**
 * Created by wenjing.liu on 2021/10/8 in J1.
 *
 * 使用object相当于类中的所有方法、属性都为静态方法
 * @author wenjing.liu
 */
object SystemPrint {
    var DEBUG: Boolean = true
    var TAG: String = "ManifestKotlinProject"

    /**
     * 日志输出
     */
    fun outPrintln(info: String) {
        if (isDebugLog(TAG)) {
            println("<- $TAG -> : $info")
        }
    }

    /**
     * 日志输出
     */
    fun outPrintln(tag: String, info: String) {
        if (!isDebugLog(tag)) {
            return
        }
        println(
            "<- ${getTag(tag)} -> : $info"
        )
    }

    /**
     * 输出红色log
     */
    fun errorPrintln(tag: String, info: String) {
        if (!isDebugLog(tag)) {
            return
        }
        errorln(
            "<- ${getTag(tag)} -> : $info"
        )
    }

    /**
     * 警告log
     */
    fun warnPrintln(tag: String, info: String) {
        if (!isDebugLog(tag)) {
            return
        }
        warnln(
            "<- ${getTag(tag)} -> : $info"
        )
    }

    /**
     * 获取可用的tag
     */
    private fun getTag(tag: String): String {
        var length = tag.length
        if (length <= 30) {
            return tag
        }

        return "${tag.subSequence(0, 15)}...${
            tag.subSequence(
                length - 14,
                length
            )
        }"
    }

    private fun isDebugLog(tag: String): Boolean {
        return DEBUG && !isConfigHideTag(tag)
    }

    private fun isConfigHideTag(tag: String): Boolean {
        return tag == LazyConfigurationTask.TAG ||
                tag == IncrementalOnDefaultTask.TAG
    }
}