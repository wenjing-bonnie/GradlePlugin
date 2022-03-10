package com.wj.gradle.apkprotect.algroithm.duichen

import com.wj.gradle.base.utils.SystemPrint
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

//    open fun encrypt(sourceFile: File, encryptFile: File): File? {
//        val contentByte = getFileContent(sourceFile)
//        val aesCipher = getAesCipher(Cipher.ENCRYPT_MODE)
//        SystemPrint.outPrintln(sourceFile.name + " contentByte.size = " + contentByte.size)
//        val encryptByte = aesCipher.doFinal(contentByte)
//        SystemPrint.outPrintln(sourceFile.name + " encryptByte.size = " + encryptByte.size)
//        val base64 = Base64.encode(encryptByte)
//        SystemPrint.outPrintln(sourceFile.name + " base64.size = " + base64.size)
//        val outputStream = FileOutputStream(encryptFile)
//        outputStream.write(base64)
//        outputStream.close()
//        return encryptFile
//
//    }
//
//    open fun decrypt(encodeFile: File, decryptFile: File?): File? {
//        val encryptByte = getFileContent(encodeFile)
//        val aesCipher = getAesCipher(Cipher.DECRYPT_MODE)
//        try {
//            val fos = FileOutputStream(decryptFile)
//            val base64: ByteArray =
//                Base64.decode(encryptByte)
//            val decryptBytes = aesCipher.doFinal(base64)
//            fos.write(decryptBytes)
//            fos.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return decryptFile
//    }

    /**
     * 对文件进行 AES 加密
     * @return 返回的是加密之后的文件
     */
    open fun encrypt(sourceFile: File, encryptFile: File): File? {
        var inputStream: FileInputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            inputStream = FileInputStream(sourceFile)
            outputStream = FileOutputStream(encryptFile)

            val cipherInputStream =
                CipherInputStream(inputStream, getAesCipher(Cipher.ENCRYPT_MODE))
            val cache = ByteArray(1024)
            var lineByte: Int = cipherInputStream.read(cache)
            while (lineByte > 0) {
                outputStream.write(cache, 0, lineByte)
                lineByte = cipherInputStream.read(cache)
            }
            cipherInputStream.close()
        } catch (e: Exception) {

        } finally {
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e: Exception) {

            }
        }
        return encryptFile
    }

    /**
     * 解密
     * @return 返回解密之后的文件
     */
//    open fun decrypt(encodeFile: File, decryptFile: File): File? {
//        var inputStream: FileInputStream? = null
//        var outputStream: FileOutputStream? = null
//        //val decryptFile = File.createTempFile(encodeFile.name, getFileSuffix(encodeFile.path))
//
//        try {
//            // SystemPrint.outPrintln(getFileSuffix(encodeFile.path))
//            inputStream = FileInputStream(encodeFile)
//            outputStream = FileOutputStream(decryptFile)
//
//            val cipherOutputStream =
//                CipherOutputStream(outputStream, getAesCipher(Cipher.DECRYPT_MODE))
//            val cache = ByteArray(1024)
//            var lineByte = inputStream.read(cache)
//            while (lineByte > 0) {
//                cipherOutputStream.write(cache, 0, lineByte)
//                lineByte = inputStream.read(cache)
//            }
//            cipherOutputStream.close()
//
//        } catch (e: Exception) {
//
//        } finally {
//            try {
//                inputStream?.close()
//                outputStream?.close()
//            } catch (e: Exception) {
//            }
//
//        }
//        return decryptFile
//    }


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