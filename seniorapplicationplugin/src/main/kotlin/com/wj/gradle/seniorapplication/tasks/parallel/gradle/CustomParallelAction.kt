package com.wj.gradle.seniorapplication.tasks.parallel.gradle

import com.android.utils.FileUtils
import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.workers.WorkAction
import java.io.File

/**
 * Created by wenjing.liu on 2021/11/4 in J1.
 *
 * 并行action
 *
 * @author wenjing.liu
 */
abstract class CustomParallelAction : WorkAction<CustomParallelParameters> {

    override fun execute() {
        SystemPrint.outPrintln(
            CustomParallelTask.TAG,
            "current thread name is ${Thread.currentThread().name}"
        )
        val inputFiles = parameters.testInputFiles
        val outputFile: Provider<RegularFile> = parameters.testLazyOutputFile.map { it }
        incrementalTaskAction(inputFiles, outputFile)
    }

    private fun incrementalTaskAction(
        testInputFiles: ConfigurableFileCollection,
        testLazyOutputFile: Provider<RegularFile>
    ) {
        val buffer = StringBuffer()
        testInputFiles.asFileTree.files.forEach {
            SystemPrint.outPrintln(CustomParallelTask.TAG, "The input file is ${it.name}")
            buffer.append(readContentFromInputs(it))
            buffer.append("\n")
            Thread.sleep(5000)
        }
        SystemPrint.outPrintln(CustomParallelTask.TAG, "The final content :\n ${buffer}")
        FileUtils.writeToFile(testLazyOutputFile.get().asFile, buffer.toString())
    }

    private fun readContentFromInputs(inputFile: File?): String {
        var result = inputFile?.readText(Charsets.UTF_8).toString()
        return result
    }
}