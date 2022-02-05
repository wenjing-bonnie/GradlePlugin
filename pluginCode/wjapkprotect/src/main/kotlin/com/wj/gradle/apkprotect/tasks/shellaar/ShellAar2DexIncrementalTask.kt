package com.wj.gradle.apkprotect.tasks.shellaar

import com.android.build.gradle.internal.tasks.NewIncrementalTask
import com.wj.gradle.apkprotect.utils.AppProtectDefaultPath
import com.wj.gradle.apkprotect.utils.AppProtectDx
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

/**
 * 将aar转化成dex
 */
abstract class ShellAar2DexIncrementalTask : NewIncrementalTask() {

    @get:InputFile
    @get:Incremental
    abstract val shellAarFileProperty: RegularFileProperty

    override fun doTaskAction(inputChanges: InputChanges) {
        val shellAarFile = shellAarFileProperty.get().asFile
        AppProtectDx.jar2Dex(shellAarFile, project)
    }
}