package com.wj.gradle.seniorapplication.tasks

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.wj.gradle.seniorapplication.utils.SystemPrint

/**
 * Created by wenjing.liu on 2021/10/9 in J1.
 *
 * @author wenjing.liu
 */
open class CustomTransformTask : Transform() {
    private val TAG: String = "Custom"

    /**
     * 返回的是该Task的名字
     * 通常该名字的构成包括
     * "transform{ContentType}With{设置的name}TaskFor{buildType}{productFlavor}"
     */
    override fun getName(): String {
        return TAG
    }

    /**
     * 返回的是Transform要处理的数据类型
     */
    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 返回Transform的修改input文件的作用域
     */
    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * 返回的是Transform要处理的input文件的作用域
     */
    override fun getReferencedScopes(): MutableSet<in QualifiedContent.Scope> {
        return super.getReferencedScopes()
    }

    /**
     * 是否为增量编译
     */
    override fun isIncremental(): Boolean {
        return true
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        SystemPrint.outPrintln(TAG, transformInvocation.toString())
        if (transformInvocation == null) {
            return
        }
        //跟project有关的内容
        val context = transformInvocation.context
        SystemPrint.outPrintln(
            TAG,
            "project name is ${context.projectName}\n variantName is ${context.variantName} \n" +
                    "path is ${context.path}\n "
        )
        val workerExecutor = context.workerExecutor
        SystemPrint.outPrintln(TAG, workerExecutor.toString())
        val inputs = transformInvocation.inputs
        inputs.forEach {
            it.jarInputs.forEach { jar ->
                SystemPrint.outPrintln(TAG, "jar name is ${jar.name}\n")
            }

            it.directoryInputs.forEach { directory ->
                SystemPrint.outPrintln(TAG, "directory is ${directory.name}")
            }

        }

        val outputsProvider = transformInvocation.outputProvider
    }
}