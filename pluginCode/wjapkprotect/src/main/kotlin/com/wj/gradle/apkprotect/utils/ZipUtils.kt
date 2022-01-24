package com.wj.gradle.apkprotect.utils

import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.Project
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Created by wenjing.liu on 2022/1/21 in J1.
 *
 * @author wenjing.liu
 */
object ZipUtils {

    /**
     * 解压缩
     * @param zipFile 需要解压的文件
     * @param descDirPath 解压文件存放的目录
     * @return 返回的是解压之后的文件夹的绝对路径
     */
    fun unZipApk(zipFile: File, descDirPath: String): String {

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        val buffer = ByteArray(1024)
        //目标的文件夹,以传入的文件名字来创建文件夹
        val resultFileAbsoluteDir = descDirPath + "/" + getApkName(zipFile)

        try {
            val zip = ZipFile(zipFile)
            val zipEntries = zip.entries()
            while (zipEntries.hasMoreElements()) {
                //获取zip文件的名字
                val zipEntry: ZipEntry = zipEntries.nextElement() as ZipEntry
                val zipEntryName = zipEntry.name
                //获取解压文件的路径
                inputStream = zip.getInputStream(zipEntry)
                //以文件的名字作为最外层的文件夹
                val descFilePath = resultFileAbsoluteDir + "/" + zipEntryName
                val descFile = createFile(descFilePath)
                //读文件
                outputStream = FileOutputStream(descFile)
                var len: Int = inputStream.read(buffer)
                while (len > 0) {
                    outputStream.write(buffer, 0, len)
                    len = inputStream.read(buffer)
                }
                //关闭流
                inputStream.close()
                outputStream.close()
            }
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
        SystemPrint.outPrintln("The ${zipFile.name} finished to unZip !")
        return resultFileAbsoluteDir
    }

    /**
     * @param unZipFiles 需要压缩的文件列表
     * @param zipFileDescPath 保存压缩之后的apk的路径
     * @param zipFileName 压缩之后的文件名,需要包含后缀名
     * @return 返回压缩文件保存的绝对路径
     */
    fun zipApk(
        unZipApkFolder: File,
        zipFileDescPath: String,
    ): File? {

        val zipFile = createFile("$zipFileDescPath/${unZipApkFolder.name}.zip")
        SystemPrint.outPrintln(zipFile.path)
        var zipOutputStream: ZipOutputStream? = null

        try {
            zipOutputStream = ZipOutputStream(FileOutputStream(zipFile))
            zip(unZipApkFolder, zipOutputStream, "")

        } finally {
            zipOutputStream?.close()
        }

        if (!zipFile.exists() || zipFile.length() == 0L) {
            return null
        }

        SystemPrint.outPrintln("The ${unZipApkFolder.name}.apk finished to zip !")
        return zipFile
    }

    /**
     * @param parent 第一次传入为"",里面若再有文件夹要加上父文件夹的名字
     */
    private fun zip(unZipFile: File, zipOutputStream: ZipOutputStream, parent: String) {
        var parentName = parent
        SystemPrint.outPrintln("parentName = " + parentName)
        SystemPrint.outPrintln("unZipFile = " + unZipFile.name + " , " + unZipFile.isDirectory)

        if (unZipFile.isDirectory) {
            val files = unZipFile.listFiles()
            //写入此目录的entry
            zipOutputStream.putNextEntry(ZipEntry("$parentName/"))
            if (parentName.isEmpty()) {
                parentName = ""
            } else {
                parentName = "$parentName/"
            }
            SystemPrint.outPrintln("size = " + files.size)
            for (file in files) {
                zip(file, zipOutputStream, parentName + file.name)
            }

        } else {
            try {
                zipOutputStream.putNextEntry(ZipEntry(parent))
                val buffer = ByteArray(1024)
                var inputStream = FileInputStream(unZipFile)
                var len: Int = inputStream.read(buffer)
                while (len > 0) {
                    zipOutputStream.write(buffer, 0, len)
                    len = inputStream.read(buffer)
                }
                zipOutputStream.closeEntry()

            } finally {

            }

        }
    }

    /**
     * 压缩文件
     * @param unZipFilesDir 需要压缩的文件夹
     * @param zipFileDescPath 保存压缩之后的apk的路径
     * @param suffix 压缩的文件后缀名
     * @return 返回压缩文件保存的绝对路径
     */


    /**
     * 创建File
     */
    private fun createFile(filePath: String): File {
        val file = File(filePath)
        val parentFile = file.parentFile!!
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    private fun getApkName(zipFile: File): String {
        return zipFile.name.substring(0, zipFile.name.lastIndexOf(".apk"))
    }


    /**
     * test code
     */
    fun zipApk(project: Project) {
        val zipPath =
            project.projectDir.absolutePath + "/build/outputs/apk/huawei/debug/app-huawei-debug.apk"
        val descPath = project.projectDir.absolutePath + "/build"
        SystemPrint.outPrintln(zipPath)
        val path = unZipApk(File(zipPath), descPath)
        SystemPrint.outPrintln("unzip is \n" + path)
        zipApk(File(path), descPath)
    }

}