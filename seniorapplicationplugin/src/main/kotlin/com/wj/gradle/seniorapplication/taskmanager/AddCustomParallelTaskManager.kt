package com.wj.gradle.seniorapplication.taskmanager;

import com.android.build.gradle.internal.profile.AnalyticsService
import com.wj.gradle.manifest.tasks.parallel.CustomNewIncrementalTask
import com.wj.gradle.seniorapplication.extensions.SeniorApplicationKotlinExtension
import com.wj.gradle.seniorapplication.tasks.parallel.gradle.CustomParallelTask
import com.wj.gradle.seniorapplication.tasks.parallel.ClassLoaderIsolationTask
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
     * 验证WorkerExecutor.classLoaderIsolation()
     */
    open fun testClassLoaderIsolationTask() {
        val classLoaderTask = project.tasks.create(
            ClassLoaderIsolationTask.TAG,
            ClassLoaderIsolationTask::class.javaObjectType
        )

        classLoaderTask.variantName = variantName
        val extension =
            project.extensions.findByType(SeniorApplicationKotlinExtension::class.javaObjectType)
        val incrementalExtension = extension?.incremental()

        classLoaderTask.configCodecClasspath.from(extension?.md5CodecClasspath)
        classLoaderTask.testInputFiles.from(incrementalExtension?.inputFiles())
        classLoaderTask.testLazyOutputFile.set(incrementalExtension?.outputFile())
        classLoaderTask.analyticsService.set(
            AnalyticsService.RegistrationAction(project).execute()
        )
        preBuildDependsOn(classLoaderTask)
    }

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
        var incrementalExtension =
            project.extensions.findByType(SeniorApplicationKotlinExtension::class.javaObjectType)
                ?.incremental()

        newIncremental.testInputFiles.from(incrementalExtension?.inputFiles())
        newIncremental.testLazyOutputFile.set(incrementalExtension?.outputFile())
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
