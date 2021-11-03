package com.wj.gradle.seniorapplication.tasks.parallel.gradle

import com.wj.gradle.seniorapplication.tasks.BaseTask
import org.gradle.work.InputChanges

/**
 * Created by wenjing.liu on 2021/11/3 in J1.
 *
 * @author wenjing.liu
 */
abstract class CustomParallelTask : BaseTask() {

    override fun incrementalTaskAction(inputChanges: InputChanges) {
    }
}