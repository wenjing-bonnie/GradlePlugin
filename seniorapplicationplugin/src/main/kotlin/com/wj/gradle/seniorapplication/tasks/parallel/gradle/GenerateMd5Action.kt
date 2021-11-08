package com.wj.gradle.seniorapplication.tasks.parallel.gradle

import com.wj.gradle.manifest.utils.SystemPrint
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.workers.WorkAction
import java.io.FileInputStream

/**
 * Created by wenjing.liu on 2021/11/8 in J1.
 * 采用md5加密
 * @author wenjing.liu
 */
abstract class GenerateMd5Action : WorkAction<CustomParallelParameters> {
    val TAG: String = "GenerateMd5Action"

    override fun execute() {
        val inputFile = parameters.testInputFile.get().asFile
        val inputStream = FileInputStream(inputFile)
        val outputFile = parameters.testLazyOutputFile.get().asFile

        Thread.sleep(5000)
        SystemPrint.outPrintln(
            TAG,
            "input name is " + inputFile.name + " \n output name is " + outputFile.name
        )
        val buffer = StringBuffer()
        buffer.append(inputFile.name)
        buffer.append("\n")
        buffer.append(DigestUtils.md5Hex(inputStream))
        buffer.append("\n")
        SystemPrint.outPrintln(TAG, "" + buffer)
        FileUtils.writeStringToFile(outputFile, buffer.toString(), Charsets.UTF_8, true)
    }
}