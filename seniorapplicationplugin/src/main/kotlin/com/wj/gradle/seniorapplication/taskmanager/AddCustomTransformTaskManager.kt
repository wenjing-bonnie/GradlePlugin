package com.wj.gradle.seniorapplication.taskmanager

import com.android.build.gradle.AppExtension
import com.wj.gradle.seniorapplication.tasks.CustomTransformTask
import org.gradle.api.Project

/**
 * Created by wenjing.liu on 2021/11/12 in J1.
 *
 * 添加自定义Transform
 * @author wenjing.liu
 */
open class AddCustomTransformTaskManager(val project: Project, val variantName: String) {

    open fun testAddCustomTransformTask() {
        val extension = project.extensions.findByType(AppExtension::class.javaObjectType)
        extension?.registerTransform(CustomTransformTask())
    }
}