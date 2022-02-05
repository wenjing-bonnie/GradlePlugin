package com.wj.gradle.apkprotect.tasks.shellaar

import com.wj.gradle.apkprotect.tasks.base.NewIncrementalWithoutOutputsTask
import com.wj.gradle.apkprotect.utils.AppProtectDx
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

/**
 * 将aar转化成dex
 */
abstract class ShellAar2DexIncrementalTask : NewIncrementalWithoutOutputsTask() {

    @get:InputFile
    @get:Incremental
    abstract val shellAarFileProperty: RegularFileProperty

    override fun doTaskAction(inputChanges: InputChanges) {
        val shellAarFile = shellAarFileProperty.get().asFile
        AppProtectDx.jar2Dex(shellAarFile, project)
    }
}