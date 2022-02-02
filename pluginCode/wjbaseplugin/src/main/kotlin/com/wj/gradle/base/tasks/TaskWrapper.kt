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
    /**获取即将加入的Task的类名*/
    val willRunTaskClass: Class<out Task>,
    /**消费Task，若有则赋值*/
    val consumerTaskClass: Class<out Task>?,
    /**该即将加入的Task的Tag*/
    val tag: String,
    /**消费Task的Tag，若有则赋值*/
    val consumerTag: String?,
    /**该锚点TaskTask的名字*/
    val anchorTaskName: String,
    /**在锚点Task之前还是之后执行Task*/
    val isDependsOn: Boolean,
    /**回调监听*/
    val taskRegisterListener: IWillRunTaskRegisteredListener?
) {

    override fun toString(): String {
        return "will run task is '${willRunTaskClass.simpleName}' , tag is $tag ; \n " +
                "anchor task is '$anchorTaskName' run before the anchor is ${isDependsOn} \n" +
                " consumer task is '${consumerTaskClass?.simpleName}' , tag is $consumerTag"
    }

    /**
     * 是生产-消费的Task
     */
    fun isConsumerTask(): Boolean {
        return consumerTaskClass != null && consumerTag != null
    }

    object Builder {

        private lateinit var willRunTaskClass: Class<out Task>
        private var consumerTaskClass: Class<out Task>? = null
        private lateinit var willRunTaskTag: String
        private var consumerTaskTag: String? = null
        private var isDependsOn: Boolean = true
        private lateinit var anchorTaskName: String
        private var taskRegisterListener: IWillRunTaskRegisteredListener? = null

        /**
         * 设置执行将要执行的Task的类名
         * open fun <T : Task> setWillRunTaskClass(name: Class<T>): Builder {
         */
        fun <T : Task> setWillRunTaskClass(name: Class<T>): Builder {
            this.willRunTaskClass = name
            return this
        }

        /**
         * 设置要执行的Task（生产Task）及消费Task
         * @param consumerTask  要执行的Task
         * @param producerTask 生产Task，该生产Task依赖于消费Task
         */
        fun setWillRunTaskClass(
            producerTask: Class<out Task>,
            consumerTask: Class<out Task>
        ): Builder {
            this.willRunTaskClass = producerTask
            this.consumerTaskClass = consumerTask
            return this

        }

        /**
         * 设置瞄点task名字
         *
         */
        fun setAnchorTaskName(name: String): Builder {
            this.anchorTaskName = name
            return this
        }

        /**
         * 该Task的Tag
         */
        fun setWillRunTaskTag(tag: String): Builder {
            this.willRunTaskTag = tag
            return this
        }

        /**
         * 该Task的Tag
         * @param tag 生产Task的tag
         * @param producerTag 消费Task的tag
         */
        fun setWillRunTaskTag(tag: String, producerTag: String): Builder {
            this.willRunTaskTag = tag
            this.consumerTaskTag = producerTag
            return this
        }

        /**
         * 设置监听回调
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
                consumerTaskClass,
                willRunTaskTag,
                consumerTaskTag,
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

            if ((consumerTaskClass != null && consumerTaskTag == null) || (consumerTaskClass == null && consumerTaskTag != null)) {
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

        /**
         * 再将Task添加到依赖锚点之前
         * @param provider 可通过provider.get()得到Task
         * @param producerProvider 若有消费Task，则返回该消费Task,若没有此时返回的为null
         */
        fun willRunTaskBeforeDependsOnAnchorTask(
            provider: TaskProvider<Task>,
            producerProvider: TaskProvider<Task>?
        )
    }

}