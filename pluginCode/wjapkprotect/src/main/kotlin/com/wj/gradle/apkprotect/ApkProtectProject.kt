package com.wj.gradle.apkprotect

import com.android.build.api.transform.Transform
import com.wj.gradle.apkprotect.algroithm.duichen.AesAlgorithm
import com.wj.gradle.base.WjVariantBaseProject
import com.wj.gradle.base.tasks.TaskWrapper
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.Project

/**
 * Created by wenjing.liu on 2022/1/19 in J1.
 *
 * @author wenjing.liu
 */
open class ApkProtectProject : WjVariantBaseProject() {


    override fun applyExceptRegister(project: Project) {
        val aesAlgorithm = AesAlgorithm()
        val encode = aesAlgorithm.encryptToBase64("123456") ?: return
        val decode = aesAlgorithm.decryptFromBase64ToString(encode)
        SystemPrint.outPrintln("encode = " + aesAlgorithm.encryptBytes2Hex(encode) + " \n decode = " + decode)
    }

    override fun getAfterEvaluateTasks(): MutableList<TaskWrapper> {
        return mutableListOf()
    }

    override fun getRegisterTransformTasks(): MutableList<Transform> {
        return mutableListOf()
    }


}