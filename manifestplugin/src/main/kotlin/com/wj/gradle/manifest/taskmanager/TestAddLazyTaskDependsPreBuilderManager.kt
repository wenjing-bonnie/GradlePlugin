package com.wj.gradle.manifest.taskmanager

import com.wj.gradle.manifest.tasks.others.LazyConfigurationTask
import com.wj.gradle.manifest.tasks.parallel.CustomIncrementalTask
import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.tasks.TaskProvider
import java.io.File

/**
 * Created by wenjing.liu on 2021/10/26 in J1.
 *
 * @author wenjing.liu
 */
open class TestAddLazyTaskDependsPreBuilderManager(var project: Project, var variantName: String) {

    open fun testAddLazyTaskDependsPreBuilder(): TaskProvider<LazyConfigurationTask> {
        var lazyTaskProvider = project.tasks.register(
            LazyConfigurationTask.TAG,
            LazyConfigurationTask::class.javaObjectType
        )
        var lazyTask = lazyTaskProvider.get()

        //在Groovy中还可以使用=进行赋值，但在Kotlin中只能使用set()
        lazyTask.testProperty.set("Hi")
        lazyTask.testObjectFactory.set("Object Factory new instances")
        //特殊的Property
        lazyTask.testDirectoryProperty.set(project.buildDir)
        lazyTask.testRegularFilePropertyFromFactory.set(project.layout.buildDirectory.file("outputs/apk/debug/app-debug.apk"))

        //var preBuild = project.tasks.getByName("preBuild")
        //preBuild.dependsOn(lazyTask)
        return lazyTaskProvider

    }

    private fun testCustomIncrementalTask(preBuild: Task) {
        var customIncremental = project.tasks.create(
            CustomIncrementalTask.TAG,
            CustomIncrementalTask::class.javaObjectType
        )
        customIncremental.variantName = variantName
        preBuild.dependsOn(customIncremental)
    }
}