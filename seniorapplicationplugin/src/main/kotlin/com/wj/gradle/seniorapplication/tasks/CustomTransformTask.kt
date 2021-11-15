package com.wj.gradle.seniorapplication.tasks

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.wj.gradle.seniorapplication.utils.SystemPrint
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

/**
 * Created by wenjing.liu on 2021/10/9 in J1.
 *
 * @author wenjing.liu
 */
open class CustomTransformTask : Transform() {
    private val TAG: String = "CustomTask"

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
        if (transformInvocation == null) {
            return
        }
        transformInvocation.referencedInputs
        //跟project有关的内容
        val context = transformInvocation.context
        SystemPrint.outPrintln(
            TAG,
            "project name is ${context.projectName}\n" +
                    "variantName is ${context.variantName} \n" +
                    "path is ${context.path}\n "
        )
        //可直接获取到workerExecutor,并发执行
        val workerExecutor = context.workerExecutor

        //TODO  根据需求选择对应的代码来打包发布插件
        val inputs = transformInvocation.inputs
        //val inputs = transformInvocation.referencedInputs
        val outputsProvider = transformInvocation.outputProvider
        transformInput(inputs, outputsProvider)
    }

    /**
     * 处理inputs
     */
    private fun transformInput(
        inputs: Collection<TransformInput>,
        outputsProvider: TransformOutputProvider
    ) {
        inputs.forEach {
            it.jarInputs.forEach { jar ->
                //处理文件
                handlerInputFiles(jar.file)
                //写入文件
                writeOutputFile(jar, Format.JAR, outputsProvider)
            }

            it.directoryInputs.forEach { directory ->
                //处理文件
                handlerInputFiles(directory.file)
                //写入文件
                writeOutputFile(directory, Format.DIRECTORY, outputsProvider)

            }
        }
    }

    private fun handlerInputFiles(inputFile: File) {
        SystemPrint.outPrintln(TAG, "handler file is \n${inputFile.absolutePath}\n")
    }

    /**
     * 复制文件内容
     */
    private fun writeOutputFile(
        input: QualifiedContent,
        format: Format,
        outputsProvider: TransformOutputProvider
    ) {
        if (outputsProvider == null) {
            return
        }
        val outputFile = outputsProvider.getContentLocation(
            input.name,
            input.contentTypes,
            input.scopes,
            format
        )
        FileUtils.copyFile(input.file, outputFile)
    }


}