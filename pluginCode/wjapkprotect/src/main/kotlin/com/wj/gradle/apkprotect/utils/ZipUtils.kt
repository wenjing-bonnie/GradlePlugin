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
     */
    fun unZip(zipFile: File, descDirPath: String) {

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        val buffer = ByteArray(1024)

        try {
            val zip = ZipFile(zipFile)
            val zipEntries = zip.entries()
            while (zipEntries.hasMoreElements()) {
                //获取zip文件的名字
                val zipEntry: ZipEntry = zipEntries.nextElement() as ZipEntry
                val zipEntryName = zipEntry.name
                //获取解压文件的路径
                inputStream = zip.getInputStream(zipEntry)
                val descFilePath = descDirPath + "/" + zipFile.name + "/" + zipEntryName
                val descFile = createFile(descFilePath)
                //读文件
                outputStream = FileOutputStream(descFile)
                var len: Int = inputStream.read(buffer)
                while (len > 0) {
                    outputStream.write(buffer, 0, len)
                    len = inputStream.read(buffer)
                }

                inputStream.close()
                outputStream.close()

            }
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

    /**
     * 压缩文件
     */
    fun zip(files: List<File>, zipFilePath: String) {
        if (files.isEmpty()) {
            return
        }

        val zipFile = createFile(zipFilePath)
        val buffer = ByteArray(1024)
        var zipOutputStream: ZipOutputStream? = null
        var inputStream: FileInputStream? = null

        try {
            zipOutputStream = ZipOutputStream(FileOutputStream(zipFile))
            for (it in files) {
                if (!it.exists()) {
                    continue
                }
                zipOutputStream.putNextEntry(ZipEntry(it.name))
                inputStream = FileInputStream(it)
                var len: Int = inputStream.read(buffer)
                while (len > 0) {
                    zipOutputStream.write(buffer, 0, len)
                }
                zipOutputStream.close()
            }

        } finally {
            inputStream?.close()
            zipOutputStream?.close()
        }
    }


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


    /**
     * test code
     */
    fun zip(project: Project) {
        val zipPath =
            project.projectDir.absolutePath + "/build/outputs/apk/huawei/debug/app-huawei-debug.apk"
        val descPath = project.projectDir.absolutePath + "/build"
        SystemPrint.outPrintln(zipPath)
        unZip(File(zipPath), descPath)
    }

}