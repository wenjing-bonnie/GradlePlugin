package com.wj.gradle.apkprotect

import com.android.build.api.transform.Transform
import com.wj.gradle.apkprotect.extensions.ApkProtectExtension
import com.wj.gradle.apkprotect.tasks.encode.EncodeDexIncrementalTask
import com.wj.gradle.apkprotect.tasks.unzip.UnzipApkIncrementalTask
import com.wj.gradle.apkprotect.tasks.zip.ZipApkIncrementalTask
import com.wj.gradle.apkprotect.utils.ZipAndUnzipApkDefaultPath
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
    override fun getAfterEvaluateTasks(project: Project): MutableList<TaskWrapper> {
        val tasks = mutableListOf<TaskWrapper>()
        tasks.add(getUnzipApkAndEncodeDexTaskWrapper())
        tasks.add(getZipIncrementalTaskWrapper(project))
        return tasks
    }

    /**
     * 获取[UnzipApkIncrementalTask]的TaskWrapper,添加到project中
     */
    private fun getUnzipApkAndEncodeDexTaskWrapper(): TaskWrapper {
        //assembleHuaweiDebug
        val unzipTaskBuilder =
            TaskWrapper.Builder().setAnchorTaskName("preBuild")
                .setWillRunTaskClass(
                    EncodeDexIncrementalTask::class.javaObjectType,
                    UnzipApkIncrementalTask::class.javaObjectType
                )
                .setWillRunTaskTag(EncodeDexIncrementalTask.TAG, UnzipApkIncrementalTask.TAG)
                .setWillRunTaskRegisterListener(object :
                    TaskWrapper.IWillRunTaskRegisteredListener {
                    override fun willRunTaskRegistered(
                        provider: TaskProvider<Task>,
                        producerProvider: TaskProvider<Task>?
                    ) {
                        initUnzipAndEncodeTask(provider, producerProvider)
                    }
                })
        return unzipTaskBuilder.builder()
    }

    /**
     * 初始化解压apk和加密的Task
     */
    private fun initUnzipAndEncodeTask(
        provider: TaskProvider<Task>,
        producerProvider: TaskProvider<Task>?
    ) {
        //消费Task
        val encodeTask = provider.get()
        if (encodeTask !is EncodeDexIncrementalTask) {
            return
        }
        //生产Task
        if (producerProvider == null) {
            return
        }
        val unzipTask = producerProvider.get()
        if (unzipTask !is UnzipApkIncrementalTask) {
            return
        }
        unzipTask.setConfigFromExtensionAfterEvaluate()
        encodeTask.dexDirectory.set((producerProvider as TaskProvider<UnzipApkIncrementalTask>).flatMap {
            it.unzipDirectory
        })
    }

    /**
     * 添加压缩apk的Task
     */
    private fun getZipIncrementalTaskWrapper(project: Project): TaskWrapper {
        val builder = TaskWrapper.Builder().setAnchorTaskName(EncodeDexIncrementalTask.TAG)
            .setIsDependsOn(false)
            .setWillRunTaskClass(ZipApkIncrementalTask::class.javaObjectType)
            .setWillRunTaskTag(ZipApkIncrementalTask.TAG)
            .setWillRunTaskRegisterListener(object : TaskWrapper.IWillRunTaskRegisteredListener {
                override fun willRunTaskRegistered(
                    provider: TaskProvider<Task>,
                    producerProvider: TaskProvider<Task>?
                ) {
                    initZipIncrementalTask(provider, producerProvider, project)
                }
            })
        return builder.builder()
    }

    /**
     * 初始化压缩apk的Task
     */
    private fun initZipIncrementalTask(
        provider: TaskProvider<Task>,
        producerProvider: TaskProvider<Task>?,
        project: Project
    ) {

        val zipTask = provider.get()
        if (zipTask !is ZipApkIncrementalTask) {
            return
        }
        val unzipApkIncrementalTask = project.tasks.getByName(UnzipApkIncrementalTask.TAG)
        if (unzipApkIncrementalTask !is UnzipApkIncrementalTask) {
            return
        }
        zipTask.unzipRootDirectory.set(ZipAndUnzipApkDefaultPath.getUnzipRootDirectory(project))
        //zipTask.unzipRootDirectory.flatMap { unzipApkIncrementalTask.unzipDirectory }
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
