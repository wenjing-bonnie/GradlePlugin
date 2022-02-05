package com.wj.gradle.apkprotect.utils

import com.wj.gradle.base.utils.SystemPrint
import org.gradle.api.Project
import java.io.*
import java.lang.IllegalArgumentException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Created by wenjing.liu on 2022/1/21 in J1.
 *
 * 压缩和解压缩apk文件的帮助类
 *
 * @author wenjing.liu
 */
object ZipAndUnZipApkUtils {
    private val TAG = javaClass.simpleName

    /**
     * 将文件夹压缩成.apk
     * @param unZipFiles 需要压缩的文件列表
     * @param zipFileDescPath 保存压缩之后的apk的路径
     * @param zipFileName 压缩之后的文件名,需要包含后缀名
     * @param zipSuffix 压缩的后缀名,默认为.apk
     * @return 返回压缩文件保存的绝对路径
     */
    fun zipApk(
        unZipApkFolder: File,
        zipFileDescPath: String
    ): File? {
        return zipFile(unZipApkFolder, zipFileDescPath, "apk")
    }

    /**
     * 将.apk解压缩成文件夹
     *
     * @param zipFile 需要解压的文件
     * @param descDirPath 解压文件存放的上级目录
     * @return 返回的是解压之后的文件夹的绝对路径
     */
    fun unZipApk(zipFile: File, descDirPath: String): String {
        if (!zipFile.name.endsWith(".apk")) {
            throw IllegalArgumentException("The zip file must end with .apk")
        }
        return unZipFile(zipFile, descDirPath)
    }

    /**
     * 解压缩成文件夹
     *
     * @param zipFile 需要解压的文件
     * @param descDirPath 解压文件存放的目录
     * @return 返回的是解压之后的文件夹的绝对路径
     */
    fun unZipFile(zipFile: File, descDirPath: String): String {
        if (!zipFile.exists()) {
            throw IllegalArgumentException("The zip file not exist !")
        }
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        val buffer = ByteArray(1024)
        //目标的文件夹,以传入的文件名字来创建文件夹
        val resultFileAbsoluteDir = descDirPath + "/" + getZipFileNameWithoutSuffix(zipFile)
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
                val descFilePath = "$resultFileAbsoluteDir/$zipEntryName"
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
        SystemPrint.outPrintln(TAG, "The ${zipFile.name} finish 'unzip' in \n$descDirPath ")
        return resultFileAbsoluteDir
    }

    /**
     * @param unZipFiles 需要压缩的文件列表
     * @param zipFileDescPath 保存压缩之后的apk的路径
     * @param zipFileName 压缩之后的文件名,需要包含后缀名
     * @param zipSuffix 压缩的后缀名,默认为.apk
     * @return 返回压缩文件保存的绝对路径
     */
    private fun zipFile(
        unZipApkFolder: File,
        zipFileDescPath: String,
        zipSuffix: String
    ): File? {

        val zipFile = createZipFile(unZipApkFolder, zipFileDescPath, zipSuffix)
        var zipOutputStream: ZipOutputStream? = null
        //压缩文件
        try {
            zipOutputStream = ZipOutputStream(FileOutputStream(zipFile))
            zip(unZipApkFolder, zipOutputStream, "")
        } finally {
            zipOutputStream?.close()
        }

        if (!zipFile.exists() || zipFile.length() == 0L) {
            return null
        }
        SystemPrint.outPrintln(TAG, "The ${zipFile.name} finish 'zip' in \n$zipFileDescPath")
        return zipFile
    }

    /**
     * 循环每个文件夹里面的文件进行通过ZipOutputStream进行压缩
     *
     * @param parent 第一次传入为"",里面若再有文件夹要加上父文件夹的名字
     */
    private fun zip(unZipFile: File, zipOutputStream: ZipOutputStream, parent: String) {
        var parentName = parent

        if (unZipFile.isDirectory) {
            val files = unZipFile.listFiles()
            //写入此目录的entry
            zipOutputStream.putNextEntry(ZipEntry("$parentName/"))
            if (parentName.isEmpty()) {
                parentName = ""
            } else {
                parentName = "$parentName/"
            }
            //要将所有的文件都要通过ZipOutputStream写入
            for (file in files) {
                zip(file, zipOutputStream, parentName + file.name)
            }
        } else {
            //压缩单个文件
            zipSingleFile(unZipFile, zipOutputStream, parent)
        }
    }

    /**
     * 压缩单个文件
     */
    private fun zipSingleFile(unZipFile: File, zipOutputStream: ZipOutputStream, parent: String) {
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

    /**
     * 创建最后的压缩文件
     */
    private fun createZipFile(
        unZipApkFolder: File,
        zipFileDescPath: String,
        zipSuffix: String
    ): File {
        return createFile("$zipFileDescPath/${unZipApkFolder.name}.${getZipFileSuffix(zipSuffix)}")
    }

    /**
     * 创建文件
     */
    private fun createFile(
        filePath: String,
    ): File {
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

    /**
     * 获取apk的名字
     */
    private fun getZipFileNameWithoutSuffix(zipFile: File): String {
        return zipFile.name.substring(0, zipFile.name.lastIndexOf("."))
    }

    /**
     * 获取压缩文件的后缀名
     */
    private fun getZipFileSuffix(zipSuffix: String): String {
        var zip = "apk"
        if (zipSuffix.isNotEmpty()) {
            zip = zipSuffix
        }
        return zip
    }

    private fun deleteFile(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * test code
     */
    fun zipFile(project: Project) {
        val zipPath =
            project.projectDir.absolutePath + "/build/outputs/apk/huawei/debug/app-huawei-debug.apk"
        val descPath = project.projectDir.absolutePath + "/build"
        SystemPrint.outPrintln(TAG, zipPath)
        val path = unZipApk(File(zipPath), descPath)
        SystemPrint.outPrintln(TAG, "unzip is \n" + path)
        zipApk(File(path), descPath)
    }
}