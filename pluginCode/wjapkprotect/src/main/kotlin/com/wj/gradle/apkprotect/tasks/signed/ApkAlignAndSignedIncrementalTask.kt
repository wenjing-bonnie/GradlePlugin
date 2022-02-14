package com.wj.gradle.apkprotect.tasks.signed

import com.wj.gradle.apkprotect.tasks.base.NewIncrementalWithoutOutputsTask
import com.wj.gradle.apkprotect.tasks.signed.parallel.ApkAlignAndSignedAction
import com.wj.gradle.apkprotect.utils.AppProtectRuntimeUtils
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File

/**
 * 对Apk签名对齐
 */
abstract class ApkAlignAndSignedIncrementalTask : NewIncrementalWithoutOutputsTask() {
    companion object {
        val TAG = "ApkAlignAndSignedIncrementalTask"
    }

    @get:InputDirectory
    @get:Incremental
    abstract val apkUnsignedDirectory: DirectoryProperty

    override fun doTaskAction(inputChanges: InputChanges) {
        val workQueue = workerExecutor.noIsolation()
        val apks = apkUnsignedDirectory.get().asFile.listFiles()
        for (apk in apks) {
            SystemPrint.outPrintln(TAG, apk.absolutePath)
            if (apk.isDirectory || !apk.endsWith(".apk")) {
                continue
            }
            // Could not serialize value of type DefaultProject，所以不使用并行Task
//            workQueue.submit(ApkAlignAndSignedAction::class.javaObjectType) {
//                it.apkUnsignedFile.set(apk)
//                it.project.set(project)
//            }
            zipAlign(apk)
            apkSigned(apk)
        }
    }

    /**
     * 对齐 https://developer.android.google.cn/studio/command-line/zipalign.html
     * zipalign 是对zip包对齐的工具,使APK包内未压缩的数据有序排列对齐,从而减少APP运行时内存消耗
     * zipalign可以在V1签名后执行, 但zipalign不能在V2签名后执行,只能在V2签名之前执行！！！
     * //zipalign -p -f -v 4 infile.apk outfile.apk
     * //如需确认 existing.apk 的对齐方式，请运行以下命令：
     * //zipalign -c -v 4 existing.apk
     */
    private fun zipAlign(apkUnsignedFile: File) {
        val command = "zipalign -c -v 4 ${apkUnsignedFile.absolutePath}"
        val error = AppProtectRuntimeUtils.runtimeExecCommand(command, project)
        val okValue = "zip align is ok !"
        AppProtectRuntimeUtils.printRuntimeResult(error, okValue)
    }

    /**
     * 签名 https://developer.android.google.cn/studio/command-line/apksigner
     * apksigner sign --ks 密钥库名 --ks-key-alias 密钥别名 xxx.apk
     *
     * 默认的debug模式的签名在/Users/liuwenjing/.android/debug.keystore
     * Keystore name: “debug.keystore”
     * Keystore password: “android”
     * Key alias: “androiddebugkey”
     * Key password: “android”
     *
     */
    private fun apkSigned(apkUnsignedFile: File) {
        val command =
            "apksigner sign --ks /Users/liuwenjing/.android/debug.keystore --ks-key-alias androiddebugkey --ks-pass pass:android --key-pass pass:android ${apkUnsignedFile.absolutePath}"
        val error = AppProtectRuntimeUtils.runtimeExecCommand(command, project)
        val okValue = "apk sign is ok !"
        AppProtectRuntimeUtils.printRuntimeResult(error, okValue)
    }
}