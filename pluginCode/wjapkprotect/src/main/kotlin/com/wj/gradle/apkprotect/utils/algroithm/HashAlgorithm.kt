package com.wj.gradle.apkprotect.algroithm

import java.security.MessageDigest

/**
 * Created by wenjing.liu on 2022/1/20 in J1.
 *
 * [ 散列算法 ]
 *
 * 1. md5:
 * 哈希函数.无论多长输入,都会输出为128b.
 * 可用在检查文件完整性及数字签名.
 * 不可逆.速度快.安全性适中
 * 2. sha1
 * 安全性比md5强.
 * 可用在检查文件完整性及数字签名.
 * 不可逆.速度慢.安全性高
 *
 * 静态类：将class换成object即可
 * @author wenjing.liu
 */
object HashAlgorithm {

    /**
     * md5
     */
    fun md5(content: String): ByteArray {
        val md5 = MessageDigest.getInstance("MD5")
        return md5.digest(content.toByteArray())
    }

    /**
     * sha1
     */
    fun sha1(content: String): ByteArray {
        val sha1 = MessageDigest.getInstance("SHA1")
        return sha1.digest(content.toByteArray())
    }

}

