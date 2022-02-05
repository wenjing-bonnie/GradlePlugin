package com.wj.gradle.apkprotect.utils

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.plugins.AppPlugin
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FilenameFilter
import java.lang.RuntimeException

/**
 * 将壳aar转化成.dex
 */
object AppProtectDx {

    private val TAG = javaClass.simpleName

    /**
     * 将aar转化成dex文件
     * @param aarFile aar的文件
     * @param project
     */
    fun jar2Dex(aarFile: File, project: Project): File {
        val aarPath = AppProtectDefaultPath.getAarRootDirectory(project).absolutePath
        val unzipDirectory = ZipAndUnZipApkUtils.unZipFile(aarFile, aarPath)
        return jar2Dex(aarFile, File(unzipDirectory), project)
    }

    /**
     * 将jar转化成dex文件
     * @param aarFile aar文件
     * @param aarUnzipDirectory aar文件解压之后的路径
     */
    private fun jar2Dex(aarFile: File, aarUnzipDirectory: File, project: Project): File {
        //找到里面的所有class.jar
        val classJars = aarUnzipDirectory.listFiles(object : FilenameFilter {
            override fun accept(p0: File?, p1: String?): Boolean {
                return p1.equals("classes.jar")
            }
        })
        if (classJars == null || classJars.isEmpty()) {
            throw IllegalArgumentException("The aar is invalid")
        }
        val classJar = classJars[0]
        //将class.jar转化成class.dex
        val aarDex = File(classJar.parent, "class.dex")
        //执行build tools的dx
        dxCommand(aarDex, classJar, project)
        return aarDex
    }

    /**
     * 执行dx命令
     * 需要配置环境变量
     */
    private fun dxCommand(aarDex: File, classJar: File, project: Project) {
        val dxTools = getDxBuildToolsPath(project)
        if (dxTools.isEmpty()) {
            throw RuntimeException("Can not find \"dx\" , make sure the project set sdk location right")
        }
        val runtime = Runtime.getRuntime()
        val process =
            runtime.exec("$dxTools/dx --dex --output=${aarDex.absolutePath} ${classJar.absolutePath}")
        try {
            process.waitFor()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        if (process.exitValue() != 0) {
            val inputStream = process.errorStream
            var len = -1
            var buffer = ByteArray(1024)
            val bos = ByteArrayOutputStream()
            len = inputStream.read(buffer)
            while (len != -1) {
                bos.write(buffer, 0, len)
                len = inputStream.read(buffer)
            }
            val error = "dx run failed , error is \n${String(bos.toByteArray(), Charsets.UTF_8)}"
            SystemPrint.errorPrintln(TAG, error)
        } else {
            SystemPrint.outPrintln(
                TAG,
                "The ${classJar.name} to ${aarDex.name} is finished in\n ${aarDex.parent}"
            )
        }
    }

    private fun getDxBuildToolsPath(project: Project): String {
        val androidExtension =
            project.extensions.findByType(AppExtension::class.javaObjectType)
                ?: return ""
        return "${androidExtension.sdkDirectory.absolutePath}/build-tools/${androidExtension.buildToolsVersion}"


    }
}