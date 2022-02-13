package com.wj.gradle.apkprotect.tasks.shellaar

import com.wj.gradle.apkprotect.tasks.base.NewIncrementalWithoutOutputsTask
import com.wj.gradle.apkprotect.utils.AppProtectDirectoryUtils
import com.wj.gradle.apkprotect.utils.AppProtectShellAarUtils
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

/**
 * 将aar转化成dex
 */
abstract class ShellAar2DexIncrementalTask : NewIncrementalWithoutOutputsTask() {
    companion object {
        val TAG = "ShellAar2DexIncrementalTask"
    }

    @get:InputFile
    @get:Incremental
    abstract val shellAarFileProperty: RegularFileProperty

    override fun doTaskAction(inputChanges: InputChanges) {
        val shellAarFile = shellAarFileProperty.get().asFile
        //将解密.aar转化成解密.dex
        val dexFile = AppProtectShellAarUtils.jar2Dex(shellAarFile, project,variantName)
        //将解密.dex拷贝到所有的解压apk的文件夹下
        AppProtectShellAarUtils.copyDex2UnzipApkDirectory(
            dexFile,
            AppProtectDirectoryUtils.getUnzipRootDirectoryBaseExtensions(project, variantName),
            project
        )
    }
}