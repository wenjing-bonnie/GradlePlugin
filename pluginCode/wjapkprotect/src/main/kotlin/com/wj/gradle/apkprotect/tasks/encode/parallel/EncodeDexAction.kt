package com.wj.gradle.apkprotect.tasks.encode.parallel

import com.wj.gradle.apkprotect.algroithm.duichen.AesFileAlgorithm
import org.gradle.workers.WorkAction

/**
 * 加密Dex
 */
abstract class EncodeDexAction : WorkAction<EncodeDexWorkParameters> {
    override fun execute() {
        val dexFile = parameters.dexFile.get().asFile
        val aesAlgorithm = AesFileAlgorithm()
        aesAlgorithm.encrypt(dexFile)
    }
}