package com.wj.gradle.manifest.taskmanager

import com.android.build.gradle.internal.profile.AnalyticsService
import com.android.build.gradle.tasks.ProcessApplicationManifest
import com.wj.gradle.manifest.tasks.parallel.AddExportForPkgManifestParallelTask
import org.gradle.api.Project

/**
 * Created by wenjing.liu on 2021/11/10 in J1.
 *
 * 添加并发增量编译的"适配Android12的exported属性"的Task
 * @author wenjing.liu
 */
open class AddExportForPkgManifestParallelTaskManager(
    private val project: Project,
    private val variantName: String
) {

    open fun addExportForPkgManifestParallelTask() {
        val processManifestTask =
            project.tasks.getByName("process${variantName}MainManifest")
        if (processManifestTask !is ProcessApplicationManifest) {
            return
        }
        //创建自定义Task
        var exportTask = project.tasks.register(
            AddExportForPkgManifestParallelTask.TAG,
            AddExportForPkgManifestParallelTask::class.javaObjectType,
        ).get()
        exportTask.variantName = variantName
        exportTask.analyticsService.set(AnalyticsService.RegistrationAction(project).execute())
        exportTask.inputManifestFiles.from(processManifestTask.getManifests())
        exportTask.inputMainManifestFile.set(processManifestTask.mainManifest.get())
        processManifestTask.dependsOn(exportTask)
    }
}