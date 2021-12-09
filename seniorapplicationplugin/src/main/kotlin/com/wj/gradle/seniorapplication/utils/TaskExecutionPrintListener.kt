package com.wj.gradle.seniorapplication.utils

import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState

/**
 * Created by wenjing.liu on 2021/12/9 in J1.
 *
 * 统计每个Task执行时间
 *
 * @author wenjing.liu
 */
open class TaskExecutionPrintListener : TaskExecutionListener {

    private val taskInfo = TaskExecutionTimeInfo()
    override fun beforeExecute(task: Task) {
        taskInfo.startTime = System.currentTimeMillis()
        taskInfo.path = task.path
    }

    override fun afterExecute(task: Task, state: TaskState) {
        taskInfo.endTime = System.currentTimeMillis()
        if (isTaskPrintln(taskInfo.path)) {
            SystemPrint.outPrintln(taskInfo.toString())
        }
    }

    private fun isTaskPrintln(path: String): Boolean {
        return path.contains("transformClassesWithAutoLogTask")
    }

    class TaskExecutionTimeInfo {
        var startTime = 0L
        var endTime = 0L
        var path = ""
        override fun toString(): String {
            return " ~~~ ~~~ '${path}'  execution time is ${endTime - startTime} ms"
        }
    }


}