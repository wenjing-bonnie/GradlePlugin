package com.wj.gradle.apkprotect.tasks.codedex

import com.wj.gradle.apkprotect.tasks.base.NewIncrementalWithoutOutputsTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.gradle.workers.WorkQueue
import java.io.File

/**
 * 对apk里面解压之后的所有.dex进行加密
 */
abstract class CodeDexTemplateIncrementalTask : NewIncrementalWithoutOutputsTask() {

    @get:Incremental
    @get:InputDirectory
    abstract val dexDirectory: DirectoryProperty

    /**
     * 处理对应的dex的action
     */
    abstract fun codeDexAction(workQueue: WorkQueue, dex: File)

    override fun doTaskAction(inputChanges: InputChanges) {
        val workQueue = workerExecutor.noIsolation()
       // SystemPrint.outPrintln(TAG, dexDirectory.get().asFile.path)
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
                codeDexAction(workQueue, dex)
            }
        }
    }

}