package com.wj.gradle.apkprotect.tasks.encode

import com.android.build.gradle.internal.tasks.NewIncrementalTask
import com.wj.gradle.apkprotect.tasks.encode.parallel.EncodeDexAction
import com.wj.gradle.apkprotect.tasks.encode.parallel.EncodeDexWorkParameters
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

/**
 * 对apk里面解压之后的所有.dex进行加密
 */
abstract class EncodeDexIncrementalTask : NewIncrementalTask() {
    companion object {
        val TAG: String = "EncodeDexIncrementalTask"
    }

    init {
        outputs.upToDateWhen { true }
    }

    @get:Incremental
    @get:InputDirectory
    abstract val dexDirectory: DirectoryProperty

    override fun doTaskAction(inputChanges: InputChanges) {
        SystemPrint.outPrintln(TAG, "The encode dex ...")
        val workQueue = workerExecutor.noIsolation()
        SystemPrint.outPrintln(TAG, dexDirectory.get().asFile.path)
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
                workQueue.submit(EncodeDexAction::class.javaObjectType) { it: EncodeDexWorkParameters ->
                    it.dexFile.set(dex)
                }
                SystemPrint.outPrintln(TAG, "The ${dex.name} finished to encode .")
            }


        }

    }
}