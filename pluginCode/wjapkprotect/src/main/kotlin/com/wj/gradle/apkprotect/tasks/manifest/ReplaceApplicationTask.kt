package com.wj.gradle.apkprotect.tasks.manifest

import com.wj.gradle.apkprotect.tasks.base.NewIncrementalWithoutOutputsTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFile
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

/**
 * created by wenjing.liu at 2022/2/12
 * 将原App的Application替换A成壳的Application
 * 依赖于processDebugManifest：为所有的变体生成最终的Manifest
 */
abstract class ReplaceApplicationTask : NewIncrementalWithoutOutputsTask() {

    @get:InputFile
    @get:Incremental
    abstract val mergedManifestFile: RegularFileProperty
    /**
     * 解密壳的application的名字
     */
    abstract val shellApplicationName: Property<String>


    override fun doTaskAction(inputChanges: InputChanges) {
        TODO("Not yet implemented")
    }
}