package com.wj.gradle.seniorapplication.tasks.parallel.gradle

import com.wj.gradle.seniorapplication.tasks.BaseTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

/**
 * Created by wenjing.liu on 2021/11/3 in J1.
 *
 * 并行Task
 *
 * @author wenjing.liu
 */
abstract class CustomParallelTask : BaseTask() {

    companion object {
        const val TAG = "CustomParallelTask"
    }

    @get:SkipWhenEmpty
    @get:OutputFile
    @get:Incremental
    abstract val testLazyOutputFile: RegularFileProperty

    @get:InputFiles
    @get:SkipWhenEmpty
    @get:Incremental
    abstract val testInputFiles: ConfigurableFileCollection

    @get:Inject
    abstract val workerExecutor: WorkerExecutor

    override fun incrementalTaskAction(inputChanges: InputChanges) {
        val workQueue = workerExecutor.noIsolation()

        workQueue.submit(
            CustomParallelAction::class.javaObjectType
        ) { param: CustomParallelParameters ->
            param.testInputFiles.from(testInputFiles.files)
            param.testLazyOutputFile.set(testLazyOutputFile.get())
        }
    }
}