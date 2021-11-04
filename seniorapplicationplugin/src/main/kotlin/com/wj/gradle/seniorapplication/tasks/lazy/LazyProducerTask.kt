package com.wj.gradle.seniorapplication.tasks.lazy

import com.android.utils.FileUtils
import com.wj.gradle.manifest.utils.SystemPrint
import com.wj.gradle.seniorapplication.tasks.BaseTask
import com.wj.gradle.seniorapplication.tasks.parallel.gradle.CustomParallelTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File

/**
 * Created by wenjing.liu on 2021/11/1 in J1.
 * 生产Task
 * RegularFileProperty类型的必须通过.set()进行赋值，否则会抛出"> No value has been specified for property "
 * ConfigurableFileCollection可以不赋值
 * @author wenjing.liu
 */
abstract class LazyProducerTask : BaseTask() {

    companion object {
        const val TAG = "LazyProducerTask"
    }

    @get:SkipWhenEmpty
    @get:OutputFile
    @get:Incremental
    abstract val testLazyOutputFile: RegularFileProperty

    @get:InputFiles
    @get:SkipWhenEmpty
    @get:Incremental
    abstract val testInputFiles: ConfigurableFileCollection


    override fun incrementalTaskAction(inputChanges: InputChanges) {
        val buffer = StringBuffer()
        testInputFiles.asFileTree.files.forEach {
            SystemPrint.outPrintln(TAG, "The input file is ${it.name}")
            buffer.append(readContentFromInputs(it))
            buffer.append("\n")
            Thread.sleep(5000)
        }
        SystemPrint.outPrintln(
            TAG,
            "current thread name is ${Thread.currentThread().name}"
        )
        SystemPrint.outPrintln(TAG, "The final content :\n ${buffer}")
        FileUtils.writeToFile(testLazyOutputFile.get().asFile, buffer.toString())
    }

    private fun readContentFromInputs(inputFile: File?): String {
        var result = inputFile?.readText(Charsets.UTF_8).toString()
        return result
    }
}