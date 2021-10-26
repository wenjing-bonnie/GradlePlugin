package com.wj.gradle.manifest.taskmanager

import com.wj.gradle.manifest.tasks.others.LazyConfigurationTask
import org.gradle.api.Project

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
    }
}