package com.wj.gradle.apkprotect.extensions

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory

/**
 * 扩展属性
 * 1.配置.apk存放的路径.默认的取["${project.projectDir.absolutePath}/build/outputs/apk/huawei/debug"]
 * 2.配置解压之后的apk存放的路径.默认取["${project.projectDir.absolutePath}/build/protect/]
 */
abstract class ApkProtectExtension {
    companion object {
        const val TAG: String = "apkProtectExtension"
    }

    /**
     * 需要加固的apk存放的路径
     */
    @get:InputDirectory
    @Deprecated("replace from packageDebug's outputDirectory")
    abstract val apkDirectory: DirectoryProperty

    /**
     * 解压之后的apk存放的路径
     */
    @get:InputDirectory
    @Deprecated("replace from apk output/unzip")
    abstract val unzipDirectory: DirectoryProperty

    /**
     * 壳aar的文件路径
     */
    @get:InputDirectory
    abstract val shellAarFile: RegularFileProperty

}