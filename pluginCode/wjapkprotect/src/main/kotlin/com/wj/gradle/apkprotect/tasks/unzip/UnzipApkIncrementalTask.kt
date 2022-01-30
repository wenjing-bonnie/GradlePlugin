package com.wj.gradle.apkprotect.tasks.unzip

import com.android.build.gradle.internal.tasks.NewIncrementalTask
import com.wj.gradle.apkprotect.extensions.ApkProtectExtension
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File
import java.lang.IllegalArgumentException
import java.util.regex.Pattern

/**
 * 增量编译,可直接创建并行Task
 *
 * 解压缩apk
 *
 * 1.配置.apk存放的路径.
 * 默认的取["${project.projectDir.absolutePath}/build/outputs/apk/"]
 * 2.配置解压之后的apk存放的路径.
 * 默认取["${project.projectDir.absolutePath}/build/protect/]
 */
abstract class UnzipApkIncrementalTask : NewIncrementalTask() {

    companion object {
        const val TAG: String = "UnzipApkIncrementalTask"
    }

    //必须在实例化该Task通过set进行赋值,否则会抛出异常
    @get:Incremental
    @get:InputDirectory
    abstract val lazyApkDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val unzipDirectory: DirectoryProperty

    /**
     * 存放所有的apk文件
     */
    private val allApks = mutableListOf<File>()

    override fun doTaskAction(inputChanges: InputChanges) {
        SystemPrint.outPrintln(TAG, "The unZip begin ...")
        val workqueue = workerExecutor.noIsolation()
        allApks.clear()
        val apkDirectory = lazyApkDirectory.get().asFile
        getAllApksFromApkDirectory(apkDirectory)
        for (file in allApks) {
            SystemPrint.outPrintln(TAG, file.path)
        }
        // if (!apkDirectory.exists()) {
        // lazyApkFile.set(File())
        //  }
        //analyticsService
    }

    /**
     * 根据配置的内容来设置inputs内容,必须在添加到project的时候进行调用初始化input/output
     *
     * 1.配置.apk存放的路径.
     * 默认的取["${project.projectDir.absolutePath}/build/outputs/apk/"]
     * 2.配置解压之后的apk存放的路径.
     * 默认取["${project.projectDir.absolutePath}/build/protect/]
     */
    open fun setConfigFromExtensionAfterEvaluate() {
        project.afterEvaluate {
            setConfigFromExtension()
        }
    }

    private fun setConfigFromExtension() {
        //设置默认值
        setDefaultConfig()
        val extension = project.extensions.findByType(ApkProtectExtension::class.javaObjectType)
        if (extension == null) {
            setDefaultConfig()
            return
        }
        if (extension.lazyApkDirectory.orNull != null) {
            lazyApkDirectory.set(extension.lazyApkDirectory.get().asFile)
        }
        if (extension.unzipDirectory.orNull != null) {
            unzipDirectory.set(extension.unzipDirectory.get().asFile)
        }
    }

    /**
     * 获取[ //build/outputs/apk/huawei/debug]目录下所有变体下的apk
     */
    private fun getAllApksFromApkDirectory(apkDirectory: File) {
        //  variantName
        if (!apkDirectory.exists() || apkDirectory.isFile) {
            throw IllegalArgumentException("The apk directory is not exist !")
        }
        val files = apkDirectory.listFiles()
        if (files == null || files.isEmpty()) {
            return
        }
        for (file in files) {
            if (file.isDirectory) {
                getAllApksFromApkDirectory(file)
            } else if (file.isFile && file.name.endsWith(".apk")) {
                allApks.add(file)
            }
        }
    }

    /**
     * 设置默认值
     */
    private fun setDefaultConfig() {
        lazyApkDirectory.set(getApkDefaultDirectory())
        unzipDirectory.set(getUnzipDirectory())
    }

    /**
     * 默认的.apk存放的路径.
     */
    private fun getApkDefaultDirectory(): File {
        val defaultPath = getApkDefaultPath()
        val defaultDirectory = project.file(defaultPath)
        if (!defaultDirectory.exists()) {
            defaultDirectory.mkdirs()
        }
        return defaultDirectory
    }

    /**
     * 获取android studio默认的debug/release的apk存放的路径
     */
    private fun getApkDefaultPath(): String {
        val rootPath = "${project.projectDir.absolutePath}/build/outputs/apk/"
        return if (getApkPathByVariantName().isEmpty()) {
            rootPath
        } else if (isDebugBuildType()) {
            "${rootPath}${getApkPathByVariantName()}/debug"
        } else if (isReleaseBuildType()) {
            "${rootPath}${getApkPathByVariantName()}/release"
        } else {
            rootPath
        }
        //return rootPath
    }

    /**
     * 从默认的debug/release的获取productFlavors
     */
    private fun getApkPathByVariantName(): String {
        var productFlavors = ""
        val regex = if (isDebugBuildType()) {
            "(\\w+)Debug"
        } else {
            "(\\w+)Release"
        }
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(variantName)
        if (matcher.find()) {
            //group(0)整个字符串;group(1)第一个括号内的内容;group(2)第二个括号内的内容
            productFlavors = matcher.group(1)
        }
        return productFlavors
    }

    /**
     * 是debug状态
     */
    private fun isDebugBuildType(): Boolean {
        return variantName.endsWith("Debug")
    }

    /**
     * 是release状态
     */
    private fun isReleaseBuildType(): Boolean {
        return variantName.endsWith("Release")
    }

    /**
     * 默认的解压之后的apk存放的路径.
     */
    private fun getUnzipDirectory(): File {
        val unzipPath = "${project.projectDir.absolutePath}/build/protect/"
        val unzipDirectory = project.file(unzipPath)
        //SystemPrint.outPrintln("default = " + unzipDirectory.path)
        if (unzipDirectory.exists()) {
            unzipDirectory.delete()
        } else {
            unzipDirectory.mkdirs()
        }
        return unzipDirectory
    }
}