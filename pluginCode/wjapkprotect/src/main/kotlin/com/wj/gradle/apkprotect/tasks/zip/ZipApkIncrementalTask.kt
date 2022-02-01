package com.wj.gradle.apkprotect.tasks.zip

import com.android.build.gradle.internal.tasks.NewIncrementalTask
import com.wj.gradle.apkprotect.extensions.ApkProtectExtension
import com.wj.gradle.apkprotect.tasks.zip.parallel.ZipApkAction
import com.wj.gradle.apkprotect.tasks.zip.parallel.ZipApkWorkParameters
import com.wj.gradle.apkprotect.utils.ZipAndUnzipApkDefaultPath
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.work.InputChanges

/**
 * 压缩.apk
 */
abstract class ZipApkIncrementalTask : NewIncrementalTask() {
    companion object {
        const val TAG: String = "ZipApkIncrementalTask"
    }

    /**
     * 存放所有的解压之后的文件夹
     */
    @get:InputDirectory
    abstract val unzipRootDirectory: DirectoryProperty

    /**
     * 存放输出的apk文件的文件夹
     */
    @get:OutputDirectory
    abstract val zipApkDirectory: DirectoryProperty

    override fun doTaskAction(inputChanges: InputChanges) {
        val workQueue = workerExecutor.noIsolation()
        val unzipDirectory = unzipRootDirectory.get().asFile
        val allApkDirectories = unzipDirectory.listFiles()
        for (apk in allApkDirectories) {
            SystemPrint.outPrintln(TAG, "apk directory is \n" + apk.path)
            workQueue.submit(ZipApkAction::class.javaObjectType) { params: ZipApkWorkParameters ->
                params.unzipApkDirectory.set(apk)
                params.zipApkDirectory.set(zipApkDirectory)
            }
        }
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
        if (extension.unzipDirectory.orNull != null) {
            unzipRootDirectory.set(extension.unzipDirectory.get().asFile)
            zipApkDirectory.set(extension.unzipDirectory.get().asFile)
        }
    }

    /**
     * 设置默认值
     */
    private fun setDefaultConfig() {
        unzipRootDirectory.set(ZipAndUnzipApkDefaultPath.getUnzipRootDirectory(project))
        zipApkDirectory.set(ZipAndUnzipApkDefaultPath.getUnzipRootDirectory(project))
    }

}