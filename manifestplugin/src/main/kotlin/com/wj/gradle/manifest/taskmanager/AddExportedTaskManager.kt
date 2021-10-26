package com.wj.gradle.manifest.taskmanager

import com.android.build.gradle.tasks.ProcessApplicationManifest
import com.wj.gradle.manifest.tasks.manifest.AddExportForPackageManifestTask
import org.gradle.api.Project

/**
 * Created by wenjing.liu on 2021/10/19 in J1.
 *
 * @author wenjing.liu
 */
open class AddExportedTaskManager(var project: Project, var variantName: String) {

    open fun addExportForPackageManifestAfterEvaluate() {

        val processManifestTask =
            project.tasks.getByName("process${variantName}MainManifest")
        if (processManifestTask !is ProcessApplicationManifest) {
            return
        }
        //创建自定义Task
        var exportTask = project.tasks.register(
            AddExportForPackageManifestTask.TAG,
            AddExportForPackageManifestTask::class.javaObjectType,
        ).get()
        exportTask.setInputMainManifest(processManifestTask.mainManifest.get())
        exportTask.setInputManifests(processManifestTask.getManifests())
        processManifestTask.dependsOn(exportTask)
    }

}