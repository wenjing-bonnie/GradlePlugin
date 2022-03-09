package com.wj.gradle.apkprotect.tasks.codedex.parallel

import com.wj.gradle.apkprotect.algroithm.duichen.AesFileAlgorithm
import org.gradle.workers.WorkAction
import java.io.File

/**
 * 加密Dex
 */
abstract class EncodeDexAction : WorkAction<EncodeDexWorkParameters> {
    val PRE = "en_"

    //    override fun execute() {
//        val dexFile = parameters.dexFile.get().asFile
//        val aesAlgorithm = AesFileAlgorithm()
//        //1.加密
//        val encodeFile = File(dexFile.parentFile, "$PRE${dexFile.name}")
//        aesAlgorithm.encrypt(dexFile, encodeFile)
//        dexFile.delete()
//        //3.rename
//        //encodeFile.renameTo(dexFile)
//    }
    //Test 仅用来测试
    override fun execute() {
        val dexFile = parameters.dexFile.get().asFile
        val encodeFile = File(dexFile.parentFile, "$PRE${dexFile.name}")
        //rename
        dexFile.renameTo(encodeFile)
    }
}