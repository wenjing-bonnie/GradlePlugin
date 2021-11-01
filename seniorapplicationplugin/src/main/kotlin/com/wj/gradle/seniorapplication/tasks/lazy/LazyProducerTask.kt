package com.wj.gradle.seniorapplication.tasks.lazy

import com.wj.gradle.seniorapplication.tasks.BaseTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.work.InputChanges

/**
 * Created by wenjing.liu on 2021/11/1 in J1.
 * 生产Task
 * @author wenjing.liu
 */
abstract class LazyProducerTask : BaseTask() {

    companion object {
        const val TAG = "LazyProducerTask"
    }

    @get:SkipWhenEmpty
    @get:OutputDirectory
    abstract val testLazyOutputDirectory: DirectoryProperty

    @get:InputFile
    @get:SkipWhenEmpty
    abstract val testInput: RegularFileProperty

    override fun funIncrementalTaskAction(inputChanges: InputChanges) {
    }
}