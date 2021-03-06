package com.wj.gradle.seniorapplication.taskmanager

import com.wj.gradle.seniorapplication.extensions.IncrementalExtension
import com.wj.gradle.seniorapplication.extensions.SeniorApplicationKotlinExtension
import com.wj.gradle.seniorapplication.tasks.lazy.LazyConsumerTask
import com.wj.gradle.seniorapplication.tasks.lazy.LazyProducerTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

/**
 * Created by wenjing.liu on 2021/11/1 in J1.
 * 通过懒加载配置添加producerTask
 * @author wenjing.liu
 */
open class AddProducerTaskManager(var project: Project, var variantName: String) {

    /**
     * 测试添加[LazyProducerTask]和[LazyConsumerTask]
     */
    open fun testAddTaskByLazyConfiguration() {

        val consumerTaskProvider =
            project.tasks.register(LazyConsumerTask.TAG, LazyConsumerTask::class.javaObjectType)
        val producerProvider =
            project.tasks.register(LazyProducerTask.TAG, LazyProducerTask::class.javaObjectType)

        setProducerProperty(producerProvider.get())
        setConsumerInputProperty(consumerTaskProvider.get(), producerProvider)

    }

    private fun setProducerProperty(producerTask: LazyProducerTask) {
        producerTask.testInputFiles.from(getIncrementalExtension().inputFiles())
        producerTask.testLazyOutputFile.set(getIncrementalExtension().outputFile())
    }

    private fun setConsumerInputProperty(
        consumerTask: LazyConsumerTask,
        producerTask: TaskProvider<LazyProducerTask>
    ) {
        //消费Task已经在依赖关系队列中，可以隐式自动添加prouducer,
        // 当Producer的outputs发生改变的时候，可自动更新到消费Task
        //connect producer task output to consumer task input
        //do not need to add a task dependency to the consumer task
        consumerTask.testLazyInputFile.set(producerTask.flatMap { it.testLazyOutputFile })
        //消费Task已添加到任务依赖中
        val preBuildProvider = project.tasks.named("preBuild")
        preBuildProvider.get().dependsOn(consumerTask)
    }

    private fun getIncrementalExtension(): IncrementalExtension {
        var extension =
            project.extensions.findByType(SeniorApplicationKotlinExtension::class.javaObjectType)
        if (extension == null) {
            return IncrementalExtension()
        }
        return extension.incremental()
    }
}