package com.wj.gradle.apkprotect

import com.android.build.api.transform.Transform
import com.wj.gradle.apkprotect.extensions.ApkProtectExtension
import com.wj.gradle.apkprotect.tasks.unzip.UnzipApkIncrementalTask
import com.wj.gradle.apkprotect.utils.ZipAndUnZipApkUtils
import com.wj.gradle.base.WjVariantBaseProject
import com.wj.gradle.base.tasks.TaskWrapper
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.Project

/**
 * Created by wenjing.liu on 2022/1/19 in J1.
 * 对apk进行加固
 * @author wenjing.liu
 */
open class ApkProtectProject : WjVariantBaseProject() {


    override fun applyExceptRegister(project: Project) {
        // testExtension(project)
    }

    /**
     * 创建extension
     */
    override fun createExtension(project: Project) {
        super.createExtension(project)
        project.extensions.create(
            ApkProtectExtension.TAG,
            ApkProtectExtension::class.javaObjectType
        )
    }

    /**
     * 在项目配置完成之后添加Task
     */
    override fun getAfterEvaluateTasks(): MutableList<TaskWrapper> {
        val tasks = mutableListOf<TaskWrapper>()
        val unzipTask =
            TaskWrapper.Builder.setAnchorTaskName("preBuild")
                .setWillRunTaskClass(UnzipApkIncrementalTask::class.javaObjectType)
                .setWillRunTaskTag(UnzipApkIncrementalTask.TAG)
                .setWillRunTaskRegisterListener(object :TaskWrapper.IWillRunTaskRegisteredListener{})
        //("assemble",true,UnzipApkIncrementalTask.TAG)
        tasks.add(unzipTask.builder())
        return tasks
    }

    override fun getRegisterTransformTasks(): MutableList<Transform> {
        return mutableListOf()
    }

    /**'
     * 测试extension ok
     */
    private fun testExtension(project: Project) {
        project.afterEvaluate {
            val extension = getCreatedExtension(project, ApkProtectExtension::class.javaObjectType)
            if (extension != null) {
                SystemPrint.outPrintln(extension.lazyApkDirectory.asFile.get().absolutePath)
                SystemPrint.outPrintln(extension.unzipDirectory.asFile.get().absolutePath)
            }
        }
    }

}