package com.wj.gradle.apkprotect.algroithm.duichen

import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * Created by wenjing.liu on 2022/1/20 in J1.
 *
 * AES加密
 *
 * @author wenjing.liu
 */
open class AesAlgorithm {
    private val AES = "AES"
    private val KET_SIZE = 128
    private val PASSWORD = "123456"
    private val CHARSET = Charsets.UTF_8


    /**
     * 加密之后已base64编码字符串输出
     */
    open fun encryptToBase64(context: String): ByteArray {
        //1.获取一个密码器
        val cipher = Cipher.getInstance(AES)
        //2.初始化密码器
        cipher.init(Cipher.ENCRYPT_MODE, getAesSecretKey())
        //3.加密context.toByteArray(CHARSET)
        val encryptBytes = cipher.doFinal(context.toByteArray(CHARSET))

        //1.加密
        // val encryptBytes = getBytesFromCipher(Cipher.ENCRYPT_MODE, context.toByteArray(CHARSET))
        //2.加密后的数据转进行base64编码
        //val base64Bytes = Base64.getEncoder().encode(encryptBytes)
        //return base64Bytes
        return encryptBytes
    }

    /**
     * 解密
     */
    open fun decryptFromBase64(contents: ByteArray): String? {

        //1.获取一个密码器
        val cipher = Cipher.getInstance(AES)
        //2.初始化密码器
        cipher.init(Cipher.DECRYPT_MODE, getAesSecretKey())
        //3.加密context.toByteArray(CHARSET)
        try {
            val decryptBytes = cipher.doFinal(contents)
            return String(decryptBytes, CHARSET)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //1.Base64解码
        // val originalBytes = Base64.getDecoder().decode(contents)
        //2.解密
        //val decryptBytes = getBytesFromCipher(Cipher.DECRYPT_MODE, contents)
        //3.解密之后的内容
        // return decryptBytes
        return null
    }

    /**
     * bytes转换成16进制的字符串String
     */
    open fun encryptBytes2Hex(bytes: ByteArray): String {
        val buffer = StringBuffer()
        bytes.forEach {

            var hex: String = Integer.toHexString(it.toInt() and 0xFF)
            if (hex.length == 1) {
                hex = "0${hex}"
            }
            buffer.append(hex)
        }
        return buffer.toString()
    }

    /**
     * 获取一个密码器
     */
    private fun getBytesFromCipher(opmode: Int, bytes: ByteArray): ByteArray {
        //1.获取一个密码器
        val cipher = Cipher.getInstance(AES)
        //2.初始化密码器
        cipher.init(opmode, getAesSecretKey())
        //3.加密context.toByteArray(CHARSET)
        val bytes = cipher.doFinal(bytes)
        return bytes
    }

    /**
     * 对密钥进行处理
     * 根据传入的seed构建密钥生成器
     */
    private fun getAesSecretKey(): SecretKeySpec {
        //1.构建密钥生成器
        val kgen: KeyGenerator = KeyGenerator.getInstance(AES)
        //2.初始化生成器
        //kgen.init(KET_SIZE, SecureRandom(PASSWORD.toByteArray(CHARSET)))
        val secureRandom = SecureRandom.getInstance("SHA1PRNG")
        secureRandom.setSeed(PASSWORD.toByteArray())
        kgen.init(KET_SIZE, secureRandom)
        //3.产生原始对称密钥
        val originalKey: SecretKey = kgen.generateKey()
        //4.获取原始对称密钥字节数组
        val key: ByteArray = originalKey.encoded
        //5.根据字节数组生成AES密钥
        return SecretKeySpec(key, AES)
    }


}