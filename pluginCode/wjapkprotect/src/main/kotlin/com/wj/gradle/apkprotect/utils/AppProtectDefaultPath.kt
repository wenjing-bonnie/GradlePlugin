package com.wj.gradle.apkprotect.utils

import com.wj.gradle.apkprotect.extensions.ApkProtectExtension
import org.gradle.api.Project
import java.io.File
import java.util.regex.Pattern

/**
 * 默认的路径
 */
object AppProtectDefaultPath {

    /**
     *  从[ApkProtectExtension]中获取配置的解压文件夹，如果没有设置则使用默认值
     */
    fun getUnzipRootDirectoryBaseExtensions(project: Project): File {
        val defaultFile = getUnzipDefaultRootDirectory(project)
        val extension = project.extensions.findByType(ApkProtectExtension::class.javaObjectType)
            ?: return defaultFile
        if (extension.unzipDirectory.orNull == null) {
            return defaultFile
        }
        return extension.unzipDirectory.get().asFile
    }

    /**
     *  从[ApkProtectExtension]中获取配置的解压文件夹，如果没有设置则使用默认值
     */
    fun getApkDirectoryBaseExtensions(project: Project, variantName: String): File {
        val defaultFile = getApkDefaultDirectory(project, variantName)
        val extension = project.extensions.findByType(ApkProtectExtension::class.javaObjectType)
            ?: return defaultFile
        if (extension.apkDirectory.orNull == null) {
            return defaultFile
        }
        return extension.apkDirectory.get().asFile
    }

    /**
     * 默认的解压之后的apk存放的路径.
     */
    fun getUnzipDefaultRootDirectory(project: Project): File {
        val unzipPath = "${project.projectDir.absolutePath}/build/protect/"
        return createEmptyDirectory(project, unzipPath)
    }

    /**
     * 默认的.apk存放的路径.
     */
    fun getApkDefaultDirectory(project: Project, variantName: String): File {
        val defaultPath = getApkDefaultPath(project, variantName)
        return createEmptyDirectory(project, defaultPath)
    }

    /**
     * 默认的处理壳aar的根目录
     */
    fun getShellAarDefaultRootDirectory(project: Project): File {
        val aarPath = "${project.projectDir.absolutePath}/build/protect/aar"
        return createEmptyDirectory(project, aarPath)
    }

    /**
     * 获取android studio默认的debug/release的apk存放的路径
     */
    private fun getApkDefaultPath(project: Project, variantName: String): String {
        val rootPath = "${project.projectDir.absolutePath}/build/outputs/apk/"
        return if (getApkPathByVariantName(variantName).isEmpty()) {
            rootPath
        } else if (isDebugBuildType(variantName)) {
            "${rootPath}${getApkPathByVariantName(variantName)}/debug"
        } else if (isReleaseBuildType(variantName)) {
            "${rootPath}${getApkPathByVariantName(variantName)}/release"
        } else {
            rootPath
        }
    }


    /**
     * 从默认的debug/release的获取productFlavors
     * 因为在配置阶段就要调用该方法对[lazyApkDirectory]进行赋值,所以
     */
    private fun getApkPathByVariantName(variantName: String): String {
        var productFlavors = ""
        //SystemPrint.outPrintln(TAG, "variantName = " + variantName)
        val regex = if (isDebugBuildType(variantName)) {
            "(\\w+)Debug"
        } else {
            "(\\w+)Release"
        }
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(variantName)
        if (matcher.find()) {
            //group(0)整个字符串;group(1)第一个括号内的内容;group(2)第二个括号内的内容
            productFlavors = matcher.group(1)
        }
        //SystemPrint.outPrintln(TAG, "productFlavors = " + productFlavors)
        return productFlavors
    }

    /**
     * 创建一个空的文件夹
     */
    private fun createEmptyDirectory(project: Project, path: String): File {
        val directory = project.file(path)
        //SystemPrint.outPrintln("default = " + unzipDirectory.path)
        if (directory.exists()) {
            directory.delete()
        } else {
            directory.mkdirs()
        }
        return directory
    }

    /**
     * 是debug状态
     */
    private fun isDebugBuildType(variantName: String): Boolean {
        return variantName.endsWith("Debug")
    }

    /**
     * 是release状态
     */
    private fun isReleaseBuildType(variantName: String): Boolean {
        return variantName.endsWith("Release")
    }
}