package com.wj.gradle.apkprotect

import com.android.build.api.transform.Transform
import com.wj.gradle.apkprotect.extensions.ApkProtectExtension
import com.wj.gradle.apkprotect.tasks.unzip.UnzipApkIncrementalTask
import com.wj.gradle.apkprotect.tasks.zip.ZipApkIncrementalTask
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
        //tasks.add(getZipApkIncrementalTaskWrapper())
        return tasks
    }

    /**
     * 获取[UnzipApkIncrementalTask]的TaskWrapper,添加到project中
     */
    private fun getUnzipApkIncrementalTaskWrapper(): TaskWrapper {
        //assembleHuaweiDebug
        val unzipTaskBuilder =
            TaskWrapper.Builder.setAnchorTaskName("preBuild")
                .setWillRunTaskClass(
                    UnzipApkIncrementalTask::class.javaObjectType,
                    ZipApkIncrementalTask::class.javaObjectType
                )
                .setWillRunTaskTag(UnzipApkIncrementalTask.TAG, ZipApkIncrementalTask.TAG)
                .setWillRunTaskRegisterListener(object :
                    TaskWrapper.IWillRunTaskRegisteredListener {
                    override fun willRunTaskBeforeDependsOnAnchorTask(
                        provider: TaskProvider<Task>,
                        producerProvider: TaskProvider<Task>?
                    ) {
                        val unzipTask = provider.get()
                        if (unzipTask !is UnzipApkIncrementalTask || producerProvider == null) {
                            return
                        }
                        val zipTask = producerProvider.get()
                        if (zipTask !is ZipApkIncrementalTask) {
                            return
                        }
                        unzipTask.setConfigFromExtensionAfterEvaluate()
                        zipTask.unzipRootDirectory.set((provider as TaskProvider<UnzipApkIncrementalTask>).flatMap {
                            it.unzipDirectory
                        })

                    }

                    override fun willRunTaskRegistered(
                        provider: TaskProvider<Task>,
                        producerProvider: TaskProvider<Task>?
                    ) {
                        val unzipTask = provider.get()
                        if (unzipTask !is UnzipApkIncrementalTask || producerProvider == null) {
                            return
                        }
                        val zipTask = producerProvider.get()
                        if (zipTask !is ZipApkIncrementalTask) {
                            return
                        }
                        unzipTask.setConfigFromExtensionAfterEvaluate()
                        zipTask.unzipRootDirectory.set((provider as TaskProvider<UnzipApkIncrementalTask>).flatMap {
                            it.unzipDirectory
                        })

                    }
                })
        return unzipTaskBuilder.builder()
    }

    /**
     * 压缩文件
     */
    private fun getZipApkIncrementalTaskWrapper(): TaskWrapper {
        val zipTaskBuilder = TaskWrapper.Builder
            .setWillRunTaskClass(ZipApkIncrementalTask::class.javaObjectType)
            .setWillRunTaskTag(ZipApkIncrementalTask.TAG)
            .setAnchorTaskName(UnzipApkIncrementalTask.TAG)
            .setIsDependsOn(false)
//            .setWillRunTaskRegisterListener(object : TaskWrapper.IWillRunTaskRegisteredListener {
//                override fun willRunTaskRegistered(
//                    provider: TaskProvider<Task>,
//                    producerProvider: TaskProvider<Task>?
//                ) {
//                    val zipTask = provider.get()
//                    if (zipTask !is ZipApkIncrementalTask) {
//                        return
//                    }
//                    //  zipTask.unzipRootDirectory.set()
//                    zipTask.setConfigFromExtensionAfterEvaluate()
//                }
//            })
        return zipTaskBuilder.builder()
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
