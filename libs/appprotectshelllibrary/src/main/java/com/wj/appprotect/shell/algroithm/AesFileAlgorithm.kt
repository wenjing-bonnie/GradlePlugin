package com.wj.appprotect.shell.algroithm

import com.wj.appprotect.shell.algroithm.java.Base64
import java.io.*
import javax.crypto.Cipher

/**
 * Created by wenjing.liu on 2022/1/21 in J1.
 *
 * 使用AES对文件进行加密和解密
 *
 * @author wenjing.liu
 */
open class AesFileAlgorithm : AbstractAesAlgorithm() {

    override fun getPassword(): String {
        return "123456"
    }

    open fun decrypt(encodeFile: File, decryptFile: File): File? {
        val encryptByte: ByteArray = getFileContent(encodeFile)
        val aesCipher = getAesCipher(Cipher.DECRYPT_MODE)
        try {
            val fos = FileOutputStream(decryptFile)
            val base64 = Base64.decode(encryptByte)
            val decryptBytes = aesCipher.doFinal(base64)
            fos.write(decryptBytes)
            fos.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        getFileContent(decryptFile)
        return decryptFile
    }

    /**
     * 解密
     * @return 返回解密之后的文件
     */
//    open fun decrypt(encodeFile: File, decryptDirectory: File): File? {
//        var inputStream: FileInputStream? = null
//        var outputStream: FileOutputStream? = null
//        if (decryptDirectory.exists()) {
//            decryptDirectory.delete()
//        } else {
//            decryptDirectory.mkdirs()
//        }
//        val decryptFile =
//            File.createTempFile(encodeFile.name, getFileSuffix(encodeFile.path), decryptDirectory)
//        // LogUtils.logV("encodeFile = " + encodeFile + " \n" + getFileSuffix(encodeFile.path))
//        LogUtils.logV("decryptFile = " + decryptFile)
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
        return buffer.toString().toByteArray(Charsets.UTF_8)
    }

}