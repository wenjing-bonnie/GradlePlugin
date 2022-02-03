package com.wj.gradle.apkprotect.tasks.codedex

import com.wj.gradle.apkprotect.tasks.codedex.parallel.DecodeDexAction
import com.wj.gradle.apkprotect.tasks.codedex.parallel.EncodeDexWorkParameters
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.workers.WorkQueue
import java.io.File

/**
 * 对apk里面解压之后的所有.dex进行解密
 */
abstract class DecodeDexIncrementalTask : CodeDexTemplateIncrementalTask() {
    
    companion object{
        val TAG: String
            get() = "DecodeDexIncrementalTask"
    }

    override fun doFirst(action: Action<in Task>): Task {
        SystemPrint.outPrintln(TAG, "The decode dex ...")
        return super.doFirst(action)
    }

    override fun codeDexAction(workQueue: WorkQueue, dex: File) {
        workQueue.submit(DecodeDexAction::class.javaObjectType) { it: EncodeDexWorkParameters ->
            it.dexFile.set(dex)
        }
        SystemPrint.outPrintln(TAG, "The ${dex.name} finished to decode .")
    }
}