package com.wj.gradle.apkprotect.utils

import java.io.File

/**
 * Created by wenjing.liu on 2022/1/21 in J1.
 *
 * 操作Dex文件
 * 实现解压、压缩、复制dex文件
 *
 * @author wenjing.liu
 */
object DexFileManager {


    fun zipApk(
        unZipApkFolder: File,
        zipFileDescPath: String
    ): File? {
      return  ZipUtils.zipFile(unZipApkFolder, zipFileDescPath, "apk")
    }
}

