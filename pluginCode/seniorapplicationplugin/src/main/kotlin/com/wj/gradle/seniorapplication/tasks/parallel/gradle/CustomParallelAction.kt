package com.wj.gradle.seniorapplication.tasks.parallel.gradle

import com.android.utils.FileUtils
import com.wj.gradle.seniorapplication.utils.SystemPrint
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
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
    private val TAG: String = "CustomParallelAction"
    override fun execute() {
        SystemPrint.outPrintln(
            TAG,
            "current thread name is ${Thread.currentThread().name}"
        )
        val beginTime = System.currentTimeMillis()
        val inputFiles = parameters.testInputFile
        val outputFile: Provider<RegularFile> = parameters.testLazyOutputFile.map { it }
        singleIncrementalTaskAction(inputFiles, outputFile)
        val costTime = System.currentTimeMillis() - beginTime
        SystemPrint.outPrintln(TAG, "The cost time is ${costTime} ms")
    }

    private fun singleIncrementalTaskAction(
        testInputFile: RegularFileProperty,
        testLazyOutputFile: Provider<RegularFile>
    ) {
        val buffer = StringBuffer()
        buffer.append(readContentFromInputs(testInputFile.get().asFile))
        buffer.append("\n")
        //Artificially make this task slower.
        Thread.sleep(5000)
        FileUtils.writeToFile(testLazyOutputFile.get().asFile, buffer.toString())
    }

    private fun readContentFromInputs(inputFile: File): String {
        return inputFile.readText(Charsets.UTF_8)
    }
}