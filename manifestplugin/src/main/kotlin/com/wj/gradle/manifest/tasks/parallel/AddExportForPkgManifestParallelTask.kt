package com.wj.gradle.manifest.tasks.parallel

import com.android.build.gradle.internal.tasks.NewIncrementalTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.work.InputChanges
import org.gradle.workers.WorkQueue
import java.io.File

/**
 * Created by wenjing.liu on 2021/11/10 in J1.
 *
 * 增量编译的并发task
 * 为所有的未适配Android 12 exported:true属性的组件添加
 * @author wenjing.liu
 */
abstract class AddExportForPkgManifestParallelTask : NewIncrementalTask() {

    companion object {
        const val TAG: String = "AddExportForPkgManifestParallelTask"
    }

    init {
        outputs.upToDateWhen { true }
    }

    @get:InputFiles
    abstract val inputManifestFiles: ConfigurableFileCollection

    @get:InputFile
    abstract val inputMainManifestFile: RegularFileProperty

    override fun doTaskAction(inputChanges: InputChanges) {
       // SystemPrint.outPrintln(TAG, "is running ...")

        val workQueue = workerExecutor.noIsolation()
        //处理第三方的manifest
        inputManifestFiles.asFileTree.files.forEach {
            workQueueSubmit(workQueue, it, false)
        }
        //处理app下的manifest
        workQueueSubmit(workQueue, inputMainManifestFile.get().asFile, false)
    }

    private fun workQueueSubmit(
        workQueue: WorkQueue,
        manifestFile: File,
        isOnlyBuildError: Boolean
    ) {
        workQueue.submit(AddExportWorkAction::class.javaObjectType) {
            it.inputManifestFile.set(manifestFile)
            it.isOnlyBuildError = isOnlyBuildError
        }
    }
}