package com.wj.gradle.apkprotect.tasks.zip.parallel

import com.wj.gradle.apkprotect.utils.ZipAndUnZipApkUtils
import org.gradle.workers.WorkAction

/**
 * 将文件夹压缩成apk
 */
abstract class ZipApkAction : WorkAction<ZipApkWorkParameters> {

    override fun execute() {
        val unzipApkDirectory = parameters.unzipApkDirectory.get().asFile
        val zipApkDirectory = parameters.zipApkDirectory.get().asFile
        ZipAndUnZipApkUtils.zipApk(unzipApkDirectory, zipApkDirectory.path)
    }
}