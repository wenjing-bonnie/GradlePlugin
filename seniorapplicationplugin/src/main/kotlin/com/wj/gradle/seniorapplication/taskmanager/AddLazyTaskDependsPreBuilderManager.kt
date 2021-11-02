package com.wj.gradle.manifest.taskmanager

import com.wj.gradle.seniorapplication.tasks.lazy.LazyConfigurationTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import java.io.File

/**
 * Created by wenjing.liu on 2021/10/26 in J1.
 *
 * @author wenjing.liu
 */
open class AddLazyTaskDependsPreBuilderManager(var project: Project, var variantName: String) {

    open fun testAddLazyTaskDependsPreBuilder(): TaskProvider<LazyConfigurationTask> {
        val lazyTaskProvider = project.tasks.register(
            LazyConfigurationTask.TAG,
            LazyConfigurationTask::class.javaObjectType
        )
        val lazyTask = lazyTaskProvider.get()

        setGenericProperty(lazyTask)
        //特殊的Property
        setFileProperty(lazyTask)
        setCollectionProperty(lazyTask)
        setMapsProperty(lazyTask)

        //通过inputs和outputs代替这种方式，直接使用Provider.flatMap{}方式添加Task依赖
        var preBuild = project.tasks.getByName("preBuild")
        preBuild.dependsOn(lazyTask)
        return lazyTaskProvider

    }

    /**
     * 通过泛型
     * 在Groovy中还可以使用=进行赋值，但在Kotlin中只能使用set()
     */
    private fun setGenericProperty(lazyTask: LazyConfigurationTask) {
        lazyTask.testProperty.set("Hi")
        lazyTask.testObjectFactory.set("Object Factory new instances")
    }

    /**
     * File类型的
     */
    private fun setFileProperty(lazyTask: LazyConfigurationTask) {
        var inputsDirectory = File("${project.projectDir.absoluteFile}/inputs")
        lazyTask.testDirectoryProperty.set(inputsDirectory)

        var provider = project.layout.buildDirectory.file("outputs/apk/debug/app-debug.apk")
        lazyTask.testRegularFileProperty.set(provider)

        lazyTask.testFileCollection.from(inputsDirectory)
    }

    /**
     * 集合类型
     */
    private fun setCollectionProperty(lazyTask: LazyConfigurationTask) {

        lazyTask.testListProperty.add("property 1")
        lazyTask.testListProperty.add("property 2")

        lazyTask.testSetProperty.add("property 1")
        lazyTask.testSetProperty.add("property 2")

    }

    /**
     * maps类型
     */
    private fun setMapsProperty(lazyTask: LazyConfigurationTask) {
        lazyTask.testMapsProperty.put("key1", 1)
        lazyTask.testMapsProperty.put("key2", project.providers.provider { 2 })
    }

}