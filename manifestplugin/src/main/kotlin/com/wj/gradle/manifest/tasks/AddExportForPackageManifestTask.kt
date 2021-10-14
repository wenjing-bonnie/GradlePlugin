package com.wj.gradle.manifest.tasks

import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by wenjing.liu on 2021/9/30 in J1.
 *
 * 用来适配Android12,自动为没有适配Android12的组件添加android:exported的属性
 * @author wenjing.liu
 */
open class AddExportForPackageManifestTask : DefaultTask() {
    companion object {
        const val TAG: String = "AddExportForPackageManifest"
    }
    private val ATTRUBUTE_EXPORTED: String = "{http://schemas.android.com/apk/res/android}exported"

    @TaskAction
    fun doTaskAction() {
        SystemPrint.outPrintln(TAG, " is running ")
    }
}