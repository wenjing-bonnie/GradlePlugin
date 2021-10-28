package com.wj.gradle.manifest.tasks.parallel

import com.android.build.gradle.internal.profile.AnalyticsService
import com.android.build.gradle.internal.tasks.NewIncrementalTask
import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
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
 * @author wenjing.liu
 */
abstract class CustomIncrementalTask : NewIncrementalTask() {
    companion object {
        const val TAG: String = "CustomIncremental"
    }

    init {
        outputs.upToDateWhen { false }
    }

    @get:Internal
    abstract val getAnalyticsService: Property<AnalyticsService>

    override val analyticsService: Property<AnalyticsService>
        get() = getAnalyticsService


    override fun doTaskAction(inputChanges: InputChanges) {
        SystemPrint.outPrintln(TAG, "is running " + inputChanges)
    }

}