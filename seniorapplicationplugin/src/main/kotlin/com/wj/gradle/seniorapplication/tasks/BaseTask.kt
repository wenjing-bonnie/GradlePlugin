package com.wj.gradle.seniorapplication.tasks

import com.wj.gradle.manifest.utils.SystemPrint
import com.wj.gradle.seniorapplication.tasks.lazy.LazyProducerTask
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.work.InputChanges

/**
 * Created by wenjing.liu on 2021/11/1 in J1.
 *
 * @author wenjing.liu
 */
abstract class BaseTask : DefaultTask() {

    abstract fun funIncrementalTaskAction(inputChanges: InputChanges)

    @TaskAction
    open fun runTaskAction(inputChanges: InputChanges) {

        SystemPrint.outPrintln(javaClass.simpleName, "Lazy producer is running")
        funIncrementalTaskAction(inputChanges)
    }

}