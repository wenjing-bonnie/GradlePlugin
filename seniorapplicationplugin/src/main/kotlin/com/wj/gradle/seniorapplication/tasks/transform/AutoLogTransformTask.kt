package com.wj.gradle.seniorapplication.tasks.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.wj.gradle.seniorapplication.extensions.SeniorApplicationKotlinExtension
import com.wj.gradle.seniorapplication.tasks.transform.parallel.AutoLogWorkAction
import com.wj.gradle.seniorapplication.tasks.transform.parallel.AutoLogWorkParameters
import com.wj.gradle.seniorapplication.utils.SystemPrint
import org.gradle.api.Project
import org.gradle.workers.WorkerExecutor
import java.io.File
import java.io.FileInputStream
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.FileOutputStream

/**
 * Created by wenjing.liu on 2021/10/9 in J1.
 *
 * @author wenjing.liu
 */
open class AutoLogTransformTask(val project: Project) : Transform() {
    private val TAG: String = "AutoLogTask"

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
        SystemPrint.outPrintln(TAG, "" + workerExecutor)

        //TODO  根据需求选择对应的代码来打包发布插件
        val inputs = transformInvocation.inputs
        //val inputs = transformInvocation.referencedInputs
        val outputsProvider = transformInvocation.outputProvider
        transformInput(inputs, outputsProvider, workerExecutor)
    }

    /**
     * 处理inputs
     */
    private fun transformInput(
        inputs: Collection<TransformInput>,
        outputsProvider: TransformOutputProvider,
        workerExecutor: WorkerExecutor
    ) {
        val workQueue = workerExecutor.noIsolation()
        SystemPrint.outPrintln(TAG, "" + workQueue)
        val classFileHandler =
            AutoLogClassFileHandler(TAG, getAutoLogTimeoutFromExtension())
        inputs.forEach {
            it.jarInputs.forEach { jar ->
                //处理文件 TODO 暂时不处理jar文件
                // handlerInputFiles(jar.file)
                //写入文件
                classFileHandler.writeOutputFile(
                    jar,
                    Format.JAR,
                    getOutputFileFromOutputsProvider(jar, Format.JAR, outputsProvider)
                )
            }

            it.directoryInputs.forEach { directory ->
                //方案一：非并行
//                classFileHandler.handlerDirectoryInputFiles(
//                    directory,
//                    getOutputFileFromOutputsProvider(directory, Format.DIRECTORY, outputsProvider)
//                )
                //方案二：并行
                workQueue.submit(AutoLogWorkAction::class.javaObjectType) { param: AutoLogWorkParameters ->
                    param.autoLogTimeout.set(getAutoLogTimeoutFromExtension())
                    param.directoryInput.set(directory)
                    param.outputFile.set(
                        getOutputFileFromOutputsProvider(
                            directory,
                            Format.DIRECTORY,
                            outputsProvider
                        )
                    )
                }
            }
        }
    }

    /**
     * 获取需要打印日志的时间
     */
    private fun getAutoLogTimeoutFromExtension(): Long {
        val extension =
            project.extensions.findByType(SeniorApplicationKotlinExtension::class.javaObjectType)
                ?: return 300L
        return extension.autoLog().autoLogTimeout()
    }

    private fun getOutputFileFromOutputsProvider(
        input: QualifiedContent,
        format: Format,
        outputsProvider: TransformOutputProvider
    ): File? {

        if (outputsProvider == null) {
            return null
        }
        val outputFile = outputsProvider.getContentLocation(
            input.name,
            input.contentTypes,
            input.scopes,
            format
        )
        return outputFile
    }

}