package com.wj.gradle.apkprotect

import com.android.build.gradle.AppExtension
import com.android.build.gradle.tasks.ProcessMultiApkApplicationManifest
import com.wj.gradle.apkprotect.extensions.ApkProtectExtension
import com.wj.gradle.apkprotect.tasks.codedex.EncodeDexIncrementalTask
import com.wj.gradle.apkprotect.tasks.manifest.ReplaceApplicationForManifestTask
import com.wj.gradle.apkprotect.tasks.shellaar.ShellAar2DexIncrementalTask
import com.wj.gradle.apkprotect.tasks.signed.ApkAlignAndSignedIncrementalTask
import com.wj.gradle.apkprotect.tasks.unzip.UnzipApkIncrementalTask
import com.wj.gradle.apkprotect.tasks.zip.ZipApkIncrementalTask
import com.wj.gradle.apkprotect.utils.AppProtectDirectoryUtils
import com.wj.gradle.base.tasks.IWillRunTaskRegisteredListener
import com.wj.gradle.base.tasks.TaskWrapperGeneric
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.RegularFile
import org.gradle.api.tasks.TaskProvider
import java.util.*

/**
 * 处理项目配置之后的Task
 * TODO 需要重新梳理下文件夹，利用原tasks的task进行签名，对齐
 */
open class AfterEvaluateTasksManager {


    /**
     * 第二步:添加解压和加密Task，两个为生产-消费的Task
     * 需要依赖于最后生成的apk的任务[packageDebug]，从中获取到生成Apk的所在目录"outputDirectory"
     * 获取[UnzipApkIncrementalTask]的TaskWrapper,添加到project中
     */
    open fun getUnzipApkAndEncodeDexTaskWrapper(
        project: Project,
        variantName: String
    ): TaskWrapperGeneric<EncodeDexIncrementalTask> {
        val packageDebugTaskName = "package$variantName"
        val unzipTask =
            TaskWrapperGeneric(
                EncodeDexIncrementalTask::class.javaObjectType,
                EncodeDexIncrementalTask.TAG,
                packageDebugTaskName
            )
        unzipTask.producerTaskClass = UnzipApkIncrementalTask::class.javaObjectType
        unzipTask.producerTaskTag = UnzipApkIncrementalTask.TAG
        unzipTask.taskRegisterListener =
            object : IWillRunTaskRegisteredListener<EncodeDexIncrementalTask> {
                override fun willRunTaskRegistered(
                    provider: TaskProvider<EncodeDexIncrementalTask>,
                    producerProvider: TaskProvider<out Task>?
                ) {
                    initUnzipAndEncodeTask(
                        provider,
                        producerProvider,
                        project,
                        packageDebugTaskName,
                        variantName
                    )
                }
            }
        return unzipTask
    }

    /**
     * 第三步之：将原Application替换成壳Application
     * 需要依赖于最后生成的最后的Manifest的任务[processDebugManifest],要在该任务执行之前完成替换
     * 或者在最后生成的manifest之后进行替换,即取判断multiApkManifestOutputDirectory里面的Manifest进行修改
     * * 替换Application为壳的Application
     */
    open fun getReplaceApplicationForManifestTaskWrapper(
        project: Project,
        variantName: String
    ): TaskWrapperGeneric<ReplaceApplicationForManifestTask> {
        val anchorTaskName = "process${variantName}Manifest"
        val manifestTask = TaskWrapperGeneric(
            ReplaceApplicationForManifestTask::class.javaObjectType,
            ReplaceApplicationForManifestTask.TAG,
            anchorTaskName
        )
        manifestTask.isDependsOn = true
        manifestTask.taskRegisterListener =
            object : IWillRunTaskRegisteredListener<ReplaceApplicationForManifestTask> {
                override fun willRunTaskRegistered(
                    provider: TaskProvider<ReplaceApplicationForManifestTask>,
                    producerProvider: TaskProvider<out Task>?
                ) {
                    val manifestTask = provider.get() as ReplaceApplicationForManifestTask
                    val processManifestTask =
                        project.tasks.getByName(anchorTaskName) as ProcessMultiApkApplicationManifest
                    //processManifestTask.multiApkManifestOutputDirectory
                    manifestTask.mergedManifestFile.set(processManifestTask.mainMergedManifest)
                    manifestTask.shellApplicationName.set(ReplaceApplicationForManifestTask.SHELL_APPLICATION_NAME)
                }
            }
        return manifestTask
    }


    /**
     * 第三步之：生成解密dex，copy到解压之后的文件夹
     * 依赖于[EncodeDexIncrementalTask]之后执行
     */
    fun getShellAar2DexTaskWrapper(project: Project): TaskWrapperGeneric<ShellAar2DexIncrementalTask> {


        val shellAar = TaskWrapperGeneric(
            ShellAar2DexIncrementalTask::class.javaObjectType,
            ShellAar2DexIncrementalTask.TAG,
            EncodeDexIncrementalTask.TAG
        )
        shellAar.taskRegisterListener =
            object : IWillRunTaskRegisteredListener<ShellAar2DexIncrementalTask> {
                override fun willRunTaskRegistered(
                    provider: TaskProvider<ShellAar2DexIncrementalTask>,
                    producerProvider: TaskProvider<out Task>?
                ) {
                    initShellAar2DexIncrementalTask(provider, producerProvider, project)
                }
            }
        return shellAar
    }

    /**
     * 第三步之：压缩.apk,生成无签名的.apk
     * 依赖于[EncodeDexIncrementalTask]之后执行
     */
    open fun getZipIncrementalTaskWrapper(
        project: Project,
        variantName: String
    ): TaskWrapperGeneric<ZipApkIncrementalTask> {

        val zipApkIncrementalTask = TaskWrapperGeneric(
            ZipApkIncrementalTask::class.javaObjectType,
            ZipApkIncrementalTask.TAG,
            ShellAar2DexIncrementalTask.TAG
        )
        zipApkIncrementalTask.taskRegisterListener =
            object : IWillRunTaskRegisteredListener<ZipApkIncrementalTask> {
                override fun willRunTaskRegistered(
                    provider: TaskProvider<ZipApkIncrementalTask>,
                    producerProvider: TaskProvider<out Task>?
                ) {
                    initZipIncrementalTask(provider, producerProvider, project, variantName)
                }
            }
        return zipApkIncrementalTask
    }

    /**
     * 第四步：签名apk
     * 通过apksigner进行签名
     *
     * 对齐
     * 依赖于原Gradle的任务队列
     */
    open fun getApkAlignAndSignedTaskWrapper(
        project: Project,
        variantName: String,
        android: AppExtension
    ): TaskWrapperGeneric<ApkAlignAndSignedIncrementalTask> {

        val apkAlignAndSignedTask = TaskWrapperGeneric(
            ApkAlignAndSignedIncrementalTask::class.javaObjectType,
            ApkAlignAndSignedIncrementalTask.TAG,
            ZipApkIncrementalTask.TAG
        )
        apkAlignAndSignedTask.taskRegisterListener =
            object : IWillRunTaskRegisteredListener<ApkAlignAndSignedIncrementalTask> {
                override fun willRunTaskRegistered(
                    provider: TaskProvider<ApkAlignAndSignedIncrementalTask>,
                    producerProvider: TaskProvider<out Task>?
                ) {
                    initApkAlignAndSignedTask(
                        provider,
                        producerProvider,
                        project,
                        variantName,
                        android
                    )
                }
            }

        return apkAlignAndSignedTask
    }


    /**
     * 初始化解压apk和加密的Task[EncodeDexIncrementalTask]
     */
    private fun initUnzipAndEncodeTask(
        provider: TaskProvider<EncodeDexIncrementalTask>,
        producerProvider: TaskProvider<out Task>?,
        project: Project,
        packageDebugTaskName: String,
        variantName: String
    ) {
        //消费Task
        val encodeTask = provider.get()
        //生产Task
        if (producerProvider == null) {
            return
        }
        val unzipTask = producerProvider.get() as UnzipApkIncrementalTask
        //apkDirectory replace by from [packageDebug] at 2022-02-13
        //unzipTask.setConfigFromExtensionAfterEvaluate()
        // val packageTask = project.tasks.getByName(packageDebugTaskName) as PackageApplication
        //  val defaultApkOutput = packageTask.outputDirectory.get().asFile
        unzipTask.unzipDirectory.set(
            AppProtectDirectoryUtils.getUnzipRootDirectoryBaseExtensions(
                project, variantName
            )
        )
        unzipTask.apkDirectory.set(
            AppProtectDirectoryUtils.getDefaultApkOutput(
                project,
                variantName
            )
        )
        //生产-消费的Task
        encodeTask.dexDirectory.set((producerProvider as TaskProvider<UnzipApkIncrementalTask>).flatMap {
            it.unzipDirectory
        })
    }

    /**
     * 初始化[ShellAar2DexIncrementalTask]
     */
    private fun initShellAar2DexIncrementalTask(
        provider: TaskProvider<ShellAar2DexIncrementalTask>,
        producerProvider: TaskProvider<out Task>?,
        project: Project
    ) {
        val aarTask = provider.get()
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
        provider: TaskProvider<ZipApkIncrementalTask>,
        producerProvider: TaskProvider<out Task>?,
        project: Project,
        variantName: String
    ) {

        val zipTask = provider.get()
        val unzipTask =
            project.tasks.getByName(UnzipApkIncrementalTask.TAG) as UnzipApkIncrementalTask
        zipTask.unzipRootDirectory.set(unzipTask.unzipDirectory.get())
        zipTask.zipApkDirectory.set(
            AppProtectDirectoryUtils.getDefaultApkOutput(project, variantName)
        )
    }

    /**
     * 初始化对齐签名Task[ApkAlignAndSignedIncrementalTask]
     */
    private fun initApkAlignAndSignedTask(
        provider: TaskProvider<ApkAlignAndSignedIncrementalTask>,
        producerProvider: TaskProvider<out Task>?,
        project: Project,
        variantName: String,
        android: AppExtension
    ) {
        val alignAndSignTask = provider.get()
        val signingConfigs = android.signingConfigs
        val defaultSigning = android.defaultConfig.signingConfig
        signingConfigs.forEach {
            //huaweiDebug
            val upperName = "${it.name.substring(0, 1).toUpperCase(Locale.getDefault())}${
                it.name.substring(1, it.name.length)
            }"
            if (variantName.contains(upperName)) {
                // SystemPrint.outPrintln(it.name + " , " + it.storeFile?.absolutePath)
                alignAndSignTask.keystoreProperty.set(it.storeFile?.absolutePath)
                alignAndSignTask.keyAliasProperty.set(it.keyAlias)
                alignAndSignTask.keyPassProperty.set(it.keyPassword)
                alignAndSignTask.storePassProperty.set(it.storePassword)
            }
        }
        // signingConfigs
        alignAndSignTask.apkUnsignedDirectory.set(
            AppProtectDirectoryUtils.getDefaultApkOutput(project, variantName)
        )
    }
}