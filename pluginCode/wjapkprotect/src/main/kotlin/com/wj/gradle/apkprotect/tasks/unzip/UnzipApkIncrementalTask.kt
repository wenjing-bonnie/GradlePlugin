package com.wj.gradle.apkprotect.tasks.unzip

import com.android.build.gradle.internal.tasks.NewIncrementalTask
import com.wj.gradle.apkprotect.extensions.ApkProtectExtension
import com.wj.gradle.base.utils.SystemPrint
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

    @get:Incremental
    @get:InputFile
    abstract val lazyApkDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val unzipDirectory: DirectoryProperty

    override fun doTaskAction(inputChanges: InputChanges) {
        SystemPrint.outPrintln(TAG, "The unZip begin ...")
        setConfigFromExtension()
        val apkDirectory = lazyApkDirectory.get().asFile
        getAllApks(apkDirectory)
        if (!apkDirectory.exists()) {
            // lazyApkFile.set(File())
        }
    }

    /**
     * 根据配置的内容来设置inputs内容
     */
    private fun setConfigFromExtension() {
        val extension = project.extensions.findByType(ApkProtectExtension::class.javaObjectType)
            ?: return
        lazyApkDirectory.set(extension.lazyApkDirectory.get())
        unzipDirectory.set(extension.unzipDirectory.get())
    }

    //build/outputs/apk/huawei/debug
    private fun getAllApks(apkDirectory: File): List<File> {
        val apkList = listOf<File>()
        //  variantName
        if (!apkDirectory.exists() || !apkDirectory.isDirectory) {
            throw IllegalArgumentException("The apk directory is invalid !")
        }

        val files = apkDirectory.listFiles()
        for (file in files) {
            SystemPrint.outPrintln(TAG, file.path)
        }
        return apkList
    }

//    private fun getApkVariantNamePath():String{
//        //variantName
//        var path = "debug"
//        val pattern = Pattern.compile("(.*?)Debug")
//        return path
//    }


}