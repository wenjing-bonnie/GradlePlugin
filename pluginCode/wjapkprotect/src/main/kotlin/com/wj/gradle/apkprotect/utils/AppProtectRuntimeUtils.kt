package com.wj.gradle.apkprotect.utils

import java.io.ByteArrayOutputStream

/**
 * 通过runtime执行指令
 */
open class AppProtectRuntimeUtils {
    /**
     * 执行指令
     * @return 返回执行指令的结果:""为正常执行；否则为错误信息
     */
    open fun runtimeExecCommand(command: String): String {
        val runtime = Runtime.getRuntime()
        val process =
            runtime.exec(command)
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
}