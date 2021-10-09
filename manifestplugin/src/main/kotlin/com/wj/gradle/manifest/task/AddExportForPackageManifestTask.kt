package com.wj.gradle.manifest.task

import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by wenjing.liu on 2021/9/30 in J1.
 *
 * NonIncrementalTask
 * @author wenjing.liu
 */
open class AddExportForPackageManifestTask : DefaultTask() {
    companion object {
        const val TAG: String = "AddExportForPackageManifest"
    }

    @TaskAction
    fun doTaskAction() {
        SystemPrint.outPrintln(TAG, " is running ")
    }
}