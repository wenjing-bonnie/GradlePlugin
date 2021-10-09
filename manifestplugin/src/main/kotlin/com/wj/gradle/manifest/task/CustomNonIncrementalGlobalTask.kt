package com.wj.gradle.manifest.task

import com.android.build.gradle.internal.profile.AnalyticsService
import com.android.build.gradle.internal.tasks.NonIncrementalGlobalTask
import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.provider.Property

/**
 * Created by wenjing.liu on 2021/10/9 in J1.
 *其中的analyticsService.get()必须要获取到一个
 * @author wenjing.liu
 */
abstract class CustomNonIncrementalGlobalTask : NonIncrementalGlobalTask() {

    companion object {
        const val TAG: String = "CustomNonIncrementalGlobal"
    }

//    override val analyticsService: Property<AnalyticsService>
//        get() = project.

    override fun doTaskAction() {
        SystemPrint.outPrintln(CustomNonIncrementalTask.TAG, "is running")
    }
}