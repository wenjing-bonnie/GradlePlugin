package com.wj.gradle.seniorapplication.tasks.lazy

import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.*
import org.gradle.api.tasks.*

/**
 * Created by wenjing.liu on 2021/10/26 in J1.
 * 测试懒加载
 * 作为producer
 * TODO 对扩展属性的优化！！！
 * https://docs.gradle.org/current/userguide/lazy_configuration.html#working_with_files_in_lazy_properties
 * @author wenjing.liu
 */
abstract class LazyConfigurationTask : DefaultTask() {
    companion object {
        const val TAG = "LazyConfigurationTask"
    }

    init {
    }


    //这种方式无法获取到analyticsService，报错信息如下：
    // Cannot query the value of task ':app:LazyConfigurationTask' property 'analyticsService' because it has no value available.
    // 解决方案：必须在实例化该实例的时候，对该属性赋值
//    @get:Internal
//    abstract val analyticsService: Property<AnalyticsService>
    //这种方式无法获取到analyticsService，报错信息如下：
//    //Cannot query the value of this property because it has no value available.
//    var analyticsService: Property<AnalyticsService> =
//        project.objects.property(AnalyticsService::class.javaObjectType)

    /**
     *  第一种方式：通过@get:xxx注解的方式
     *
     *  注意一定要用val进行修饰，否则会抛出java.lang.NullPointerException
     */
    @get:Input
    abstract val testProperty: Property<String>

    @Internal
    var testProvider: Provider<String> = testProperty.map {
        "${it} from property"
    }

    /**
     *  第二种：通过ObjectFactory.property(Class)直接创建一个实例,其中Project.getObjects获取ObjectFactory
     */
    var testObjectFactory: Property<String> = project.objects.property(String::class.javaObjectType)

    //特殊的Property.
    //第一种：通过@get:xxx
    @get:InputDirectory
    abstract val testDirectoryProperty: DirectoryProperty

    //第二种:通过ObjectFactory
    var testRegularFileProperty: RegularFileProperty = project.objects.fileProperty()

    //跟File相关的
    @get:InputFiles
    abstract val testFileCollection: ConfigurableFileCollection

//    @get:OutputFiles
//    var testFileTreeProperty: ConfigurableFileTree = project.objects.fileTree()

    //集合相关
    @get:Internal
    abstract val testListProperty: ListProperty<String>

    var testSetProperty: SetProperty<String> =
        project.objects.setProperty(String::class.javaObjectType)

    //maps相关
    var testMapsProperty: MapProperty<String, Int> =
        project.objects.mapProperty(String::class.javaObjectType, Int::class.javaObjectType)


    @TaskAction
    open fun runTaskAction() {
        SystemPrint.outPrintln(TAG, "running !!!!")
        // SystemPrint.outPrintln(TAG, "test analytics \n" + analyticsService.get())
        printGenericProperty()
        printFileProperty()
        printCollectionProperty()
        printMapsProperty()
    }

    private fun printGenericProperty() {
        SystemPrint.outPrintln(TAG, "test Property is " + testProperty.get())
        SystemPrint.outPrintln(TAG, "test Provider is " + testProvider.get())
        SystemPrint.outPrintln(TAG, "test ObjectFactory is " + testObjectFactory.get())
    }

    private fun printFileProperty() {
        SystemPrint.outPrintln(
            TAG,
            "test DirectoryProperty is = " + testDirectoryProperty.get().asFile.absolutePath
        )
        SystemPrint.outPrintln(
            TAG,
            "test RegularFilePropertyFromFactory is = " + testRegularFileProperty.get().asFile.absolutePath
        )
    }

    private fun printCollectionProperty() {
        testListProperty.get().forEach {
            SystemPrint.outPrintln(TAG, "${it} in list property")
        }
        testSetProperty.get().forEach {
            SystemPrint.outPrintln(TAG, "${it} in set property")

        }
    }

    private fun printMapsProperty() {
        testMapsProperty.get().forEach { key, value ->
            SystemPrint.outPrintln(
                TAG,
                "key is ${key} , value is ${value}"
            )
        }
    }
}