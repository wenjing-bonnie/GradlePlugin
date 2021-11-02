package com.wj.gradle.seniorapplication.taskmanager

import com.wj.gradle.manifest.extensions.IncrementalExtension
import com.wj.gradle.manifest.extensions.SeniorLazyKotlinExtension
import com.wj.gradle.manifest.utils.SystemPrint
import com.wj.gradle.seniorapplication.tasks.lazy.LazyConsumerTask
import com.wj.gradle.seniorapplication.tasks.lazy.LazyProducerTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

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

        setProducerInputProperty(producerProvider.get())
        setConsumerInputProperty(consumerTaskProvider.get(), producerProvider)
        //消费Task已添加到任务依赖中
        preBuildProvider.get().dependsOn(consumerTaskProvider.get())
    }

    private fun setProducerInputProperty(producerTask: LazyProducerTask) {
        producerTask.testInputFile.set(getIncrementalExtension().inputFile())
        producerTask.testLazyOutputDirectory.set(getIncrementalExtension().inputFile())
    }

    private fun setConsumerInputProperty(
        consumerTask: LazyConsumerTask,
        producerTask: TaskProvider<LazyProducerTask>
    ) {
        //connect producer task output to consumer task input
        //do not need to add a task dependency to the consumer task
        consumerTask.testLazyInputDirectory.set(producerTask.flatMap { it.testLazyOutputDirectory })
        SystemPrint.outPrintln("134", consumerTask.testLazyInputDirectory.get().asFile.absolutePath)

    }

    private fun getIncrementalExtension(): IncrementalExtension {
        var extension =
            project.extensions.findByType(SeniorLazyKotlinExtension::class.javaObjectType)
        if (extension == null) {
            return IncrementalExtension()
        }
        return extension.incremental()
    }
}