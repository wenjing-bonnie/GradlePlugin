package com.wj.gradle.manifest.tasks.others

import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by wenjing.liu on 2021/10/26 in J1.
 * 测试懒加载
 * https://docs.gradle.org/current/userguide/lazy_configuration.html#working_with_files_in_lazy_properties
 * @author wenjing.liu
 */
abstract class LazyConfigurationTask : DefaultTask() {
    companion object {
        const val TAG = "LazyConfigurationTask"
    }

    @TaskAction
    open fun runTaskAction() {
        SystemPrint.outPrintln(TAG, "running !!!!")
    }
}