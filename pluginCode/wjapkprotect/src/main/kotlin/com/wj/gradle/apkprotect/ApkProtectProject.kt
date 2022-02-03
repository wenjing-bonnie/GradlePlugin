package com.wj.gradle.apkprotect

import com.android.build.api.transform.Transform
import com.wj.gradle.apkprotect.extensions.ApkProtectExtension
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
    override fun getAfterEvaluateTasks(project: Project): MutableList<TaskWrapper> {
        val afterManager = AfterEvaluateTasksManager()
        val tasks = mutableListOf<TaskWrapper>()
        tasks.add(afterManager.getUnzipApkAndEncodeDexTaskWrapper())
        tasks.add(afterManager.getZipIncrementalTaskWrapper(project))
        tasks.add(afterManager.getDecodeIncrementalTaskWrapper(project))
        return tasks
    }


    override fun getRegisterTransformTasks(project: Project): MutableList<Transform> {
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
