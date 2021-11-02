package com.wj.gradle.seniorapplication.tasks.lazy

import com.wj.gradle.manifest.utils.SystemPrint
import com.wj.gradle.seniorapplication.tasks.BaseTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
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
    @get:OutputFile
    abstract val testLazyOutputDirectory: RegularFileProperty

    @get:InputFile
    @get:SkipWhenEmpty
    abstract val testInputFile: RegularFileProperty

    @get:SkipWhenEmpty
    @get:InputFiles
    abstract val testInputFileCollection: ConfigurableFileCollection

    override fun incrementalTaskAction(inputChanges: InputChanges) {

        SystemPrint.outPrintln(TAG, "output \n" + testLazyOutputDirectory.get().asFile.absolutePath)
    }
}