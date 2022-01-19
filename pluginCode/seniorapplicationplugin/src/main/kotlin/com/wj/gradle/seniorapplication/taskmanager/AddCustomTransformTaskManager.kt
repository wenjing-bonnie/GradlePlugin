package com.wj.gradle.seniorapplication.taskmanager

import com.android.build.gradle.AppExtension
import com.wj.gradle.seniorapplication.tasks.transform.AutoLogTransformTask
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
        //因为这个task是在项目构建之前添加到项目中的，而extension只有在项目构建后才能得到
        //所以这里将传入project，在task中取得配置的内容
        extension?.registerTransform(AutoLogTransformTask(project))
    }


}