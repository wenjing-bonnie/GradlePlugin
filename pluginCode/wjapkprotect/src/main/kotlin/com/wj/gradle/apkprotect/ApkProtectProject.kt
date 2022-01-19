package com.wj.gradle.apkprotect

import com.android.build.api.transform.Transform
import com.wj.gradle.base.WjVariantBaseProject
import com.wj.gradle.base.tasks.TaskWrapper
import org.gradle.api.Project

/**
 * Created by wenjing.liu on 2022/1/19 in J1.
 *
 * @author wenjing.liu
 */
open class ApkProtectProject : WjVariantBaseProject() {

    override fun createExtension(project: Project) {
        TODO("Not yet implemented")
    }

    override fun getAfterEvaluateTaskDependsOn(): MutableList<TaskWrapper> {
        TODO("Not yet implemented")
    }

    override fun getRegisterTransformTasks(): MutableList<Transform> {
        TODO("Not yet implemented")
    }


}