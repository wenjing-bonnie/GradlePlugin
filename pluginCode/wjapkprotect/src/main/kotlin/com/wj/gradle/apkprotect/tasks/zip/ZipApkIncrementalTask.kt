package com.wj.gradle.apkprotect.tasks.zip

import com.android.build.gradle.internal.tasks.NewIncrementalTask
import com.wj.gradle.apkprotect.tasks.zip.parallel.ZipApkAction
import com.wj.gradle.apkprotect.tasks.zip.parallel.ZipApkWorkParameters
import com.wj.gradle.apkprotect.utils.AppProtectDirectoryUtils
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

/**
 * TODO 这里的备注还需要完善
 * 压缩.apk，消费Task，此时的inputs接收[ShellAar2DexIncrementalTask]的outputs
 */
abstract class ZipApkIncrementalTask : NewIncrementalTask() {
    companion object {
        const val TAG: String = "ZipApkIncrementalTask"
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
    @get:OutputDirectory
    @get:Incremental
    abstract val zipApkDirectory: DirectoryProperty

    override fun doTaskAction(inputChanges: InputChanges) {
        SystemPrint.outPrintln(TAG, "The zip begin ...")
        val workQueue = workerExecutor.noIsolation()
        val unzipDirectory = unzipRootDirectory.get().asFile
        SystemPrint.outPrintln(
            TAG,
            AppProtectDirectoryUtils.getDefaultApkOutput(project, variantName).listFiles()
                .toString()
        )

        SystemPrint.outPrintln(TAG, "path = " + unzipDirectory.absolutePath)
        val allApkDirectories = unzipDirectory.listFiles()
        for (apk in allApkDirectories) {
            if (!AppProtectDirectoryUtils.isValidApkUnzipDirectory(apk)) {
                continue
            }
            // SystemPrint.outPrintln(TAG, "apk directory is \n" + apk.path)
            workQueue.submit(ZipApkAction::class.javaObjectType) { params: ZipApkWorkParameters ->
                params.unzipApkDirectory.set(apk)
                params.zipApkDirectory.set(zipApkDirectory)
            }
        }
    }
}