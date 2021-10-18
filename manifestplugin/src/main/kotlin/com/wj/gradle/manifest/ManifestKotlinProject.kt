package com.wj.gradle.manifest

import com.android.build.gradle.internal.tasks.AndroidVariantTask
import com.android.build.gradle.tasks.ProcessApplicationManifest
import com.android.build.gradle.tasks.ProcessMultiApkApplicationManifest
import com.wj.gradle.manifest.extensions.BuildType
import com.wj.gradle.manifest.extensions.ManifestKotlinExtension
import com.wj.gradle.manifest.tasks.AddExportForPackageManifestTask
import com.wj.gradle.manifest.tasks.SetLatestVersionForMergedManifestTask
import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File
import java.util.regex.Pattern

/**
 * Created by wenjing.liu on 2021/9/27 in J1.
 * 插件入口类
 * @author wenjing.liu
 */
class ManifestKotlinProject : Plugin<Project> {
    /**
     * variant name
     */
    private var variantName: String = ""

    override fun apply(p0: Project) {
        //在配置扩展属性的时候,一定要保证无论什么情况都可以调用到.像如果把该方法移到if之后,则会始终找不到配置的扩展属性
        createExtension(p0)
        if (!getValidVariantNameInBuild(p0)) {
            return
        }
        SystemPrint.outPrintln("Welcome ManifestKotlinProject")
        addTasksForVariantAfterEvaluate(p0)
    }

    /**
     * 创建属性扩展
     * @param project
     */
    private fun createExtension(project: Project) {
        project.extensions.create(
            ManifestKotlinExtension.TAG,
            ManifestKotlinExtension::class.javaObjectType,
            project.container(BuildType::class.javaObjectType)
        )
    }

    /**
     * 在项目配置完之后添加自定义的Task
     * @param project
     */
    private fun addTasksForVariantAfterEvaluate(project: Project) {

        //用Provider.get()获取task值的时候，才会去创建这个task。
//        var nonIncrementalTask = project.tasks.register(
//            CustomNonIncrementalTask.TAG,
//            CustomNonIncrementalTask::class.javaObjectType
//        ).get()
//        var incrementalTask = project.tasks.register(
//            CustomIncrementalTask.TAG,
//            CustomIncrementalTask::class.javaObjectType
//        ).get()
//        var nonIncrementalGlobalTask = project.tasks.register(
//            CustomNonIncrementalGlobalTask.TAG,
//            CustomNonIncrementalGlobalTask::class.javaObjectType
//        ).get()
        //在项目配置结束之后,添加自定义的Task
        project.afterEvaluate {
            addExportForPackageManifestAfterEvaluate(it)
            addSetLatestVersionForMergedManifestAfterEvaluate(it)
//            testAddTaskAfterEvaluate(
//                it,
//                nonIncrementalTask//,
//                //nonIncrementalGlobalTask,
//                //incrementalTask
//            )
//            testExtension(it)
        }
    }

    /**
     * 找到该APP在打包过程中的所有Manifest文件,在打包编译报错的processDebugManifest执行之前为符合条件的组件添加android:exported
     * @param project
     */
    private fun addExportForPackageManifestAfterEvaluate(project: Project) {

        val processManifestTask =
            project.tasks.getByName("process${variantName}MainManifest")
        if (processManifestTask !is ProcessApplicationManifest) {
            return
        }
        //创建自定义Task
        var exportTask = project.tasks.register(
            AddExportForPackageManifestTask.TAG,
            AddExportForPackageManifestTask::class.javaObjectType,
        ).get()
        exportTask.setInputMainManifest(processManifestTask.mainManifest.get())
        exportTask.setInputManifests(processManifestTask.getManifests())
        processManifestTask.dependsOn(exportTask)
    }

    /**
     * 找到该APP最终的Manifest文件,通过[SetLatestVersionForMergedManifestTask]修改versionCode和versionName
     * @param project
     */
    private fun addSetLatestVersionForMergedManifestAfterEvaluate(project: Project) {
        val multiApkApplicationManifest =
            project.tasks.getByName("process${variantName}Manifest")

        if (multiApkApplicationManifest !is ProcessMultiApkApplicationManifest) {
            return
        }

        var versionTask = project.tasks.create(
            SetLatestVersionForMergedManifestTask.TAG,
            SetLatestVersionForMergedManifestTask::class.javaObjectType
        )
        versionTask.setMainMergedManifest(multiApkApplicationManifest.mainMergedManifest.asFile.get())
        versionTask.setInputVersionFile(getVersionManagerFromExtension(project))
        multiApkApplicationManifest.finalizedBy(versionTask)
    }

    /**
     * 从extension中获取version 管理文件
     */
    private fun getVersionManagerFromExtension(project: Project): File {
        var extension = project.extensions.findByType(ManifestKotlinExtension::class.javaObjectType)
        if (extension == null) {
            return File("")
        }
        return extension.versionManager()
    }

    /**
     * 在build过程中获取variant name,需要注意这种方法不适用于sync.
     * 在sync过程中该插件不执行sync
     */
    private fun getValidVariantNameInBuild(project: Project): Boolean {
        var parameter = project.gradle.startParameter.taskRequests.toString()
        var regex = if (parameter.contains("assemble")) {
            "assemble(\\w+)"
        } else {
            "generate(\\w+)"
        }
        var pattern = Pattern.compile(regex)
        var matcher = pattern.matcher(parameter)
        if (matcher.find()) {
            //group(0)整个字符串;group(1)第一个括号内的内容;group(2)第二个括号内的内容
            variantName = matcher.group(1)
        }
        //SystemPrint.outPrintln(variantName)
        if (variantName.isNullOrBlank()) {
            return false
        }
        return true
    }

    /**
     * 测试设置extension
     */
    private fun testExtension(project: Project) {
        var extension: ManifestKotlinExtension? =
            project.extensions.findByType(ManifestKotlinExtension::class.javaObjectType) ?: return
        SystemPrint.outPrintln("test set manifestKotlin{} extension: $extension \n")
    }

    /**
     *  测试 增加NewIncrementalTask
     */
    private fun testAddTaskAfterEvaluate(
        project: Project,
        vararg tasks: Task
    ) {
        var preBuildTask = project.tasks.getByName("pre${variantName}Build")
        for (task in tasks) {
            if (task is AndroidVariantTask) {
                task.variantName = variantName
            }
            preBuildTask.finalizedBy(task)
        }
    }

}