package com.wj.gradle.manifest.tasks

import com.android.build.gradle.internal.tasks.NewIncrementalTask
import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.work.InputChanges

/**
 * Created by wenjing.liu on 2021/10/9 in J1.
 *
 * @author wenjing.liu
 */
abstract class CustomIncrementalTask : NewIncrementalTask() {
    companion object {
        const val TAG: String = "CustomIncremental"
    }
    init {
        outputs.upToDateWhen { false }
    }


    override fun doTaskAction(inputChanges: InputChanges) {
        SystemPrint.outPrintln(TAG, "is running " + inputChanges)
    }

}