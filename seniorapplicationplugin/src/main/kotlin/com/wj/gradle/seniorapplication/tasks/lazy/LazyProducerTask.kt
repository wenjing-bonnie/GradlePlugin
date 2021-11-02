package com.wj.gradle.seniorapplication.tasks.lazy

import com.wj.gradle.manifest.utils.SystemPrint
import com.wj.gradle.seniorapplication.tasks.BaseTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.Incremental
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
    @get:Incremental
    abstract val testLazyOutputDirectory: RegularFileProperty

    @get:InputFile
    @get:SkipWhenEmpty
    @get:Incremental
    abstract val testInputFile: RegularFileProperty

    //RegularFileProperty类型的必须通过.set()进行赋值，否则会抛出"> No value has been specified for property "
    //ConfigurableFileCollection可以不赋值
    @get:SkipWhenEmpty
    @get:Internal
    @get:Incremental
    abstract val testMustSetProperty: RegularFileProperty

    override fun incrementalTaskAction(inputChanges: InputChanges) {

        SystemPrint.outPrintln(TAG, "output \n" + testLazyOutputDirectory.get().asFile.absolutePath)
    }
}