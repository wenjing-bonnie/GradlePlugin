package com.wj.gradle.apkprotect.tasks.zip

import com.android.build.gradle.internal.tasks.NewIncrementalTask
import com.wj.gradle.apkprotect.extensions.ApkProtectExtension
import com.wj.gradle.apkprotect.tasks.zip.parallel.ZipApkAction
import com.wj.gradle.apkprotect.tasks.zip.parallel.ZipApkWorkParameters
import com.wj.gradle.apkprotect.utils.ZipAndUnzipApkDefaultPath
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File

/**
 * 压缩.apk，消费Task，此时的inputs接收[UnzipApkIncrementalTask]的outputs
 */
abstract class ZipApkIncrementalTask : NewIncrementalTask() {
    companion object {
        const val TAG: String = "ZipApkIncrementalTask"
    }

    init {
        outputs.upToDateWhen {
            true
        }
    }

    /**
     * 存放所有的解压之后的文件夹
     */
    @get:InputDirectory
    @get:Incremental
    abstract val unzipRootDirectory: DirectoryProperty

    /**
     * 存放输出的apk文件的文件夹
     */
    private val zipApkDirectory: Provider<Directory> =
        unzipRootDirectory.flatMap { project.layout.buildDirectory.dir("${it.asFile.path}/apps/") }

    override fun doTaskAction(inputChanges: InputChanges) {
        SystemPrint.outPrintln(TAG,"The zip begin ...")
        val workQueue = workerExecutor.noIsolation()
        val unzipDirectory = unzipRootDirectory.get().asFile
        val allApkDirectories = unzipDirectory.listFiles()
        for (apk in allApkDirectories) {
           // SystemPrint.outPrintln(TAG, "apk directory is \n" + apk.path)
            workQueue.submit(ZipApkAction::class.javaObjectType) { params: ZipApkWorkParameters ->
                params.unzipApkDirectory.set(apk)
                params.zipApkDirectory.set(zipApkDirectory)
            }
        }
    }
}