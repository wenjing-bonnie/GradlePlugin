package com.wj.gradle.seniorapplication

import com.wj.gradle.manifest.taskmanager.AddLazyTaskDependsPreBuilderManager
import com.wj.gradle.manifest.utils.SystemPrint
import com.wj.gradle.seniorapplication.extensions.SeniorApplicationKotlinExtension
import com.wj.gradle.seniorapplication.taskmanager.AddIncrementalTaskDependsPreBuildManager
import com.wj.gradle.seniorapplication.taskmanager.AddTaskByLazyConfigurationManager
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.regex.Pattern

/**
 * Created by wenjing.liu on 2021/10/29 in J1.
 * 专门自定义Gradle 高级进阶
 * @author wenjing.liu
 */
open class SeniorApplicationProject : Plugin<Project> {

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
        SystemPrint.outPrintln("Welcome ${javaClass.simpleName}")
        addTasksForVariantAfterEvaluate(p0)
    }

    /**
     * 创建属性扩展
     * @param project
     */
    private fun createExtension(project: Project) {
        project.extensions.create(
            SeniorApplicationKotlinExtension.TAG,
            SeniorApplicationKotlinExtension::class.javaObjectType
        )
    }

    /**
     * 在项目配置完之后添加自定义的Task
     * @param project
     */
    private fun addTasksForVariantAfterEvaluate(project: Project) {

        //在项目配置结束之后,添加自定义的Task
        project.afterEvaluate {
            addIncrementalOnDefaultTaskAfterEvaluate(it)
            addLazyConfigurationTaskAfterEvaluate(it)
            addTaskByLazyConfigurationAfterEvaluate(it)
        }
    }

    /**
     *增量编译的Task
     * @param project
     */
    private fun addIncrementalOnDefaultTaskAfterEvaluate(project: Project) {
        val incrementalManager = AddIncrementalTaskDependsPreBuildManager(project, variantName)
        incrementalManager.testIncrementalOnDefaultTask()
    }

    /**
     * lazy configuration task
     * @param project
     */
    private fun addLazyConfigurationTaskAfterEvaluate(project: Project) {
        val lazyManager = AddLazyTaskDependsPreBuilderManager(project, variantName)
        lazyManager.testAddLazyTaskDependsPreBuilder()
    }

    private fun addTaskByLazyConfigurationAfterEvaluate(project: Project){
        val lazyConfigurationManager = AddTaskByLazyConfigurationManager(project,variantName)
        lazyConfigurationManager.testAddTaskByLazyConfiguration()
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