package com.wj.gradle.base

import com.android.build.api.transform.Transform
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.profile.AnalyticsService
import com.android.build.gradle.internal.tasks.AndroidVariantTask
import com.wj.gradle.base.tasks.TaskWrapperGeneric
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.regex.Pattern
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.provider.Provider

/**
 * Created by wenjing.liu on 2021/10/29 in J1.
 *
 * 一、自定义Gradle Plugin的基工程:
 * [优势：可快速创建可基于当前variant的创建Task的Project]
 *
 * 二、使用方式
 * 1.在自定义Gradle Plugin的工程的build.gradle下添加如下代码：
 * repositories {
 *     //配置自定义插件的maven依赖。闭包的调用
 *      maven {
 *            //引用本地gradle的目录
 *         url uri('../../plugins')
 *      }
 * }
 * dependencies {
 *     //引用本地gradle
 *      implementation 'com.wj.gradle.plugins:variantbaseplugin:1.0.0'
 * }
 * 2.在自定义Gradle Plugin的工程的新建类继承{@link WjVariantBaseProject}
 *
 * 通过上述两个步骤就可以快速的创建一个添加Task的工程
 *
 * TODO 后面考虑怎么把build.gradle里面配置的内容添加到这个里面
 *
 * @author wenjing.liu
 */
abstract class WjVariantBaseProject : Plugin<Project> {

    /**
     * variant name
     */
    private var variantName: String = ""

    /**
     * 并行Task的必须设置的属性
     */
    private lateinit var analyticsService: Provider<AnalyticsService>

    private lateinit var androidExtension: AppExtension

    /**
     * TODO 暂定该方法不可复写
     */
    final override fun apply(p0: Project) {
        resetGlobalTag("base-${javaClass.simpleName}")
        initAnalyticsService(p0)
        //在配置扩展属性的时候,一定要保证无论什么情况都可以调用到.像如果把该方法移到if之后,则会始终找不到配置的扩展属性
        createExtension(p0)
        if (!getValidVariantNameInBuild(p0)) {
            return
        }
        addTransformTaskByExtension(p0)
        SystemPrint.outPrintln("Welcome ${javaClass.simpleName}")
        addTasksForBuildVariantAfterEvaluate(p0)
        applyExceptRegister(p0)
    }


    /**
     * 创建属性扩展
     * 考虑到并不是所有的gradle都需要扩展属性，所以该方法不做抽象
     * @param project
     */
    open fun createExtension(project: Project) {
    }

    /**
     * 设置全局的Tag标识,默认为
     * TODO 这里还没有验证成功
     */
    open fun resetGlobalTag(tag: String) {
        SystemPrint.TAG = tag
    }

    /**
     * 供子工程进行在apply()中添加相应的代码
     */
    open fun applyExceptRegister(project: Project) {

    }

    /**
     * 在项目配置完之后添加自定义的Task。
     *
     * 如果没有特殊要求，凡是继承自{@ DefaultTask} 、{@IncrementalTask}、{@link NonIncrementalTask}等都是在项目配置完成之后添加Task
     *
     * 注意：虽然并不是所有的plugin都存在这种类型的Task，但仍然需要重载，如果无该类型的Task返回一个空集合即可。
     */
    abstract fun getAfterEvaluateTasks(project: Project): MutableList<TaskWrapperGeneric<out Task>>

    /**
     * 继承自{@ Transform}的Task必须在apply()开始的时候就要添加Task
     *
     * 所有继承自{@ Transform}的Task需要添加到该plugin都要通过该方法返回
     *
     * 注意：虽然并不是所有的plugin都存在这种类型的Task，但仍然需要重载，如果无该类型的Task返回一个空集合即可。
     */
    abstract fun getRegisterTransformTasks(project: Project): MutableList<Transform>


    /**
     * 获取当前的variant的name
     */
    open fun getVariantName(): String {
        return variantName
    }

    /**
     * 获取android{}
     */
    open fun getAndroidExtension(): AppExtension {
        return androidExtension
    }

    /**
     * 获取注册的extension
     */
    open fun <T> getCreatedExtension(project: Project, clss: Class<T>): T? {
        return project.extensions.findByType(clss)
    }

    private fun initAnalyticsService(project: Project) {
        analyticsService = AnalyticsService.RegistrationAction(project).execute()
    }

    /**
     * 因为这个task是在项目构建之前添加到项目中的，而extension只有在项目构建后才能得到
     * 所以这里将传入project，在task中取得配置的内容
     */
    private fun addTransformTaskByExtension(project: Project) {
        androidExtension =
            project.extensions.findByType(AppExtension::class.javaObjectType) ?: return
        val transforms = getRegisterTransformTasks(project)
        //循环取出Transform添加到project中
        transforms.forEach {
            androidExtension.registerTransform(it)
        }
    }

    /**
     * 在项目配置完之后添加自定义的Task
     * @param project
     */
    private fun addTasksForBuildVariantAfterEvaluate(project: Project) {
        //在项目配置结束之后,添加自定义的Task
        project.afterEvaluate {
            val tasks = getAfterEvaluateTasks(project)
            //循环取出Task添加到project中
            tasks.forEach {
                registerEveryTaskAfterEvaluate(project, it)
            }
        }
    }

    /**
     * 为每个Task注册到project中
     */
    private fun <IRunTask : Task> registerEveryTaskAfterEvaluate(
        project: Project,
        wrapper: TaskWrapperGeneric<IRunTask>
    ) {
        SystemPrint.outPrintln(wrapper.toString())
        //要执行的Task，也是生产-消费Task中的消费Task,最终添加到项目依赖
        val provider =
            project.tasks.register(
                wrapper.willRunTaskTag,
                wrapper.willRunTaskClass
            )
        val dependsOnTask = project.tasks.getByPath(wrapper.anchorTaskName)
        var producerTaskProvider: TaskProvider<out Task>? = null
        if (wrapper.isConsumerProducerTask()) {
            producerTaskProvider =
                project.tasks.register(
                    wrapper.producerTaskTag,
                    wrapper.producerTaskClass
                )
        }

        if (wrapper.isDependsOn) {
            dependsOnTask.dependsOn(provider.get())
        } else {
            dependsOnTask.finalizedBy(provider.get())
        }
        //自动为Task绑定variantName
        initAndroidVariantTask(provider)
        //生产Task
        initAndroidVariantTask(producerTaskProvider)

        //回调返回每个Task实例
        wrapper.willRunTaskRegisterListener?.let {
            it.willRunTaskRegistered(provider, producerTaskProvider)
        }
    }

    /**
     * 初始化AndroidVariantTask的[AndroidVariantTask#variantName]和[AndroidVariantTask#analyticsService]
     */
    private fun <ITask : Task> initAndroidVariantTask(provider: TaskProvider<ITask>?) {
        //takeIf: 返回true,返回it本身，否则返回false
        provider?.takeIf {
            it.get() is AndroidVariantTask
        }?.let {
            (provider.get() as AndroidVariantTask).variantName = variantName
            (provider.get() as AndroidVariantTask).analyticsService.set(analyticsService)
        }
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


    /**
     * 添加repositories
     */
//    private fun addRepositories(project: Project) {
//        addMavenCentralToRepositories(project)
//        addThisBaseProjectToRepositories(project)
//    }

    /**
     * 将本工程添加到repositories中
     */
//    private fun addThisBaseProjectToRepositories(project: Project) {
//        val path = getThisBaseProjectMavenUrl()
//        if (path == null || path.isEmpty()) {
//            return
//        }
//        project.repositories.maven {
//            it.url = project.uri(path)
//        }
//    }


    /**
     * 添加mavenCentral()
     */
//    private fun addMavenCentralToRepositories(project: Project) {
//        project.repositories.mavenCentral()
//    }

}