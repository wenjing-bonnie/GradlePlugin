package com.wj.gradle.base.tasks

import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import java.lang.IllegalArgumentException

/**
 * Created by wenjing.liu on 2022/1/19 in J1.
 *
 * 每个Task封装成该类，用来添加到project中
 *
 * 因为要进行赋值操作，所以需要in
 * @author wenjing.liu
 */
open class TaskWrapperGeneric<IRunTask : Task>(
    /**获取即将加入的Task的类名,生产-消费Task中的消费Task,最终添加到项目依赖*/
    var willRunTaskClass: Class<IRunTask>,
    /**该即将加入的Task的Tag*/
    var willRunTaskTag: String,
    /**该锚点TaskTask的名字*/
    var anchorTaskName: String
) {

    init {
        //检测是不是控制
        checkArgument()
    }

    /**获取即将加入的Task的类名,生产-消费Task中的消费Task,最终添加到项目依赖*/
    // var willRunTaskClass: Class<IRunTask>,

    /**生产Task，若有则赋值*/
    var producerTaskClass: Class<out Task>? = null

    /**该即将加入的Task的Tag*/
    // var willRunTaskTag: String,

    /**生产Task的Tag，若有则赋值*/
    var producerTaskTag: String? = null

    /**该锚点TaskTask的名字*/
    // var anchorTaskName: String,

    /**在锚点Task之前还是之后执行Task*/
    var isDependsOn: Boolean = false

    /**回调监听*/
    var taskRegisterListener: IWillRunTaskRegisteredListener<IRunTask>? = null


    override fun toString(): String {
        return "will run task is '${willRunTaskClass.simpleName}' , tag is $willRunTaskTag ; \n" +
                "                     anchor task is '$anchorTaskName' run before the anchor is ${isDependsOn} ;\n" +
                "                     consumer task is '${producerTaskClass?.simpleName}' , tag is $producerTaskTag ."
    }

    /**
     * 是生产-消费的Task
     */
    fun isConsumerProducerTask(): Boolean {
        return producerTaskClass != null && producerTaskTag != null
    }

    //使用懒加载,应该不需要自己做检查了,kotlin会去检查是否赋值
    private fun checkArgument() {
        if (willRunTaskTag.isEmpty()) {
            throw  IllegalArgumentException("Must set tag is not empty for will run task")
        }
        if (anchorTaskName.isEmpty()) {
            throw IllegalArgumentException("Must set anchor task for will run task")
        }

        if ((producerTaskClass != null && producerTaskTag == null) || (producerTaskClass == null && producerTaskTag != null)) {
            throw IllegalAccessException("If you set producer task , must set the producer task 's tag  and class")
        }
    }
}

/**
 * 监听在Project中register之后返回provider实例
 */
interface IWillRunTaskRegisteredListener<IRunTask : Task> {
    /**
     * 将注册到Project的provider返回
     * @param provider 可通过provider.get()得到Task
     * @param producerProvider 若有消费Task，则返回该消费Task,若没有此时返回的为null
     * */
    fun willRunTaskRegistered(
        provider: TaskProvider<IRunTask>,
        producerProvider: TaskProvider<out Task>?
    )
}