package com.wj.gradle.manifest.extensions.utils

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
        if (DEBUG) {
            println("<- $TAG -> : $info")
        }
    }

    /**
     * 日志输出
     */
    fun outPrintln(tag: String, info: String) {
        if (!DEBUG) {
            return
        }
        var length = tag.length
        if (length <= 30) {
            println("<- $tag -> : $info")
            return
        }

        println(
            "<- ${tag.subSequence(0, 15)}...${
                tag.subSequence(
                    length - 14,
                    length
                )
            } -> : $info"
        )
    }
}