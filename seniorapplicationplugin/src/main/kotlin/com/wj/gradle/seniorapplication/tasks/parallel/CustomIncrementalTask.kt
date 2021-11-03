package com.wj.gradle.manifest.tasks.parallel

import com.android.build.gradle.internal.profile.AnalyticsService
import com.android.build.gradle.internal.tasks.NewIncrementalTask
import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.work.InputChanges
import javax.naming.spi.ObjectFactory

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
abstract class CustomIncrementalTask : NewIncrementalTask() {
    companion object {
        const val TAG: String = "CustomIncrementalTask"
    }

    init {
        outputs.upToDateWhen { false }
    }

    override fun doTaskAction(inputChanges: InputChanges) {
        SystemPrint.outPrintln(TAG, "is running ")
    }

}