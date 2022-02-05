package com.wj.gradle.apkprotect

import com.wj.gradle.apkprotect.extensions.ApkProtectExtension
import com.wj.gradle.apkprotect.tasks.codedex.DecodeDexIncrementalTask
import com.wj.gradle.apkprotect.tasks.codedex.EncodeDexIncrementalTask
import com.wj.gradle.apkprotect.tasks.shellaar.ShellAar2DexIncrementalTask
import com.wj.gradle.apkprotect.tasks.unzip.UnzipApkIncrementalTask
import com.wj.gradle.apkprotect.tasks.zip.ZipApkIncrementalTask
import com.wj.gradle.apkprotect.utils.AppProtectDirectoryUtils
import com.wj.gradle.base.tasks.TaskWrapper
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.RegularFile
import org.gradle.api.tasks.TaskProvider

/**
 * 处理项目配置之后的Task
 */
open class AfterEvaluateTasksManager {

    /**
     * 第一步:添加解压和加密Task，两个为生产-消费的Task
     * 获取[UnzipApkIncrementalTask]的TaskWrapper,添加到project中
     */
    open fun getUnzipApkAndEncodeDexTaskWrapper(project: Project): TaskWrapper {
        //TODO 还没有找到合适的锚点 assembleHuaweiDebug
        val unzipTaskBuilder =
            TaskWrapper.Builder().setAnchorTaskName("preBuild")
                .setWillRunTaskClass(
                    EncodeDexIncrementalTask::class.javaObjectType,
                    UnzipApkIncrementalTask::class.javaObjectType
                )
                .setIsDependsOn(true)
                .setWillRunTaskTag(EncodeDexIncrementalTask.TAG, UnzipApkIncrementalTask.TAG)
                .setWillRunTaskRegisterListener(object :
                    TaskWrapper.IWillRunTaskRegisteredListener {
                    override fun willRunTaskRegistered(
                        provider: TaskProvider<Task>,
                        producerProvider: TaskProvider<Task>?
                    ) {
                        initUnzipAndEncodeTask(provider, producerProvider, project)
                    }
                })
        return unzipTaskBuilder.builder()
    }

    /**
     * 第二步：生成解密dex，存放到解压之后的文件夹
     */

    fun getShellAar2DexTaskWrapper(project: Project): TaskWrapper {
        val shellAar = TaskWrapper.Builder().setAnchorTaskName(EncodeDexIncrementalTask.TAG)
            .setWillRunTaskClass(ShellAar2DexIncrementalTask::class.javaObjectType)
            .setWillRunTaskTag(ShellAar2DexIncrementalTask.TAG)
            .setWillRunTaskRegisterListener(object : TaskWrapper.IWillRunTaskRegisteredListener {
                override fun willRunTaskRegistered(
                    provider: TaskProvider<Task>,
                    producerProvider: TaskProvider<Task>?
                ) {
                    initShellAar2DexIncrementalTask(provider, producerProvider, project)
                }
            })
        return shellAar.builder()
    }

    /**
     * 第三步:压缩.apk,生成无签名的.apk
     * 添加压缩apk的Task
     */
    open fun getZipIncrementalTaskWrapper(project: Project): TaskWrapper {
        val builder = TaskWrapper.Builder()
            .setWillRunTaskClass(ZipApkIncrementalTask::class.javaObjectType)
            .setWillRunTaskTag(ZipApkIncrementalTask.TAG)
            .setAnchorTaskName(ShellAar2DexIncrementalTask.TAG)
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
     * 第三步：
     */

    /**
     * TODO
     * 测试加密之后的dex文件，通过解密之后，可压缩可用的apk
     */
    open fun getDecodeIncrementalTaskWrapper(project: Project): TaskWrapper {
        val builder = TaskWrapper.Builder()
            .setWillRunTaskClass(DecodeDexIncrementalTask::class.javaObjectType)
            .setWillRunTaskTag(DecodeDexIncrementalTask.TAG)
            .setAnchorTaskName(EncodeDexIncrementalTask.TAG)
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
     * 初始化解压apk和加密的Task[EncodeDexIncrementalTask]
     */
    private fun initUnzipAndEncodeTask(
        provider: TaskProvider<Task>,
        producerProvider: TaskProvider<Task>?,
        project: Project
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
     * 初始化[ShellAar2DexIncrementalTask]
     */
    private fun initShellAar2DexIncrementalTask(
        provider: TaskProvider<Task>,
        producerProvider: TaskProvider<Task>?,
        project: Project
    ) {
        val aarTask = provider.get()
        if (aarTask !is ShellAar2DexIncrementalTask) {
            return
        }
        aarTask.shellAarFileProperty.set(getAarFileFromExtension(project))
    }

    /**
     * 从配置中获取aar的路径
     */
    private fun getAarFileFromExtension(project: Project): RegularFile {
        val extension = project.extensions.findByType(ApkProtectExtension::class.javaObjectType)
            ?: throw RuntimeException("Not found the \"shellAarFile\". You must set \"shellAarFile\" by apkProtectExtension{} in build.gradle!")

        val shellAarFile = extension.shellAarFile
        //TODO 需要优化成自定义壳aar及加密的dex的方式
        if (shellAarFile.orNull == null) {
            throw RuntimeException("Not found the \"shellAarFile\". You must set by apkProtectExtension{} in build.gradle!")
        }
        return shellAarFile.get()
    }

    /**
     * 初始化压缩apk的Task[ZipApkIncrementalTask]
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
        zipTask.unzipRootDirectory.set(
            AppProtectDirectoryUtils.getUnzipRootDirectoryBaseExtensions(
                project
            )
        )
        zipTask.zipApkDirectory.set(
            AppProtectDirectoryUtils.getUnsignedApkZipDirectoryFromUnzipDirectory(
                project
            )
        )
    }

    /**
     * 初始化解密task[DecodeDexIncrementalTask]
     */
    private fun initDecodeIncrementalTask(
        provider: TaskProvider<Task>,
        producerProvider: TaskProvider<Task>?,
        project: Project
    ) {
        val decodeTask = provider.get()
        if (decodeTask !is DecodeDexIncrementalTask) {
            return
        }

        val unzipApkIncrementalTask = project.tasks.getByName(UnzipApkIncrementalTask.TAG)
        if (unzipApkIncrementalTask !is UnzipApkIncrementalTask) {
            return
        }
        //TODO 这里的获取方式需要优化
        //decodeTask.dexDirectory.set(unzipApkIncrementalTask.unzipDirectory.get())
        decodeTask.dexDirectory.set(
            AppProtectDirectoryUtils.getUnzipRootDirectoryBaseExtensions(
                project
            )
        )
    }

}