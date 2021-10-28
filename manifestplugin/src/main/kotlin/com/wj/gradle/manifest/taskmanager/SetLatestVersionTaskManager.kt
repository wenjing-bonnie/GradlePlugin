package com.wj.gradle.manifest.taskmanager

import com.android.build.gradle.tasks.ProcessMultiApkApplicationManifest
import com.wj.gradle.manifest.extensions.ManifestKotlinExtension
import com.wj.gradle.manifest.tasks.manifest.SetLatestVersionForMergedManifestTask
import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import java.io.File

/**
 * Created by wenjing.liu on 2021/10/19 in J1.
 * 将[SetLatestVersionForMergedManifestTask]添加到任务队列中
 *
 * @author wenjing.liu
 */
open class SetLatestVersionTaskManager(var project: Project, var variantName: String) {

    /**
     * 将[SetLatestVersionForMergedManifestTask]添加到任务队列中
     */
    open fun addSetLatestVersionForMergedManifestAfterEvaluate() {
//        val multiApkApplicationManifest =
//            project.tasks.getByName("process${variantName}Manifest")
        val multiApkProvider = project.tasks.named(
            "process${variantName}Manifest",
            ProcessMultiApkApplicationManifest::class.javaObjectType
        )
        val multiApkApplicationManifest = multiApkProvider.get()
        //SystemPrint.outPrintln(multiApkProvider.get())
        if (multiApkApplicationManifest !is ProcessMultiApkApplicationManifest) {
            return
        }
        var versionTask = project.tasks.create(
            SetLatestVersionForMergedManifestTask.TAG,
            SetLatestVersionForMergedManifestTask::class.javaObjectType
        )
        var inputFile = getVersionManagerFromExtension()
        //仅input存在的时候,才设置input,否则会抛出异常
        // File '/Users/j1/Documents/android/code/GradlePlugin/app' specified for property '$1' is not a file.
        if (inputFile.exists()) {
            versionTask.inputs.file(inputFile)
        }
        //TODO 猜测：这种方式不起作用，是因为没有将该Task的outputs关联到下一个Task的inputs中
        // 而在Android gradle中的tasks已经存在了每个Task的outputs和inputs之间的依赖关系,
        // 只是通过下面的方式，并没有将该Task关联到所有的Task中
        // versionTask.mergedManifestDirectory.set(multiApkProvider.flatMap { it.multiApkManifestOutputDirectory })
        versionTask.outputs.file(multiApkApplicationManifest.mainMergedManifest.asFile.get())
        multiApkApplicationManifest.finalizedBy(versionTask)
    }

    /**
     * 从extension中获取version 管理文件
     */
    private fun getVersionManagerFromExtension(): File {
        var extension = project.extensions.findByType(ManifestKotlinExtension::class.javaObjectType)
        if (extension == null) {
            return File("")
        }
        return extension.versionManager()
    }
}