package com.wj.gradle.seniorapplication.taskmanager;

import com.android.build.gradle.internal.profile.AnalyticsService
import com.wj.gradle.manifest.tasks.parallel.CustomNewIncrementalTask
import com.wj.gradle.seniorapplication.extensions.SeniorApplicationKotlinExtension
import com.wj.gradle.seniorapplication.tasks.parallel.gradle.CustomParallelTask
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Created by wenjing.liu on 2021/10/29 in J1.
 *
 * 验证并行Task
 * @author wenjing.liu
 */
open class AddCustomParallelTaskManager(var project: Project, var variantName: String) {
    /**
     * Gradle中的并行Task
     */
    open fun testCustomParallelTask() {
        val parallelTask =
            project.tasks.create(CustomParallelTask.TAG, CustomParallelTask::class.javaObjectType)
        var incrementalExtension =
            project.extensions.findByType(SeniorApplicationKotlinExtension::class.javaObjectType)
                ?.incremental()

        parallelTask.testInputFiles.from(incrementalExtension?.inputFiles())
        parallelTask.testLazyOutputFile.set(incrementalExtension?.outputFile())
        preBuildDependsOn(parallelTask)
    }

    /**
     * Android gradle中的并行Task
     */
    open fun testCustomNewIncrementalTask() {
        val newIncremental = project.tasks.create(
            CustomNewIncrementalTask.TAG,
            CustomNewIncrementalTask::class.javaObjectType
        )
        newIncremental.variantName = variantName
        newIncremental.analyticsService.set(
            AnalyticsService.RegistrationAction(project).execute()
        )
        preBuildDependsOn(newIncremental)
    }

    private fun preBuildDependsOn(task: Task) {
        val preBuild = project.tasks.getByName("preBuild")
        preBuild.dependsOn(task)
    }


}
