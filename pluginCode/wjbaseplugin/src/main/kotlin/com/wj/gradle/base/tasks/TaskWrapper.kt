package com.wj.gradle.base.tasks

import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * Created by wenjing.liu on 2022/1/19 in J1.
 *
 * 每个Task封装成该类，用来添加到project中
 *
 * @author wenjing.liu
 */
open class TaskWrapper private constructor(
    /**获取即将加入的Task的类名*/
    val willRunTaskClass: Class<Task>,
    /**该即将加入的Task的Tag*/
    val tag: String,
    /**该锚点TaskTask的名字*/
    val dependsOnTaskName: String,
    /**在锚点Task之前还是之后执行Task*/
    val isDependsOn: Boolean,
    /**回调监听*/
    val taskRegisterListener: IWillRunTaskRegisteredListener?
) {

    open class Builder {

        private lateinit var willRunTaskClass: Class<Task>
        private var tag: String = SystemPrint.TAG
        private var isDependsOn: Boolean = true
        private lateinit var dependsOnTaskName: String
        private var taskRegisterListener: IWillRunTaskRegisteredListener? = null


        /**
         * 执行将要执行的Task的类名
         */
        open fun setWillRunTaskClass(name: Class<Task>): Builder {
            this.willRunTaskClass = name
            return this
        }

        /**
         * 设置瞄点task名字
         * @beforeAnchor:true:在该Task之前执行目标Task,通过dependsOn添加targetTask
         * false:在Task之后执行目标Task,通过finalizedBy执行Task
         *
         */
        open fun setAnchorTaskName(name: String, isDependsOn: Boolean): Builder {
            this.dependsOnTaskName = name
            this.isDependsOn = isDependsOn
            return this
        }

        /**
         * 设置瞄点task名字
         * @beforeAnchor:true:在该Task之前执行目标Task,通过dependsOn添加targetTask
         * false:在Task之后执行目标Task,通过finalizedBy执行Task
         *
         */
        open fun setAnchorTaskName(name: String): Builder {
            return this.setAnchorTaskName(name, true)
        }

        /**
         * 该Task的Tag
         */
        open fun setTag(tag: String): Builder {
            this.tag = tag
            return this
        }

        /**
         * 设置监听回调
         */
        open fun setTaskRegisterListener(listener: IWillRunTaskRegisteredListener): Builder {
            this.taskRegisterListener = listener
            return this
        }


        open fun builder(): TaskWrapper {
            val wrapper = TaskWrapper(
                willRunTaskClass,
                tag,
                dependsOnTaskName,
                isDependsOn,
                taskRegisterListener
            )
            return wrapper
        }
    }


    /**
     * 监听在Project中register之后返回provider实例
     */
    interface IWillRunTaskRegisteredListener {
        /**
         * 将注册到Project的provider返回
         * */
        fun willRunTaskRegistered(provider: TaskProvider<Task>, task: Task)
    }

}