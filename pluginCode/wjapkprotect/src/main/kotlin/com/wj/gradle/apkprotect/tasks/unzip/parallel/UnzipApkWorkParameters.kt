package com.wj.gradle.apkprotect.tasks.unzip.parallel

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.workers.WorkParameters

interface UnzipApkWorkParameters : WorkParameters {
    /**
     * 需要解压的apk
     */
    val unzipApk: RegularFileProperty

    /**
     * 解压之后的文件存放的上级目录,以apk的名字存放解压之后的文件
     */
    val unzipDirectory: DirectoryProperty


}