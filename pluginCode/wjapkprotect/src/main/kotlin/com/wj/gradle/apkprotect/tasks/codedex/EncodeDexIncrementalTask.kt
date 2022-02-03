package com.wj.gradle.apkprotect.tasks.codedex

import com.wj.gradle.apkprotect.tasks.codedex.parallel.EncodeDexAction
import com.wj.gradle.apkprotect.tasks.codedex.parallel.EncodeDexWorkParameters
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.workers.WorkQueue
import java.io.File

/**
 * 对apk里面解压之后的所有.dex进行加密
 */
abstract class EncodeDexIncrementalTask : CodeDexTemplateIncrementalTask() {
    companion object{
        val TAG: String
            get() = "EncodeDexIncrementalTask"
    }

    override fun doFirst(action: Action<in Task>): Task {
        SystemPrint.outPrintln(TAG, "The encode dex ...")
        return super.doFirst(action)
    }

    override fun codeDexAction(workQueue: WorkQueue, dex: File) {
        workQueue.submit(EncodeDexAction::class.javaObjectType) { it: EncodeDexWorkParameters ->
            it.dexFile.set(dex)
        }
        SystemPrint.outPrintln(TAG, "The ${dex.name} finished to encode .")
    }
}