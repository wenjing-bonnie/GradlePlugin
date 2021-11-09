package com.wj.gradle.sensorapplication.tasks.parallel

import com.android.build.gradle.internal.tasks.NonIncrementalGlobalTask
import com.wj.gradle.manifest.utils.SystemPrint
import com.wj.gradle.seniorapplication.tasks.parallel.ClassLoaderIsolationTask

/**
 * Created by wenjing.liu on 2021/10/9 in J1.
 *其中的analyticsService.get()必须要获取到一个
 *
 * TODO AnalyticsService.RegistrationAction(project).execute()可以获得一个Provider,暂时还没有搞清楚怎么转换成Property
 * @author wenjing.liu
 */
abstract class CustomNonIncrementalGlobalTask : NonIncrementalGlobalTask() {

    companion object {
        const val TAG: String = "CustomNonIncrementalGlobal"
    }
    //Property<T> extends Provider<T>
//    override val analyticsService: Property<AnalyticsService>
//        get() =





    override fun doTaskAction() {
        SystemPrint.outPrintln(ClassLoaderIsolationTask.TAG, "is running")
    }
}