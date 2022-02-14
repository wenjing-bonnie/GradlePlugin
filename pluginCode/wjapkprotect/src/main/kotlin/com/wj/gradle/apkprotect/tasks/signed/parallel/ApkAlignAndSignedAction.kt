package com.wj.gradle.apkprotect.tasks.signed.parallel

import com.wj.gradle.apkprotect.utils.AppProtectRuntimeUtils
import org.gradle.api.Project
import org.gradle.workers.WorkAction
import java.io.File

/**
 * create by wenjing.liu at 2022/2/14
 */
abstract class ApkAlignAndSignedAction : WorkAction<ApkAlignAndSignedParameters> {

    override fun execute() {
        val apkUnsignedFile = parameters.apkUnsignedFile.get().asFile
        val project = parameters.project.get()
        zipAlign(apkUnsignedFile, project)
        apkSigned(apkUnsignedFile, project)
    }

    /**
     * 对齐
     * zipalign 是对zip包对齐的工具,使APK包内未压缩的数据有序排列对齐,从而减少APP运行时内存消耗
     * zipalign可以在V1签名后执行, 但zipalign不能在V2签名后执行,只能在V2签名之前执行！！！
     */
    private fun zipAlign(apkUnsignedFile: File, project: Project) {
        val command = "zipalign -v -p 4 ${apkUnsignedFile.absolutePath}"
        val error = AppProtectRuntimeUtils.runtimeExecCommand(command, project)
        val okValue = "zip align is ok !"
        AppProtectRuntimeUtils.printRuntimeResult(error, okValue)
    }

    private fun apkSigned(apkUnsignedFile: File, project: Project) {
        val command = " apksigner sign --ks debug.keystore ${apkUnsignedFile.absolutePath}"
        val error = AppProtectRuntimeUtils.runtimeExecCommand(command, project)
        val okValue = "apk sign is ok !"
        AppProtectRuntimeUtils.printRuntimeResult(error, okValue)
    }
}