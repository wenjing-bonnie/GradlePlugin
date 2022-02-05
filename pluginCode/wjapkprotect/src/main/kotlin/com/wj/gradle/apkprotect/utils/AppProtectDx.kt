package com.wj.gradle.apkprotect.utils

import com.android.build.gradle.AppExtension
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
     * 将aar转化成dex文件，默认的存放到build/protect/aar
     * @param aarFile aar的文件
     * @param project
     */
    fun jar2Dex(aarFile: File, project: Project): File {
        //1.获取默认的操作aar文件的路径
        val aarPath = AppProtectDefaultPath.getShellAarRootDirectory(project).absolutePath
        //2.解压aar文件到默认的存放aar的路径
        val aarUnzipPath = ZipAndUnZipApkUtils.unZipFile(aarFile, aarPath)
        val aarUnzipDirectory = File(aarUnzipPath)
        //3.找到里面的所有class.jar
        val classJars = aarUnzipDirectory.listFiles(object : FilenameFilter {
            override fun accept(p0: File?, p1: String?): Boolean {
                return p1.equals("classes.jar")
            }
        })
        if (classJars == null || classJars.isEmpty()) {
            throw IllegalArgumentException("The aar is invalid")
        }
        val classJar = classJars[0]
        //4.将class.jar转化成class.dex,存放到解压文件的同级目录
        val aarDex = File(aarUnzipDirectory.parent, "classes.dex")
        //5.执行build tools的dx
        dxCommand(aarDex, classJar, project)
        return aarDex
    }

    /**
     * 执行dx命令
     * 注意[在执行dx的时候，要加上绝对路径，配置dx的环境变量不起作用]
     */
    private fun dxCommand(aarDex: File, classJar: File, project: Project) {
        val dxTools = getDxBuildToolsPath(project)
        if (dxTools.isEmpty()) {
            throw RuntimeException("Can not find \"dx\" , make sure the project set sdk location right")
        }
        val runtime = Runtime.getRuntime()
        val process =
            runtime.exec("$dxTools/dx --dex --output=${aarDex.absolutePath} ${classJar.absolutePath}")
        SystemPrint.outPrintln(
            TAG,
            "$dxTools/dx --dex --output=${aarDex.absolutePath} ${classJar.absolutePath}"
        )
        try {
            process.waitFor()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val exitValue = process.exitValue()
        if (exitValue == 0) {
            SystemPrint.outPrintln(
                TAG,
                "The ${classJar.name} to ${aarDex.name} is finished in\n ${aarDex.parent}"
            )
            return
        }
        //错误信息输出
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

    }

    private fun getDxBuildToolsPath(project: Project): String {
        val androidExtension =
            project.extensions.findByType(AppExtension::class.javaObjectType)
                ?: return ""
        return "${androidExtension.sdkDirectory.absolutePath}/build-tools/${androidExtension.buildToolsVersion}"


    }
}