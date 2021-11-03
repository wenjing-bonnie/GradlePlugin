package com.wj.gradle.seniorapplication.tasks.lazy

import com.wj.gradle.manifest.utils.SystemPrint
import com.wj.gradle.seniorapplication.tasks.BaseTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

/**
 * Created by wenjing.liu on 2021/11/1 in J1.
 * 消费Task
 * TODO  当有超过2个的inputs或者outputs时@get:SkipWhenEmpty失效,超过2个加载属性会抛出异常
 * TODO  并且仅在全都配置值的时候才起作用, 如果Customer为NO-SOURCE不执行,producer也不执行
 *
 * @author wenjing.liu
 */
abstract class LazyConsumerTask : BaseTask() {

    companion object {
        const val TAG = "LazyConsumerTask"
    }

    init {
        outputs.upToDateWhen { true }
    }

    @get:SkipWhenEmpty
    @get:InputFile
    @get:Incremental
    abstract val testLazyInputFile: RegularFileProperty

    var conventionProperty: Property<String> =
        project.objects.property(String::class.javaObjectType)

    override fun incrementalTaskAction(inputChanges: InputChanges) {
        SystemPrint.outPrintln(
            TAG,
            "input file is \n" + testLazyInputFile.get().asFile.absolutePath
        )
        conventionProperty()
    }

    private fun conventionProperty() {
        conventionProperty.convention("set convention")
        SystemPrint.outPrintln(TAG, conventionProperty.get())
        conventionProperty.set("set new value")
        SystemPrint.outPrintln(TAG, conventionProperty.get())
    }

}