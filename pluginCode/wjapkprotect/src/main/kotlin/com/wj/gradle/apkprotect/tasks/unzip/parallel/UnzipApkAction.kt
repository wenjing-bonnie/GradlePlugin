package com.wj.gradle.apkprotect.tasks.unzip.parallel

import com.wj.gradle.apkprotect.utils.ZipAndUnZipApkUtils
import org.gradle.workers.WorkAction

/**
 * 解压每个.apk
 */
abstract class UnzipApkAction : WorkAction<UnzipApkWorkParameters> {

    override fun execute() {
        val unzipApk = parameters.unzipApk.get().asFile
        val unzipDirectory = parameters.unzipDirectory.get().asFile
        ZipAndUnZipApkUtils.unZipApk(unzipApk, unzipDirectory.path)
    }
}