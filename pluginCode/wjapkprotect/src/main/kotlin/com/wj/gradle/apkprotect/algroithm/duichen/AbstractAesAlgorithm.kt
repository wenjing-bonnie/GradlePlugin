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
abstract class AbstractAesAlgorithm {
    private val AES = "AES"
    private val KET_SIZE = 128
    private val PASSWORD = "123456"
    val CHARSET = Charsets.UTF_8


    /**
     * 获取一个密码器
     */
    open fun getAesCipher(opmode: Int): Cipher {
        //1.获取一个密码器
        val cipher = Cipher.getInstance(AES)
        //2.初始化密码器
        cipher.init(opmode, getAesSecretKey())
        return cipher
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


}