package com.wj.gradle.apkprotect

import com.android.build.api.transform.Transform
import com.wj.gradle.apkprotect.extensions.ApkProtectExtension
import com.wj.gradle.apkprotect.tasks.unzip.UnzipApkIncrementalTask
import com.wj.gradle.base.WjVariantBaseProject
import com.wj.gradle.base.tasks.TaskWrapper
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

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
        tasks.add(getUnzipApkIncrementalTaskWrapper())
        return tasks
    }

    /**
     * 获取[UnzipApkIncrementalTask]的TaskWrapper,添加到project中
     */
    private fun getUnzipApkIncrementalTaskWrapper(): TaskWrapper {
        val unzipTaskBuilder =
            TaskWrapper.Builder.setAnchorTaskName("assembleHuaweiDebug")
                .setWillRunTaskClass(UnzipApkIncrementalTask::class.javaObjectType)
                .setWillRunTaskTag(UnzipApkIncrementalTask.TAG)
                .setWillRunTaskRegisterListener(object :
                    TaskWrapper.IWillRunTaskRegisteredListener {
                    override fun willRunTaskRegistered(provider: TaskProvider<Task>) {
                        val unzipTask = provider.get()
                        if (unzipTask !is UnzipApkIncrementalTask) {
                            return
                        }
                        unzipTask.setConfigFromExtensionAfterEvaluate()
                    }
                })
        return unzipTaskBuilder.builder();
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