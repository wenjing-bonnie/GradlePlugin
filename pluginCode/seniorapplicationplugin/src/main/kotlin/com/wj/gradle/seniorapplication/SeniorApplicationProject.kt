package com.wj.gradle.seniorapplication

import com.android.build.gradle.AppExtension
import com.wj.gradle.base.WjVariantBaseProject
import com.wj.gradle.seniorapplication.extensions.SeniorApplicationKotlinExtension
import com.wj.gradle.seniorapplication.taskmanager.*
import com.wj.gradle.seniorapplication.utils.SystemPrint
import com.wj.gradle.seniorapplication.utils.TaskExecutionPrintListener
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
        //createConfiguration(p0)
        if (!getValidVariantNameInBuild(p0)) {
            return
        }
        //addTaskExecutionListener(p0)
        addAddCustomTransformTaskByExtension(p0)
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
     * 增加task执行时间的统计
     */
    private fun addTaskExecutionListener(project: Project) {
        project.gradle.addListener(TaskExecutionPrintListener())
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
            //TODO 在验证并行Tasks的时候,需要对下面两个方法分别进行显示或隐藏
            //addProducerTaskAfterEvaluate(it)
            addCustomParallelTaskAfterEvaluate(it)
        }
    }

    /**
     *增量编译的Task
     * @param project
     */
    private fun addIncrementalOnDefaultTaskAfterEvaluate(project: Project) {
        val incrementalManager = AddIncrementalTaskManager(project, variantName)
        incrementalManager.testIncrementalOnDefaultTask()
    }

    /**
     * lazy configuration task
     * @param project
     */
    private fun addLazyConfigurationTaskAfterEvaluate(project: Project) {
        val lazyManager = AddLazyConfigurationTaskManager(project, variantName)
        lazyManager.testAddLazyTaskDependsPreBuilder()
    }

    /**
     * 添加生产Task
     */
    private fun addProducerTaskAfterEvaluate(project: Project) {
        val lazyConfigurationManager = AddProducerTaskManager(project, variantName)
        lazyConfigurationManager.testAddTaskByLazyConfiguration()
    }

    /**
     * 添加并行Task
     */
    private fun addCustomParallelTaskAfterEvaluate(project: Project) {
        val customParallelTaskManager = AddCustomParallelTaskManager(project, variantName)
        //TODO 需要根据合适的例子来打开对应的方法进行打包发布
        //customParallelTaskManager.testCustomParallelTask()
        customParallelTaskManager.testCustomNewIncrementalTask()
        customParallelTaskManager.testClassLoaderIsolationTask()
    }

    /**
     * 添加自定义Transform
     */
    private fun addAddCustomTransformTaskByExtension(project: Project) {
        val customTransform = AddCustomTransformTaskManager(project, variantName)
        customTransform.testAddCustomTransformTask()
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

    private fun getValidVariantNameInExtension(project: Project) {

        val androidExtension = project.extensions.findByType(AppExtension::class.javaObjectType)
        if (androidExtension == null) {
            return
        }
        androidExtension.productFlavors.forEach {
            SystemPrint.outPrintln(it.name)
        }
    }

}