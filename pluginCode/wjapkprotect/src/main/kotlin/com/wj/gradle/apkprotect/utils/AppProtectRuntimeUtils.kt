package com.wj.gradle.apkprotect.utils

import com.android.build.gradle.AppExtension
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.Project
import java.io.ByteArrayOutputStream

/**
 * 通过runtime执行指令
 */
object AppProtectRuntimeUtils {
    val TAG = "AppProtectRuntimeUtils"

    /**
     * 执行指令
     * @param command 该指令无需添加build tools的绝对路径
     *
     * @return 返回执行指令的结果:""为正常执行；否则为错误信息
     */
    fun runtimeExecCommand(command: String, project: Project): String {
        return runtimeExecCommand("${getBuildToolsPath(project)}/$command")
    }

    /**
     * 执行指令
     * @param command
     *
     * @return 返回执行指令的结果:""为正常执行；否则为错误信息
     */
    fun runtimeExecCommand(command: String): String {
        val runtime = Runtime.getRuntime()
        SystemPrint.outPrintln("The running command is \n$command")
        val process = runtime.exec(command)
        try {
            process.waitFor()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val exitValue = process.exitValue()
        if (exitValue == 0) {
            process.destroy()
            return ""
        }
        //错误信息输出
        val inputStream = process.errorStream
        var buffer = ByteArray(1024)
        val bos = ByteArrayOutputStream()
        var len = inputStream.read(buffer)
        while (len != -1) {
            bos.write(buffer, 0, len)
            len = inputStream.read(buffer)
        }
        bos.close()
        process.destroy()
        return "Runtime failed , error is \n${String(bos.toByteArray(), Charsets.UTF_8)}"
    }


    /**
     * 获取编译工具的路径
     */
    private fun getBuildToolsPath(project: Project): String {
        val androidExtension =
            project.extensions.findByType(AppExtension::class.javaObjectType)
                ?: return ""
        return "${androidExtension.sdkDirectory.absolutePath}/build-tools/${androidExtension.buildToolsVersion}"
    }

    /**
     * 打印运行结果
     */
    fun printRuntimeResult(error: String, okValue: String) {
        if (error.isEmpty()) {
            SystemPrint.outPrintln(
                TAG,
                okValue
            )
            return
        }
        //错误信息输出
        SystemPrint.errorPrintln(TAG, error)
    }
}