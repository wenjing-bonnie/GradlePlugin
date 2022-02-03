package com.wj.gradle.apkprotect.tasks.encode

import com.android.build.gradle.internal.tasks.NewIncrementalTask
import com.wj.gradle.apkprotect.tasks.encode.parallel.DecodeDexAction
import com.wj.gradle.apkprotect.tasks.encode.parallel.EncodeDexAction
import com.wj.gradle.apkprotect.tasks.encode.parallel.EncodeDexWorkParameters
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.gradle.workers.WorkQueue
import java.io.File

/**
 * 对apk里面解压之后的所有.dex进行加密
 */
abstract class EncodeDexIncrementalTask : NewIncrementalTask() {
    companion object {
        val TAG_ENCODE: String = "EncodeDexIncrementalTask"
        val TAG_DECODE: String = "DecodeDexIncrementalTask"
    }

    init {
        outputs.upToDateWhen { true }
    }

    @get:Incremental
    @get:InputDirectory
    abstract val dexDirectory: DirectoryProperty

    /**
     * true:encode;false:decode
     */
    var isEncodeTask: Boolean = true

    override fun doTaskAction(inputChanges: InputChanges) {
        SystemPrint.outPrintln(TAG_ENCODE, "The encode dex ...")
        val workQueue = workerExecutor.noIsolation()
        SystemPrint.outPrintln(TAG_ENCODE, dexDirectory.get().asFile.path)
        val dexParentFiles = dexDirectory.get().asFile.listFiles()
        for (dexParent in dexParentFiles) {
            //传过来的是生成压缩文件的根目录
            if (!dexParent.isDirectory) {
                continue
            }
            val dexFiles = dexParent.listFiles()
            //得到所有的.dex文件进行加密
            for (dex in dexFiles) {
                if (!dex.name.endsWith(".dex")) {
                    continue
                }
                if (isEncodeTask) {
                    encodeDexAction(workQueue, dex)
                } else {
                    decodeDexAction(workQueue, dex)
                }
            }
        }

    }

    private fun encodeDexAction(workQueue: WorkQueue, dex: File) {
        workQueue.submit(EncodeDexAction::class.javaObjectType) { it: EncodeDexWorkParameters ->
            it.dexFile.set(dex)
        }
        SystemPrint.outPrintln(TAG_ENCODE, "The ${dex.name} finished to encode .")
    }

    private fun decodeDexAction(workQueue: WorkQueue, dex: File) {
        workQueue.submit(DecodeDexAction::class.javaObjectType) { it: EncodeDexWorkParameters ->
            it.dexFile.set(dex)
        }
        SystemPrint.outPrintln(TAG_ENCODE, "The ${dex.name} finished to decode .")
    }
}