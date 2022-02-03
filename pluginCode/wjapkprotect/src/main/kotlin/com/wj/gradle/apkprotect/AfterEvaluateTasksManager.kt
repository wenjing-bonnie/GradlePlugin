package com.wj.gradle.apkprotect

import com.wj.gradle.apkprotect.tasks.encode.EncodeDexIncrementalTask
import com.wj.gradle.apkprotect.tasks.unzip.UnzipApkIncrementalTask
import com.wj.gradle.apkprotect.tasks.zip.ZipApkIncrementalTask
import com.wj.gradle.apkprotect.utils.ZipAndUnzipApkDefaultPath
import com.wj.gradle.base.tasks.TaskWrapper
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * 处理项目配置之后的Task
 */
open class AfterEvaluateTasksManager {

    /**
     * 第一步:添加解压和加密Task，两个为生产-消费的Task
     * 获取[UnzipApkIncrementalTask]的TaskWrapper,添加到project中
     */
    open fun getUnzipApkAndEncodeDexTaskWrapper(): TaskWrapper {
        //assembleHuaweiDebug
        val unzipTaskBuilder =
            TaskWrapper.Builder().setAnchorTaskName("preBuild")
                .setWillRunTaskClass(
                    EncodeDexIncrementalTask::class.javaObjectType,
                    UnzipApkIncrementalTask::class.javaObjectType
                )
                .setWillRunTaskTag(EncodeDexIncrementalTask.TAG_ENCODE, UnzipApkIncrementalTask.TAG)
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
     * 第二步：生成解密dex，存放到解压之后的文件夹
     */

    /**
     * 第三步：
     */

    /**
     * TODO
     * 测试加密之后的dex文件，通过解密之后，可压缩可用的apk
     */
    open fun getDecodeIncrementalTaskWrapper(project: Project): TaskWrapper {
        val builder = TaskWrapper.Builder()
            .setWillRunTaskClass(EncodeDexIncrementalTask::class.javaObjectType)
            .setWillRunTaskTag(EncodeDexIncrementalTask.TAG_DECODE)
            .setIsDependsOn(false)
            .setAnchorTaskName(EncodeDexIncrementalTask.TAG_ENCODE)
            .setWillRunTaskRegisterListener(object : TaskWrapper.IWillRunTaskRegisteredListener {
                override fun willRunTaskRegistered(
                    provider: TaskProvider<Task>,
                    producerProvider: TaskProvider<Task>?
                ) {
                    initDecodeIncrementalTask(provider, producerProvider, project)
                }
            })
        return builder.builder()
    }

    /**
     * 最后一步:压缩.apk
     * TODO 暂时设置TAG_DECODE为锚点，测试加密解密之后可行
     * 添加压缩apk的Task
     */
    open fun getZipIncrementalTaskWrapper(project: Project): TaskWrapper {
        //TODO 暂时设置TAG_DECODE为锚点，测试加密解密之后可行
        val builder = TaskWrapper.Builder()
            .setAnchorTaskName(EncodeDexIncrementalTask.TAG_DECODE)
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
        encodeTask.isEncodeTask = true
        encodeTask.dexDirectory.set((producerProvider as TaskProvider<UnzipApkIncrementalTask>).flatMap {
            it.unzipDirectory
        })
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
        //TODO 这里的获取方式需要优化
        //zipTask.unzipRootDirectory.set(unzipApkIncrementalTask.unzipDirectory.get())
        zipTask.unzipRootDirectory.set(ZipAndUnzipApkDefaultPath.getUnzipRootDirectory(project))
    }

    private fun initDecodeIncrementalTask(
        provider: TaskProvider<Task>,
        producerProvider: TaskProvider<Task>?,
        project: Project
    ) {
        val decodeTask = provider.get()
        if (decodeTask !is EncodeDexIncrementalTask) {
            return
        }

        val unzipApkIncrementalTask = project.tasks.getByName(UnzipApkIncrementalTask.TAG)
        if (unzipApkIncrementalTask !is UnzipApkIncrementalTask) {
            return
        }
        decodeTask.isEncodeTask = false
        //TODO 这里的获取方式需要优化
        //decodeTask.dexDirectory.set(unzipApkIncrementalTask.unzipDirectory.get())
        decodeTask.dexDirectory.set(ZipAndUnzipApkDefaultPath.getUnzipRootDirectory(project))
    }

}