package com.wj.gradle.apkprotect.tasks.unzip

import com.android.build.gradle.internal.tasks.NewIncrementalTask
import com.wj.gradle.apkprotect.extensions.ApkProtectExtension
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File
import java.lang.IllegalArgumentException

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

    //必须在实例化该Task通过set进行赋值
    @get:Incremental
    @get:InputFile
    abstract val lazyApkDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val unzipDirectory: DirectoryProperty

    override fun doTaskAction(inputChanges: InputChanges) {
        SystemPrint.outPrintln(TAG, "The unZip begin ...")
        val workqueue = workerExecutor.noIsolation()

       // val apkDirectory = lazyApkDirectory.get().asFileTree
       // getAllApks(apkDirectory)
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
       // setDefaultConfig()
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

    //build/outputs/apk/huawei/debug
    private fun getAllApks(apkDirectory: File): List<File> {
        val apkList = listOf<File>()
        //  variantName
        SystemPrint.outPrintln(apkDirectory.path + " \n " + apkDirectory.exists() + "\n" + apkDirectory.isDirectory)
        if (!apkDirectory.exists() || !apkDirectory.isDirectory) {
            throw IllegalArgumentException("The apk directory is invalid !")
        }
        val files = apkDirectory.listFiles()
        for (file in files) {
            SystemPrint.outPrintln(TAG, file.path)
        }
        return apkList
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
        val defaultPath = "${project.projectDir.absolutePath}/build/outputs/apk/huawei/debug/app-huawei-debug.apk"
        val defaultDirectory = project.file(defaultPath)
        //SystemPrint.outPrintln("default = " + defaultDirectory.path)
        if (!defaultDirectory.exists()) {
            defaultDirectory.mkdirs()
        }
        return defaultDirectory
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