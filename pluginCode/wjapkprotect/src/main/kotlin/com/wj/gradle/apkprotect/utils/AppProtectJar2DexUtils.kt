package com.wj.gradle.apkprotect.utils

import com.android.build.gradle.AppExtension
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.Project
import java.io.File
import java.io.FilenameFilter
import java.lang.RuntimeException

/**
 * 将壳aar转化成.dex
 */
object AppProtectJar2DexUtils {

    private val TAG = javaClass.simpleName

    /**
     * 将aar转化成dex文件，默认的存放到build/protect/aar
     * @param aarFile aar的文件
     * @param project
     * @return 返回生成的dex文件
     */
    fun jar2Dex(aarFile: File, project: Project): File {
        //1.获取默认的操作aar文件的路径
        val aarPath = AppProtectProcessDirectory.getShellAarDefaultRootDirectory(project).absolutePath
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
        val runtime = AppProtectRuntimeUtils()
        val command = "$dxTools/dx --dex --output=${aarDex.absolutePath} ${classJar.absolutePath}"
        val exitValue = runtime.runtimeExecCommand(command)
        if (exitValue.isEmpty()) {
            SystemPrint.outPrintln(
                TAG,
                "The ${classJar.name} to ${aarDex.name} is finished in\n ${aarDex.parent}"
            )
            return
        }
        //错误信息输出
        SystemPrint.errorPrintln(TAG, exitValue)

    }

    private fun getDxBuildToolsPath(project: Project): String {
        val androidExtension =
            project.extensions.findByType(AppExtension::class.javaObjectType)
                ?: return ""
        return "${androidExtension.sdkDirectory.absolutePath}/build-tools/${androidExtension.buildToolsVersion}"
    }
}