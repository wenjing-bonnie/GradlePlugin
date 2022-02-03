package com.wj.gradle.apkprotect.tasks.codedex.parallel

import com.wj.gradle.apkprotect.algroithm.duichen.AesFileAlgorithm
import org.gradle.workers.WorkAction

/**
 * 加密Dex
 */
abstract class EncodeDexAction : WorkAction<EncodeDexWorkParameters> {
    override fun execute() {
        val dexFile = parameters.dexFile.get().asFile
        val aesAlgorithm = AesFileAlgorithm()
        //SystemPrint.outPrintln("", "" + aesAlgorithm.getFileContent(dexFile))

        aesAlgorithm.encrypt(dexFile)
        // SystemPrint.outPrintln(aesAlgorithm.getFileContent(dexFile))

        // SystemPrint.errorPrintln("", aesAlgorithm.getFileContent(aesAlgorithm.decrypt(dexFile))?.toString())

    }
}