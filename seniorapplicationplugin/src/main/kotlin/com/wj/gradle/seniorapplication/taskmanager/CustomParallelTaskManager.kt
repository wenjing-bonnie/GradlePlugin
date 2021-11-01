package com.wj.gradle.manifest.taskmanager;

import com.wj.gradle.manifest.tasks.parallel.CustomIncrementalTask
import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Created by wenjing.liu on 2021/10/29 in J1.
 *
 * 验证并行Task
 * @author wenjing.liu
 */
open class CustomParallelTaskManager(var project: Project, var variantName: String) {

    private fun testCustomIncrementalTask(preBuild: Task) {
        var customIncremental = project.tasks.create(
            CustomIncrementalTask.TAG,
            CustomIncrementalTask::class.javaObjectType
        )
        customIncremental.variantName = variantName
        SystemPrint.outPrintln("" + customIncremental.analyticsService.isPresent)
        preBuild.dependsOn(customIncremental)
    }
}
