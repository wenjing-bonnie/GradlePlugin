package com.wj.gradle.apkprotect.algroithm

/**
 * Created by wenjing.liu on 2022/1/20 in J1.
 *
 * [ 非对称加密 ]
 *
 * 需要两个密钥:[ 公开密钥 ] [ 私有密钥 ]
 *
 * 如果使用[ 公钥 ]进行数据加密,只有对应的[ 私钥 ]才能进行解密;
 * 如果使用[ 私钥 ]进行数据加密,只有对应的[ 公钥 ]才能进行解密;
 *
 * 通常做法:
 * 甲方生成一对密钥,然后把[ 公钥 ]公开,得到公钥的乙方使用该密钥对机密信息进行加密后在发送给甲方，
 * 甲方在使用[ 私钥]对加密的信息进行解密.
 *

 *
 * @author wenjing.liu
 */
object FeiDuichenAlgorithm {



}