package com.wj.gradle.apkprotect.algroithm.duichen

import org.bouncycastle.util.encoders.Base64
import java.io.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream

/**
 * Created by wenjing.liu on 2022/1/21 in J1.
 *
 * 使用AES对文件进行加密和解密
 *
 * @author wenjing.liu
 */
open class AesFileAlgorithm : AbstractAesAlgorithm() {

    override fun getPassword(): String {
        //AES密码必须为16位
        return "1234567890123456"
    }

    open fun encrypt(sourceFile: File, encryptFile: File): File? {
        val contentByte = getFileContent(sourceFile)
        val aesCipher = getAesCipher(Cipher.ENCRYPT_MODE)
        val encryptByte = aesCipher.doFinal(contentByte)
        val base64 = Base64.encode(encryptByte)
        val outputStream = FileOutputStream(encryptFile)
        outputStream.write(base64)
        outputStream.close()
        return encryptFile

    }

    /**
     * 对文件进行 AES 加密
     * @return 返回的是加密之后的文件
     */
//    open fun encrypt(sourceFile: File, encryptFile: File): File? {
//        var inputStream: FileInputStream? = null
//        var outputStream: FileOutputStream? = null
//        //val encryptFile = File.createTempFile(sourceFile.name, getFileSuffix(sourceFile.path),sourceFile.parentFile)
//
//        //新建临时加密文件
//        try {
//            inputStream = FileInputStream(sourceFile)
//            outputStream = FileOutputStream(encryptFile)
//
//           // val
//
//            val cipherInputStream =
//                CipherInputStream(inputStream, getAesCipher(Cipher.ENCRYPT_MODE))
//            val cache = ByteArray(1024)
//            var lineByte: Int = cipherInputStream.read(cache)
//            while (lineByte > 0) {
//                outputStream.write(cache, 0, lineByte)
//                lineByte = cipherInputStream.read(cache)
//            }
//            cipherInputStream.close()
//
//        } catch (e: Exception) {
//
//        } finally {
//            try {
//                inputStream?.close()
//                outputStream?.close()
//            } catch (e: Exception) {
//
//            }
//        }
//        return encryptFile
//    }

    /**
     * 解密
     * @return 返回解密之后的文件
     */
    open fun decrypt(encodeFile: File, decryptFile: File): File? {
        var inputStream: FileInputStream? = null
        var outputStream: FileOutputStream? = null
        //val decryptFile = File.createTempFile(encodeFile.name, getFileSuffix(encodeFile.path))

        try {
            // SystemPrint.outPrintln(getFileSuffix(encodeFile.path))
            inputStream = FileInputStream(encodeFile)
            outputStream = FileOutputStream(decryptFile)

            val cipherOutputStream =
                CipherOutputStream(outputStream, getAesCipher(Cipher.DECRYPT_MODE))
            val cache = ByteArray(1024)
            var lineByte = inputStream.read(cache)
            while (lineByte > 0) {
                cipherOutputStream.write(cache, 0, lineByte)
                lineByte = inputStream.read(cache)
            }
            cipherOutputStream.close()

        } catch (e: Exception) {

        } finally {
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e: Exception) {
            }

        }
        return decryptFile
    }


    /**
     * 获取到文件内容
     */
    open fun getFileContent(file: File?): ByteArray {
        if (file == null) {
            return ByteArray(0)
        }
        var bufferReader: BufferedReader? = null
        var buffer = StringBuffer()
        try {
            bufferReader = BufferedReader(FileReader(file))
            var content = bufferReader.readLine()
            while (content != null) {
                buffer.append(content)
                buffer.append("\n")
                content = bufferReader.readLine()
            }
            return buffer.toString().toByteArray(Charsets.UTF_8)
        } catch (e: Exception) {

        } finally {
            try {
                bufferReader?.close()
            } catch (e: Exception) {
            }

        }
        return ByteArray(0)
    }

    /**
     * 获取到文件的后缀名
     */
    private fun getFileSuffix(sourceFilePath: String): String {
        // suffix
        return sourceFilePath.substring(sourceFilePath.lastIndexOf("."))
    }

}