package com.wj.gradle.manifest.taskmanager

import com.android.build.gradle.tasks.ProcessMultiApkApplicationManifest
import com.wj.gradle.manifest.extensions.ManifestKotlinExtension
import com.wj.gradle.manifest.tasks.manifest.SetLatestVersionForMergedManifestTask
import org.gradle.api.Project
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
        val multiApkApplicationManifest =
            project.tasks.getByName("process${variantName}Manifest")
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