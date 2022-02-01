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
    /**该即将加入的Task的Tag*/
    val tag: String,
    /**该锚点TaskTask的名字*/
    val dependsOnTaskName: String,
    /**在锚点Task之前还是之后执行Task*/
    val isDependsOn: Boolean,
    /**回调监听*/
    val taskRegisterListener: IWillRunTaskRegisteredListener?
) {

    object Builder {

        private lateinit var willRunTaskClass: Class<out Task>
        private lateinit var willRunTaskTag: String
        private var isDependsOn: Boolean = true
        private lateinit var dependsOnTaskName: String
        private var taskRegisterListener: IWillRunTaskRegisteredListener? = null

        /**
         * 执行将要执行的Task的类名
         * open fun <T : Task> setWillRunTaskClass(name: Class<T>): Builder {
         */
        open fun <T : Task> setWillRunTaskClass(name: Class<T>): Builder {
            this.willRunTaskClass = name
            return this
        }

        /**
         * 设置瞄点task名字
         *
         * @param isDependsOn:true:在该Task之前执行目标Task,通过dependsOn添加targetTask
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
        open fun setWillRunTaskTag(tag: String): Builder {
            this.willRunTaskTag = tag
            return this
        }

        /**
         * 设置监听回调
         */
        open fun setWillRunTaskRegisterListener(listener: IWillRunTaskRegisteredListener): Builder {
            this.taskRegisterListener = listener
            return this
        }

        /**
         * 设置在锚点Task之前还是之后执行Task
         * @param isDependsOn true:在锚点Task之前去执行该Task
         */
        open fun setIsDependsOn(isDependsOn: Boolean): Builder {
            this.isDependsOn = isDependsOn
            return this
        }


        open fun builder(): TaskWrapper {
            checkArgument()
            val wrapper = TaskWrapper(
                willRunTaskClass,
                willRunTaskTag,
                dependsOnTaskName,
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
            if (dependsOnTaskName.isEmpty()) {
                throw IllegalArgumentException("Must set anchor task for will run task")
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
         * */
        fun willRunTaskRegistered(provider: TaskProvider<Task>)
    }

}