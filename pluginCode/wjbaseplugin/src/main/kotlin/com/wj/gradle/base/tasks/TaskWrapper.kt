package com.wj.gradle.base.tasks

import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import java.lang.IllegalArgumentException

/**
 * Created by wenjing.liu on 2022/1/19 in J1.
 *
 * 每个Task封装成该类，用来添加到project中
 *
 * @author wenjing.liu
 */
open class TaskWrapper private constructor(
    /**获取即将加入的Task的类名,生产-消费Task中的消费Task,最终添加到项目依赖*/
    val willRunTaskClass: Class<out Task>,
    /**生产Task，若有则赋值*/
    val producerTaskClass: Class<out Task>?,
    /**该即将加入的Task的Tag*/
    val tag: String,
    /**生产Task的Tag，若有则赋值*/
    val producerTag: String?,
    /**该锚点TaskTask的名字*/
    val anchorTaskName: String,
    /**在锚点Task之前还是之后执行Task*/
    val isDependsOn: Boolean,
    /**回调监听*/
    val taskRegisterListener: IWillRunTaskRegisteredListener?
) {

    override fun toString(): String {
        return "will run task is '${willRunTaskClass.simpleName}' , tag is $tag ; \n" +
                "                     anchor task is '$anchorTaskName' run before the anchor is ${isDependsOn} ;\n" +
                "                     consumer task is '${producerTaskClass?.simpleName}' , tag is $producerTag ."
    }

    /**
     * 是生产-消费的Task
     */
    fun isConsumerProducerTask(): Boolean {
        return producerTaskClass != null && producerTag != null
    }

    /**
     * 使用内部类代替静态类，否则里面的变量不能每次重新赋值
     */
    class Builder {
        private lateinit var willRunTaskClass: Class<out Task>
        private var producerTaskClass: Class<out Task>? = null
        private lateinit var willRunTaskTag: String
        private var producerTaskTag: String? = null
        private var isDependsOn: Boolean = true
        private lateinit var anchorTaskName: String
        private var taskRegisterListener: IWillRunTaskRegisteredListener? = null

//
//        /**
//         * 因为是静态类，所以里面的变量要进行初始化的赋值
//         */
//        init {
//            producerTaskClass = null
//            producerTaskTag = null
//            taskRegisterListener = null
//            isDependsOn = true
//        }

        /**
         *
         * 设置执行将要执行的Task的类名
         * open fun <T : Task> setWillRunTaskClass(name: Class<T>): Builder {
         */
        fun <T : Task> setWillRunTaskClass(name: Class<T>): Builder {
            this.willRunTaskClass = name
            return this
        }

        /**
         * 设置要执行的Task（生产Task）及消费Task
         * @param consumerTask  要执行的Task,也是生产-消费Task中的消费Task,最终添加到项目依赖
         * @param producerTask 生产Task，生产Task为消费Task提供inputs,通过flatMap{]添加到项目依赖中
         */
        fun setWillRunTaskClass(
            consumerTask: Class<out Task>,
            producerTask: Class<out Task>
        ): Builder {
            this.willRunTaskClass = consumerTask
            this.producerTaskClass = producerTask
            return this

        }

        /**
         * 设置瞄点task名字
         */
        fun setAnchorTaskName(name: String): Builder {
            this.anchorTaskName = name
            return this
        }

        /**
         * 该Task的Tag
         */
        @Deprecated("auto use the task class name instead of this set method")
        fun setWillRunTaskTag(tag: String): Builder {
            this.willRunTaskTag = tag
            return this
        }

        /**
         * 该Task的Tag
         * @param tag 要执行的Task的tag，也是生产-消费Task中的消费Task,最终添加到项目依赖
         * @param producerTag 消费Task的tag
         */
        fun setWillRunTaskTag(tag: String, producerTag: String): Builder {
            this.willRunTaskTag = tag
            this.producerTaskTag = producerTag
            return this
        }

        /**
         * 设置监听回调
         * TODO 暂时没有想到怎么将传入的类作为返回，目前需要进行强制转化一下！！！ 2022/02/05
         */
        fun setWillRunTaskRegisterListener(listener: IWillRunTaskRegisteredListener): Builder {
            this.taskRegisterListener = listener
            return this
        }

        /**
         * 设置在锚点Task之前还是之后执行Task
         * @param isDependsOn true:在锚点Task之前去执行该Task
         */
        fun setIsDependsOn(isDependsOn: Boolean): Builder {
            this.isDependsOn = isDependsOn
            return this
        }

        fun builder(): TaskWrapper {
            checkArgument()
            val wrapper = TaskWrapper(
                willRunTaskClass,
                producerTaskClass,
                willRunTaskTag,
                producerTaskTag,
                anchorTaskName,
                isDependsOn,
                taskRegisterListener
            )
            return wrapper
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
    interface IWillRunTaskRegisteredListener {
        /**
         * 将注册到Project的provider返回
         * @param provider 可通过provider.get()得到Task
         * @param producerProvider 若有消费Task，则返回该消费Task,若没有此时返回的为null
         * */
        fun willRunTaskRegistered(
            provider: TaskProvider<Task>,
            producerProvider: TaskProvider<Task>?
        )
    }
}