package com.wj.gradle.seniorapplication.tasks.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.wj.gradle.seniorapplication.extensions.SeniorApplicationKotlinExtension
import com.wj.gradle.seniorapplication.utils.SystemPrint
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
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
                //处理文件 TODO 暂时不处理jar文件
                // handlerInputFiles(jar.file)
                //写入文件
                writeOutputFile(jar, Format.JAR, outputsProvider)
            }

            it.directoryInputs.forEach { directory ->
                //处理文件
                handlerDirectoryInputFiles(directory.file)
                //写入文件
                writeOutputFile(directory, Format.DIRECTORY, outputsProvider)

            }
        }
    }

    /**
     * 处理所有文件夹里的.class文件
     */
    private fun handlerDirectoryInputFiles(inputFile: File) {
        if (inputFile.isDirectory) {
            var filesInDirectory = inputFile.listFiles()
            filesInDirectory.forEach {
                handlerDirectoryInputFiles(it)
            }
        } else {
            handlerDirectoryInputClassFileByAsm(inputFile)
        }
    }

    /**
     * 处理单个的.class文件
     */
    private fun handlerDirectoryInputClassFileByAsm(inputClassFile: File) {
        SystemPrint.outPrintln(TAG, "handler .class is \n${inputClassFile.absolutePath}\n")
        if (!inputClassFile.name.endsWith(".class")) {
            return
        }
        //TODO  test
//        if (!inputClassFile.name.endsWith("ByteCode.class")) {
//            return
//        }
        //1.创建ClassReader
        val fis = FileInputStream(inputClassFile)
        val classReader = ClassReader(fis)

        //2.创建ClassWriter
        val classWriter =
            ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES)
        //3.实例化自定义的AutoLogClassVisitor
        val autoLogClassVisitor = AutoLogClassVisitor(classWriter, getAutoLogTimeout())
        //4.注册AutoLogClassVisitor
        classReader.accept(autoLogClassVisitor, ClassReader.SKIP_FRAMES)

        //5.将修改之后的.class文件重新写入到该文件中
        val fos = FileOutputStream(inputClassFile)
        fos.write(classWriter.toByteArray())
        fos.close()
        fis.close()
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
        if (format == Format.JAR) {
            FileUtils.copyFile(input.file, outputFile)
        } else {
            FileUtils.copyDirectory(input.file, outputFile)
        }
    }


    private fun getAutoLogTimeout(): Long {
        val extension =
            project.extensions.findByType(SeniorApplicationKotlinExtension::class.javaObjectType)
        if (extension == null) {
            return 300L
        }
        return extension.autoLog().autoLogTimeout()
    }


}