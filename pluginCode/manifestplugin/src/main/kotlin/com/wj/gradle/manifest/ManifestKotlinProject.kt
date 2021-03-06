package com.wj.gradle.manifest

import com.wj.gradle.manifest.extensions.BuildType
import com.wj.gradle.manifest.extensions.ManifestKotlinExtension
import com.wj.gradle.manifest.taskmanager.AddExportForPkgManifestParallelTaskManager
import com.wj.gradle.manifest.taskmanager.AddExportedTaskManager
import com.wj.gradle.manifest.taskmanager.SetLatestVersionTaskManager
import com.wj.gradle.manifest.tasks.manifest.SetLatestVersionForMergedManifestTask
import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.Plugin
import org.gradle.api.Project
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

        //在项目配置结束之后,添加自定义的Task
        project.afterEvaluate {
            try {
                //addExportForPackageManifestAfterEvaluate(it)
                addExportForPkgManifestParallelAfterEvaluate(it)
                addSetLatestVersionForMergedManifestAfterEvaluate(it)
            } catch (e: Exception) {

            }
        }
    }

    /**
     * 找到该APP在打包过程中的所有Manifest文件,在打包编译报错的processDebugManifest执行之前为符合条件的组件添加android:exported
     * @param project
     */
    private fun addExportForPackageManifestAfterEvaluate(project: Project) {
        var addExportedTaskManager = AddExportedTaskManager(project, variantName)
        addExportedTaskManager.addExportForPackageManifestAfterEvaluate()
    }

    /**
     * 并发增量task
     */
    private fun addExportForPkgManifestParallelAfterEvaluate(project: Project) {
        val addExportedManager = AddExportForPkgManifestParallelTaskManager(project, variantName)
        addExportedManager.addExportForPkgManifestParallelTask()
    }

    /**
     * 找到该APP最终的Manifest文件,通过[SetLatestVersionForMergedManifestTask]修改versionCode和versionName
     * @param project
     */
    private fun addSetLatestVersionForMergedManifestAfterEvaluate(project: Project) {
        var setLatestVersionManager = SetLatestVersionTaskManager(project, variantName)
        setLatestVersionManager.addSetLatestVersionForMergedManifestAfterEvaluate()
    }


    /**
     * 在build过程中获取variant name,需要注意这种方法不适用于sync.
     * 在sync过程中该插件不执行sync
     */
    private fun getValidVariantNameInBuild(project: Project): Boolean {
        var parameter = project.gradle.startParameter.taskRequests.toString()
        var regex = if (parameter.contains("assemble")) {
            ":app:assemble(\\w+)"
        } else {
            ":app:generate(\\w+)"
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
}