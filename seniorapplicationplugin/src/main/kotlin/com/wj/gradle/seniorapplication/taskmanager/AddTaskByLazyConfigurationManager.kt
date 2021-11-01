package com.wj.gradle.seniorapplication.taskmanager

import com.wj.gradle.seniorapplication.tasks.lazy.LazyConsumerTask
import com.wj.gradle.seniorapplication.tasks.lazy.LazyProducerTask
import org.gradle.api.Project

/**
 * Created by wenjing.liu on 2021/11/1 in J1.
 *
 * @author wenjing.liu
 */
open class AddTaskByLazyConfigurationManager(var project: Project, var variantName: String) {

    open fun testAddTaskByLazyConfiguration() {
        val preBuildProvider = project.tasks.named("preBuild")

        val consumerTaskProvider =
            project.tasks.register(LazyConsumerTask.TAG, LazyConsumerTask::class.javaObjectType)
        val producerProvider =
            project.tasks.register(LazyProducerTask.TAG, LazyProducerTask::class.javaObjectType)

        preBuildProvider.get().dependsOn(producerProvider.get())

        consumerTaskProvider.get().testLazyInputDirectory.set(producerProvider.flatMap { it.testLazyOutputDirectory })
    }
}