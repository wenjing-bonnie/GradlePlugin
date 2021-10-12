package com.wj.gradle.manifest

import com.android.build.gradle.internal.tasks.AndroidVariantTask
import com.wj.gradle.manifest.tasks.AddExportForPackageManifestTask
import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
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
        addTasksForVariantAfterEvaluate(p0)
    }

    /**
     *在项目配置完之后添加自定义的Task
     */
    private fun addTasksForVariantAfterEvaluate(project: Project) {
        //创建自定义Task
        var exportTask = project.tasks.register(
            AddExportForPackageManifestTask.TAG,
            AddExportForPackageManifestTask::class.javaObjectType
        ).get()
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
        //每次都运行一个任务,那么您可以指定它永远不会是最新的
        // incrementalTask.outputs.upToDateWhen {  false }
        //在项目配置结束之后,添加自定义的Task
        project.afterEvaluate {
            addExportForPackageManifestAfterEvaluate(it, exportTask)
//            testAddTaskAfterEvaluate(
//                it,
//                nonIncrementalTask//,
//                //nonIncrementalGlobalTask,
//                //incrementalTask
//            )
        }
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