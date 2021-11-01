package com.wj.gradle.seniorapplication.tasks.lazy

import com.wj.gradle.manifest.utils.SystemPrint
import com.wj.gradle.seniorapplication.tasks.BaseTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.work.InputChanges

/**
 * Created by wenjing.liu on 2021/11/1 in J1.
 * 消费Task
 *
 * @author wenjing.liu
 */
abstract class LazyConsumerTask : BaseTask() {

    companion object {
        const val TAG = "LazyConsumerTask"
    }

    @get:SkipWhenEmpty
    @get:InputDirectory
    abstract val testLazyInputDirectory: DirectoryProperty

    override fun funIncrementalTaskAction(inputChanges: InputChanges) {
        SystemPrint.outPrintln(
            TAG,
            "input directory is \n" + testLazyInputDirectory.get().asFile.absolutePath
        )
    }

}