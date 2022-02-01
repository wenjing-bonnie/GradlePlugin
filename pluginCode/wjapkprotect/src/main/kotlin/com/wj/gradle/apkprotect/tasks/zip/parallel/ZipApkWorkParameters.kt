package com.wj.gradle.apkprotect.tasks.zip.parallel

import org.gradle.api.file.DirectoryProperty
import org.gradle.workers.WorkParameters

interface ZipApkWorkParameters : WorkParameters {
    /**
     * 存放所有的解压之后的文件夹
     */
    val unzipApkDirectory: DirectoryProperty

    /**
     * 存放输出的apk文件的文件夹
     */
    val zipApkDirectory: DirectoryProperty
}