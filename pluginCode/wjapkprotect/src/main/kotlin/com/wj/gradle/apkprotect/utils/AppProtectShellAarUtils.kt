package com.wj.gradle.apkprotect.utils

import com.android.build.gradle.AppExtension
import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.Project
import java.io.File
import java.io.FileFilter
import java.io.FilenameFilter
import java.lang.RuntimeException

/**
 * 将壳aar转化成.dex
 */
object AppProtectShellAarUtils {

    private val TAG = javaClass.simpleName
    private val SHELL_DEX = "shell.dex"

    /**
     * 将aar转化成dex文件，默认的存放到build/protect/aar
     * @param aarFile aar的文件
     * @param project
     * @return 返回生成的dex文件
     */
    fun jar2Dex(aarFile: File, project: Project, variantName: String): File {
        //1.获取默认的操作aar文件的路径
        val aarPath =
            AppProtectDirectoryUtils.getShellAarDefaultRootDirectory(
                project,
                variantName
            ).absolutePath
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
        val aarDex = File(aarUnzipDirectory.parent, SHELL_DEX)
        //5.执行build tools的dx
        dxCommand(aarDex, classJar, project)
        return aarDex
    }

    /**
     * 将壳.dex拷贝到每个解压的文件夹里面
     */
    fun copyDex2UnzipApkDirectory(dexFile: File, unzipDirectory: File, project: Project) {
        val allApks = unzipDirectory.listFiles(object : FileFilter {
            override fun accept(p0: File?): Boolean {
                //去除本身
                return AppProtectDirectoryUtils.isValidApkUnzipDirectory(p0)
            }
        })
        for (apk in allApks) {
            val copyCommand = "cp ${dexFile.absolutePath} ${apk.absolutePath}"
            val runtimeUtils = AppProtectRuntimeUtils()
            val error = runtimeUtils.runtimeExecCommand(copyCommand)
            val okValue = "Finished to 'copy' \n ${dexFile.absolutePath}\n to \n${apk.absolutePath}"
            printRuntimeResult(error, okValue)
        }
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
        val error = runtime.runtimeExecCommand(command)
        printRuntimeResult(
            error,
            "Finished to 'dx' ${classJar.name} to ${aarDex.name} in\n ${aarDex.parent}"
        )
    }

    /**
     * 打印运行结果
     */
    private fun printRuntimeResult(error: String, okValue: String) {
        if (error.isEmpty()) {
            SystemPrint.outPrintln(
                TAG,
                okValue
            )
            return
        }
        //错误信息输出
        SystemPrint.errorPrintln(TAG, error)
    }

    private fun getDxBuildToolsPath(project: Project): String {
        val androidExtension =
            project.extensions.findByType(AppExtension::class.javaObjectType)
                ?: return ""
        return "${androidExtension.sdkDirectory.absolutePath}/build-tools/${androidExtension.buildToolsVersion}"
    }
}