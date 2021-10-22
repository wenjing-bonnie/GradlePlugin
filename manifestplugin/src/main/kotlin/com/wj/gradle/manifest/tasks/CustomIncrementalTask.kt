package com.wj.gradle.manifest.tasks

import com.android.build.api.transform.Transform
import com.android.build.gradle.internal.profile.AnalyticsService
import com.android.build.gradle.internal.tasks.NewIncrementalTask
import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.work.InputChanges
import javax.inject.Inject

/**
 * Created by wenjing.liu on 2021/10/9 in J1.
 *
 * @author wenjing.liu
 */
open abstract class CustomIncrementalTask : NewIncrementalTask() {
    companion object {
        const val TAG: String = "CustomIncremental"
    }

    init {
        outputs.upToDateWhen { false }
    }

//    @get:Internal
// override val analyticsService: Property<AnalyticsService>


    override fun doTaskAction(inputChanges: InputChanges) {
        SystemPrint.outPrintln(TAG, "is running " + inputChanges)
    }

}