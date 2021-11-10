package com.wj.gradle.seniorapplication.tasks.parallel

import com.android.build.gradle.internal.tasks.NewIncrementalTask
import com.wj.gradle.seniorapplication.tasks.parallel.gradle.CustomParallelAction
import com.wj.gradle.seniorapplication.tasks.parallel.gradle.CustomParallelParameters
import com.wj.gradle.seniorapplication.utils.SystemPrint
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

/**
 * Created by wenjing.liu on 2021/10/9 in J1.
 *
 * 后面通过尝试继承com.android.build.gradle.internal.plugins.BasePlugin
 * 看是不是可以获取到getAnalyticsService！！
 *
 *
 * // Cannot query the value of task ':app:LazyConfigurationTask' property 'analyticsService' because it has no value available.
 * @get:Internal
 * abstract val analyticsService: Property<AnalyticsService>
 *
 * 可通过这种方式对这个属性值进行赋值
 * customIncremental.analyticsService.set(AnalyticsService.RegistrationAction(project).execute())
 * @author wenjing.liu
 */
abstract class CustomNewIncrementalTask : NewIncrementalTask() {
    companion object {
        const val TAG: String = "CustomNewIncrementalTask"
    }

    @get:SkipWhenEmpty
    @get:OutputFile
    @get:Incremental
    abstract val testLazyOutputFile: RegularFileProperty

    @get:InputFiles
    @get:SkipWhenEmpty
    @get:Incremental
    abstract val testInputFiles: ConfigurableFileCollection

    override fun doTaskAction(inputChanges: InputChanges) {
        SystemPrint.outPrintln(TAG, "is running ")
        noIsolation()
    }

    private fun noIsolation() {
        val workQueue = workerExecutor.noIsolation()
        testInputFiles.asFileTree.files.forEach {
            workQueue.submit(CustomParallelAction::class.javaObjectType) { params: CustomParallelParameters ->
                params.testInputFile.set(it)
                params.testLazyOutputFile.set(testLazyOutputFile.map { it })
            }
        }
    }
}