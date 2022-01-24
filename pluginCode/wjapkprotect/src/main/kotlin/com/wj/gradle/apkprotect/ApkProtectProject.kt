package com.wj.gradle.apkprotect

import com.android.build.api.transform.Transform
import com.wj.gradle.apkprotect.utils.ZipUtils
import com.wj.gradle.base.WjVariantBaseProject
import com.wj.gradle.base.tasks.TaskWrapper
import org.gradle.api.Project

/**
 * Created by wenjing.liu on 2022/1/19 in J1.
 *
 * @author wenjing.liu
 */
open class ApkProtectProject : WjVariantBaseProject() {


    override fun applyExceptRegister(project: Project) {
        ZipUtils.zipFile(project)
    }


    override fun getAfterEvaluateTasks(): MutableList<TaskWrapper> {
        return mutableListOf()
    }

    override fun getRegisterTransformTasks(): MutableList<Transform> {
        return mutableListOf()
    }


}