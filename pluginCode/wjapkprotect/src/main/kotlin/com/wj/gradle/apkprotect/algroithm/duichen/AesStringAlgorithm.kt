package com.wj.gradle.apkprotect.algroithm.duichen

import java.util.*
import javax.crypto.Cipher

/**
 * Created by wenjing.liu on 2022/1/21 in J1.
 *
 * @author wenjing.liu
 */
class AesStringAlgorithm :AbstractAesAlgorithm(){

    /**
     * 加密之后已base64编码字符串输出
     * @param context 需要加密的字符串
     * @return 返回byte[]
     */
    open fun encryptToBase64(context: String): ByteArray? {
        return encryptToBase64(context.toByteArray(CHARSET))
    }

    /**
     * 加密之后已base64编码字符串输出
     * @param context 需要加密的文件
     * @return 返回byte[]
     */
//    open fun encryptToBase64(context: File): ByteArray? {
//
//
//    }

    /**
     * 加密之后已base64编码字符串输出
     * @param context 需要加密的byte[]
     */
    open fun encryptToBase64(contents: ByteArray): ByteArray? {
        try {
            //1.加密
            val encryptBytes = getBytesFromCipher(Cipher.ENCRYPT_MODE, contents)
            //2.加密后的数据转进行base64编码
            return Base64.getEncoder().encode(encryptBytes)
        } catch (e: Exception) {
        }
        return null
    }

    /**
     * 解密
     *
     * @param contents 被base64编码之后的加密数据
     *
     * @return 返回解密的字符串
     */
    open fun decryptFromBase64ToString(contents: ByteArray): String? {
        val decodeBytes = decryptFromBase64(contents) ?: return null
        return String(decodeBytes, CHARSET)
    }

    /**
     * 解密
     *
     * @param contents 被base64编码之后的加密数据
     */
    open fun decryptFromBase64(contents: ByteArray): ByteArray? {
        try {
            //1.Base64解码
            val originalBytes = Base64.getDecoder().decode(contents)
            //2.解密之后的内容
            return getBytesFromCipher(Cipher.DECRYPT_MODE, originalBytes)
        } catch (e: Exception) {

        }
        return null
    }

    /**
     * 获取一个密码器
     */
    open fun getBytesFromCipher(opmode: Int, bytes: ByteArray): ByteArray {
        //3.加密context.toByteArray(CHARSET)
        val bytes = getAesCipher(opmode).doFinal(bytes)
        return bytes
    }

}