package com.wj.gradle.seniorapplication.taskmanager;

import com.android.build.gradle.internal.profile.AnalyticsService
import com.wj.gradle.manifest.tasks.parallel.CustomIncrementalTask
import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.Project

/**
 * Created by wenjing.liu on 2021/10/29 in J1.
 *
 * 验证并行Task
 * @author wenjing.liu
 */
open class AddCustomParallelTaskManager(var project: Project, var variantName: String) {

    open fun testCustomIncrementalTask() {
        var customIncremental = project.tasks.create(
            CustomIncrementalTask.TAG,
            CustomIncrementalTask::class.javaObjectType
        )
        customIncremental.variantName = variantName
        customIncremental.analyticsService.set(
            AnalyticsService.RegistrationAction(project).execute()
        )
        val preBuild = project.tasks.getByName("preBuild")
        preBuild.dependsOn(customIncremental)
    }
}
