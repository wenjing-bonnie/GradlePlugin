package com.wj.gradle.apkprotect.algroithm.duichen

/**
 * Created by wenjing.liu on 2022/1/20 in J1.
 *
 * [ 对称加密 ]
 *
 * 使用的密码只有一个.发送和接收都使用这个密钥对数据进行加密和解密.
 * 加密过程:数据发送方将[ 明文 ]和[ 加密密钥 ]经过特殊加密处理,生成复杂的[ 加密密文 ]进行发送;
 * 解密过程:数据接收方接收到密文后，若想读取原数据,则使用[ 加密密钥 ]及 [ 逆算法 ]对 []加密的密文 ] 进行解密
 *
 * 密钥管理比较难,不适合互联网,一般用于内部系统
 * 安全性性中
 *
 * 1.DES
 * 分组密码,以64位为分组对数据加密.
 * 密钥长度为56位.安全性低.运行速度快.资源消耗中
 *
 * 2.3DES
 * 密钥长度为112或168.安全性适中.运行速度慢.资源消耗高
 *
 * 3.AES
 * 密钥长度为128、192、256.安全性高.运行速度高.资源消耗低
 *
 * @author wenjing.liu
 */
object DuichenAlgorithm {
}