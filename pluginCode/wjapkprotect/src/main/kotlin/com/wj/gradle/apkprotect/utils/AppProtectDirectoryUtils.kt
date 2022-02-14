package com.wj.gradle.apkprotect.utils

import com.android.build.gradle.tasks.PackageApplication
import com.wj.gradle.apkprotect.extensions.ApkProtectExtension
import org.gradle.api.Project
import java.io.File

/**
 * 默认的路径
 * 放到build目录下有个好处就是在rebuild的时候，可以删除重新生成
 */
object AppProtectDirectoryUtils {
    private val APKS_UNSIGNED = "apks-unsigned"
    private val APKS_SIGNED = "apks-signed"
    private val AAR = "aar"
    private val UNZIP = "unzip"

    /**
     *  从[ApkProtectExtension]中获取配置的解压文件夹，如果没有设置则使用默认值
     *  @param defaultApkOutput 默认的生成apk的目录
     */

    fun getUnzipRootDirectoryBaseExtensions(project: Project, variantName: String): File {
        val defaultFile = getUnzipDefaultRootDirectory(project, variantName)
        val extension = project.extensions.findByType(ApkProtectExtension::class.javaObjectType)
            ?: return defaultFile
        if (extension.unzipDirectory.orNull == null) {
            return defaultFile
        }
        return extension.unzipDirectory.get().asFile
    }

    /**
     * 存放压缩之后的无签名的apk
     */
    fun getUnsignedApkZipDirectoryFromUnzipDirectory(
        project: Project,
        variantName: String
    ): File {
        val unzipDirectory = getUnzipRootDirectoryBaseExtensions(project, variantName)
        return File(unzipDirectory, APKS_UNSIGNED)
    }

    /**
     * 存放压缩之后的签名的apk
     */
    fun getSignedApkZipDirectoryFromUnzipDirectory(project: Project, variantName: String): File {
        val unzipDirectory = getUnzipRootDirectoryBaseExtensions(project, variantName)
        return File(unzipDirectory, APKS_SIGNED)
    }

    /**
     * 默认的解压之后的apk存放的路径.
     * @param defaultApkOutput 默认的生成apk的目录
     */
    private fun getUnzipDefaultRootDirectory(project: Project, variantName: String): File {
        val defaultApkOutput = getDefaultApkOutput(project, variantName)
        val unzipPath = "${defaultApkOutput.absolutePath}/$UNZIP"
        return createEmptyDirectory(unzipPath)
    }

    /**
     * 获取Apk默认的路径
     */
    fun getDefaultApkOutput(project: Project, variantName: String): File {
        val packageTask = project.tasks.getByName("package$variantName") as PackageApplication
        return packageTask.outputDirectory.get().asFile
    }

    /**
     * 默认的处理壳aar的根目录
     */
    fun getShellAarDefaultRootDirectory(project: Project, variantName: String): File {
        val aarPath = "${getDefaultApkOutput(project, variantName)}/${AAR}"
        return createEmptyDirectory(aarPath)
    }

    /**
     * 判断是不是一个有效的apk解压之后的文件夹
     * 不能为'aar'、'apks-unsigned'、apks-signed
     */
    fun isValidApkUnzipDirectory(file: File?): Boolean {
        if (file == null) {
            return false
        }
        return file.isDirectory &&
                (!file.name.equals(AAR)) &&
                (!file.name.equals(APKS_UNSIGNED)) &&
                (!file.name.equals(APKS_SIGNED)) &&
                (!file.name.equals(UNZIP))
    }
    
    /**
     * 创建一个空的文件夹
     */
    private fun createEmptyDirectory(path: String): File {
        val directory = File(path)
        if (directory.exists()) {
            return directory
        } else {
            directory.mkdirs()
            return directory
        }
    }
}