package com.wj.gradle.manifest.task

import com.android.build.gradle.internal.tasks.NonIncrementalTask
import com.wj.gradle.manifest.utils.SystemPrint

/**
 * Created by wenjing.liu on 2021/10/8 in J1.
 * 在NewIncrementalTask的action中增加判断:如果inputChanges并不是增量编译,会删掉所有的输出文件,然后执行doTaskAction
 * NonIncrementalTask为抽象类,需要复写里面的抽象方法和抽象属性
 * @author wenjing.liu
 */
abstract class CustomNonIncrementalTask : NonIncrementalTask() {
    companion object {
        const val TAG: String = "CustomNonIncremental"
    }

    /**
     *
     * AnalyticsService records execution spans of tasks and workers. At the end of the build,
     * it combines data from execution and configuration, and writes build proto to disk.
     * 记录tasks和workers的执行范围.在编译结束,结合执行和配置的数据,并将构建协议写入磁盘
     */
//    override val analyticsService: Property<AnalyticsService>
//        get() {
//            return  null
//        }

    /**
     * WorkerExecutor用于异步执行task任务,可以安全并发的执行Task
     * Allows work to be submitted for asynchronous execution. This api allows for safe, concurrent execution of work items and enables:
     * https://docs.gradle.org/current/javadoc/org/gradle/workers/WorkerExecutor.html
     */
    //get()=...和get(){...}是一样的.如果只有一行代码可以直接用get()=...
//    override val workerExecutor: WorkerExecutor
//        get() =

    override fun doTaskAction() {
        SystemPrint.outPrintln(TAG, "is running")
    }


}