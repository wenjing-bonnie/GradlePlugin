package com.wj.gradle.manifest

import com.wj.gradle.manifest.task.AddExportForPackageManifestTask
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
        if (!getValidVariantNameInBuild(p0)) {
            return
        }
        SystemPrint.outPrintln("Welcome ManifestKotlinProject")
        addTaskForVariantAfterEvaluate(p0)
    }

    /**
     *在项目配置完之后添加自定义的Task
     */
    private fun addTaskForVariantAfterEvaluate(project: Project) {
        val exportTask = project.tasks.create(
            AddExportForPackageManifestTask.TAG,
            AddExportForPackageManifestTask::class.javaObjectType
        )
        project.afterEvaluate {
            addExportForPackageManifestAfterEvaluate(it, exportTask)
        }
    }

    /**
     * 找到该APP在打包过程中的所有Manifest文件,在打包编译报错的processDebugManifest执行之前为符合条件的组件添加android:exported
     */
    private fun addExportForPackageManifestAfterEvaluate(
        project: Project,
        exportTask: AddExportForPackageManifestTask
    ) {
        val processManifestTask = project.tasks.getByName("process${variantName}MainManifest")
        processManifestTask.dependsOn(exportTask)
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
}