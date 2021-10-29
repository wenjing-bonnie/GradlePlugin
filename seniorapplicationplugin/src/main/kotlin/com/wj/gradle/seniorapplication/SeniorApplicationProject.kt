package com.wj.gradle.seniorapplication

import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by wenjing.liu on 2021/10/29 in J1.
 * 专门自定义Gradle 高级进阶
 * @author wenjing.liu
 */
open class SeniorApplicationProject : Plugin<Project> {
    override fun apply(p0: Project) {
        SystemPrint.outPrintln("Welcome SeniorApplicationProject")

    }
}