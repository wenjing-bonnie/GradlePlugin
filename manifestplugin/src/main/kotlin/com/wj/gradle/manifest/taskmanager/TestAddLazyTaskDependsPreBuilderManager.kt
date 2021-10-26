package com.wj.gradle.manifest.taskmanager

import com.wj.gradle.manifest.tasks.others.LazyConfigurationTask
import com.wj.gradle.manifest.tasks.parallel.CustomIncrementalTask
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Created by wenjing.liu on 2021/10/26 in J1.
 *
 * @author wenjing.liu
 */
open class TestAddLazyTaskDependsPreBuilderManager(var project: Project, var variantName: String) {

    open fun testAddLazyTaskDependsPreBuilder() {
        var lazyTask = project.tasks.create(
            LazyConfigurationTask.TAG,
            LazyConfigurationTask::class.javaObjectType
        )
        var preBuilder = project.tasks.findByName("preBuild")
        preBuilder?.dependsOn(lazyTask)

        //testCustomIncrementalTask(preBuilder)
    }

    private fun testCustomIncrementalTask(preBuild: Task?) {
        var customIncremental = project.tasks.create(
            CustomIncrementalTask.TAG,
            CustomIncrementalTask::class.javaObjectType
        )
        customIncremental.variantName = variantName
        preBuild?.dependsOn(customIncremental)
    }
}