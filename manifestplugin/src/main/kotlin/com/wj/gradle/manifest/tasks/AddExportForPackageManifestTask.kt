package com.wj.gradle.manifest.tasks

import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.*
import javax.inject.Inject

/**
 * Created by wenjing.liu on 2021/9/30 in J1.
 *
 * 用来适配Android12,自动为没有适配Android12的组件添加android:exported的属性
 * 输入所有的被打包到APP的manifest文件以及app这个module下对应的manifest文件
 * 执行该任务之后,所有符合条件的组件都会添加android:exported
 *
 * @author wenjing.liu
 */

open class AddExportForPackageManifestTask : DefaultTask() {
    companion object {
        const val TAG: String = "AddExportForPackageManifest"
    }

    private val ATTRUBUTE_EXPORTED: String = "{http://schemas.android.com/apk/res/android}exported"
    private lateinit var inputManifests: FileCollection
    private lateinit var inputMainManifest: File

    open fun setInputManifests(input: FileCollection) {
        this.inputManifests = input
    }

    open fun setInputMainManifest(input: File) {
        this.inputMainManifest = input
    }

    @TaskAction
    fun doTaskAction() {
        SystemPrint.errorPrintln(
            TAG,
            "<<!!!  Warning begin ...\n" +
                    "开始为所有被打包到APP的manifest文件 检查和增加\"android:exported\"\n" +
                    "因为操作的第三方的manifest,所以该属性为true" +
                    "  Warning end ... >>"
        )
        for (input in inputManifests) {
            SystemPrint.outPrintln(TAG, "input = " + input.absolutePath)
        }
        SystemPrint.outPrintln(TAG, "input main = " + inputMainManifest.absolutePath)
    }
}