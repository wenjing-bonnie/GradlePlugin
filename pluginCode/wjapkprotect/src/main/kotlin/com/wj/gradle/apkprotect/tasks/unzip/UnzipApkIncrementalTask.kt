package com.wj.gradle.apkprotect.tasks.unzip

import com.android.build.gradle.internal.tasks.NewIncrementalTask
import com.wj.gradle.apkprotect.extensions.ApkProtectExtension
import com.wj.gradle.apkprotect.tasks.unzip.parallel.UnzipApkAction
import com.wj.gradle.apkprotect.tasks.unzip.parallel.UnzipApkWorkParameters
import com.wj.gradle.apkprotect.utils.ZipAndUnzipApkDefaultPath.getApkDefaultDirectory
import com.wj.gradle.apkprotect.utils.ZipAndUnzipApkDefaultPath.getUnzipRootDirectory
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
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

    //必须在实例化该Task通过set进行赋值,否则会抛出异常
    //No value has been specified for property 'zipApkDirectory'.
    @get:Incremental
    @get:InputDirectory
    abstract val lazyApkDirectory: DirectoryProperty

    /**
     * 解压之后的文件存放的上级目录,以apk的名字存放解压之后的文件
     */
    @get:OutputDirectory
    abstract val unzipDirectory: DirectoryProperty

    /**
     * 存放所有的apk文件
     */
    private val allApks = mutableListOf<File>()

    /**
     *
     */
    override fun doTaskAction(inputChanges: InputChanges) {
        SystemPrint.outPrintln(TAG, "The unZip begin ...")
        val workqueue = workerExecutor.noIsolation()
        allApks.clear()
        //1.delete unzip directory
        deleteAndReMkdirsUnzipDirectory()
        //2.find all apk files in lazyApkDirectory
        val apkDirectory = lazyApkDirectory.get().asFile
        getAllApksFromApkDirectory(apkDirectory)
        //3.unzip .apk to unzipDirectory
        for (file in allApks) {
            //SystemPrint.outPrintln(TAG, "The apks path is \n" + file.path)
            workqueue.submit(UnzipApkAction::class.javaObjectType) { params: UnzipApkWorkParameters ->
                params.unzipApk.set(file)
                params.unzipDirectory.set(unzipDirectory.get())
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
        if (extension.lazyApkDirectory.orNull != null) {
            lazyApkDirectory.set(extension.lazyApkDirectory.get().asFile)
        }
        if (extension.unzipDirectory.orNull != null) {
            unzipDirectory.set(extension.unzipDirectory.get().asFile)
        }
    }

    /**
     * 设置默认值
     */
    private fun setDefaultConfig() {
        lazyApkDirectory.set(getApkDefaultDirectory(project,variantName))
        unzipDirectory.set(getUnzipRootDirectory(project))
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

    private fun deleteAndReMkdirsUnzipDirectory(){
        val unzipDirectory = unzipDirectory.get().asFile
        if (unzipDirectory.exists()){
            unzipDirectory.delete()
        }
        unzipDirectory.mkdirs()
    }
}