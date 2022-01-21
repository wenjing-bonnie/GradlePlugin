package com.wj.gradle.apkprotect

import com.android.build.api.transform.Transform
import com.wj.gradle.apkprotect.algroithm.duichen.AbstractAesAlgorithm
import com.wj.gradle.apkprotect.algroithm.duichen.AesFileAlgorithm
import com.wj.gradle.apkprotect.algroithm.duichen.AesStringAlgorithm
import com.wj.gradle.base.WjVariantBaseProject
import com.wj.gradle.base.tasks.TaskWrapper
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.Project
import java.io.File

/**
 * Created by wenjing.liu on 2022/1/19 in J1.
 *
 * @author wenjing.liu
 */
open class ApkProtectProject : WjVariantBaseProject() {


    override fun applyExceptRegister(project: Project) {
        aesString()
        aesFile()
    }


    override fun getAfterEvaluateTasks(): MutableList<TaskWrapper> {
        return mutableListOf()
    }

    override fun getRegisterTransformTasks(): MutableList<Transform> {
        return mutableListOf()
    }


    fun aesString() {
        val aesAlgorithm = AesStringAlgorithm()
        val encode = aesAlgorithm.encryptToBase64("123456") ?: return
        val decode = aesAlgorithm.decryptFromBase64ToString(encode)
        SystemPrint.outPrintln("encode = " + aesAlgorithm.encryptBytes2Hex(encode) + " \n decode = " + decode)
    }

    fun aesFile() {
        val aes = AesFileAlgorithm()
        val path =
            "/Users/j1/Documents/android/code/GradlePlugin/pluginCode/wjapkprotect/src/main/kotlin/com/wj/gradle/apkprotect/ApkProtectProject.kt"
        val encodeFile = aes.encrypt(File(path)) ?: return
        val decodeFile = aes.decrypt(encodeFile)
    }


}