package com.wj.gradle.manifest.tasks

import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

/**
 * Created by wenjing.liu on 2021/10/21 in J1.
 *
 * 测试增量编译
 * https://docs.gradle.org/current/userguide/custom_tasks.html#sec:implementing_an_incremental_task
 * @author wenjing.liu
 */
open abstract class IncrementalOnDefaultTask : DefaultTask() {

    companion object {
        const val TAG = "IncrementalOnDefaultTask"
    }

    init {
        outputs.upToDateWhen {
            true
        }
        SystemPrint.errorPrintln(TAG, "init")
    }

    @get:Incremental
    @get:InputFiles
    //@get:PathSensitive(PathSensitivity.ABSOLUTE)
    abstract val testInputFiles: ConfigurableFileCollection

    @get:OutputDirectory
    @get:Incremental
    abstract val testOutputDir: DirectoryProperty

    @get:Incremental
    @get:InputFile
    abstract val testInputFile: RegularFileProperty


    @TaskAction
    open fun runTaskAction(inputChanges: InputChanges) {
        SystemPrint.outPrintln(TAG, "running isIncremental ..." + inputChanges.isIncremental)

        inputChanges.getFileChanges(testInputFiles).forEach {
            SystemPrint.outPrintln(TAG, "change type = " + it.changeType)
        }
    }

}