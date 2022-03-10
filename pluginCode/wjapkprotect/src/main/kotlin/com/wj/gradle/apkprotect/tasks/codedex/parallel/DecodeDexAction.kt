package com.wj.gradle.apkprotect.tasks.codedex.parallel

import com.wj.gradle.apkprotect.algroithm.duichen.AesFileAlgorithm
import org.gradle.workers.WorkAction

/**
 * 解密.dex
 */
abstract class DecodeDexAction : WorkAction<EncodeDexWorkParameters> {
    override fun execute() {
        val dexFile = parameters.dexFile.get().asFile
        val aesFileAlgorithm = AesFileAlgorithm()
       // aesFileAlgorithm.decrypt(dexFile, dexFile)
    }
}