package com.wj.gradle.seniorapplication.tasks.parallel

import com.android.build.gradle.internal.tasks.NonIncrementalTask
import com.wj.gradle.seniorapplication.tasks.parallel.gradle.CustomParallelParameters
import com.wj.gradle.seniorapplication.tasks.parallel.gradle.GenerateMd5Action
import com.wj.gradle.seniorapplication.utils.SystemPrint
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.work.Incremental
import org.gradle.workers.WorkQueue

/**
 * Created by wenjing.liu on 2021/11/8 in J1.
 *
 * 用来说明WorkerExecutor.classLoaderIsolation()
 * @author wenjing.liu
 */
abstract class ClassLoaderIsolationTask : NonIncrementalTask() {
    companion object {
        const val TAG: String = "ClassLoaderIsolationTask"
    }

    @get:SkipWhenEmpty
    @get:OutputFile
    @get:Incremental
    abstract val testLazyOutputFile: RegularFileProperty

    @get:InputFiles
    @get:SkipWhenEmpty
    @get:Incremental
    abstract val testInputFiles: ConfigurableFileCollection

    @get:InputFiles
    /**
     * 配置的Apache Common Codec库
     */
    abstract val configCodecClasspath: ConfigurableFileCollection

    override fun doTaskAction() {
        SystemPrint.outPrintln(TAG, "is running")
        // TODO 根据条件选择打开合适的方法
        //val workQueue = classLoaderIsolation()
        val workQueue = processIsolation()
        testInputFiles.asFileTree.files.forEach {
            workQueue.submit(GenerateMd5Action::class.javaObjectType) { param: CustomParallelParameters ->
                param.testInputFile.set(it)
                param.testLazyOutputFile.set(testLazyOutputFile.get())

            }
        }
    }

    private fun classLoaderIsolation(): WorkQueue {
        return workerExecutor.classLoaderIsolation() {
            it.classpath.from(configCodecClasspath)
        }
    }

    private fun processIsolation(): WorkQueue {
        return workerExecutor.processIsolation() {
            it.classpath.from(configCodecClasspath)
            it.forkOptions { fork ->
                fork.maxHeapSize = "64m"
                //fork.systemProperty("org.gradle.sample.showFileSize", true)
            }
        }
    }


}