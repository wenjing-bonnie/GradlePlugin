package com.wj.gradle.manifest.extensions

import java.io.File

/**
 * Created by wenjing.liu on 2021/10/12 in J1.
 * 类似于android gradle plugin中的
//android {
//    buildTypes {
//        release {
//            minifyEnabled false
//            proguardFiles  file("version.xml")
//        }
//    }
 * @author wenjing.liu
 *
 * 主构造函数和次构造函数,默认会有一个不带参数的主构造函数.
 * 【主构造函数】没有函数体,直接定义在类名的后面.通过init{}在主构造函数中添加代码块
 * 若在主构造函数的参数使用了var/val修饰,相当于在类中声明了对应名称的属性
 * 【次构造函数】可以有多个次构造函数,用constructor声明
 * 如果一个类中既有主构造函数又有次构造函数,所有的次构造函数必须使用this直接或间接调用主构造函数.当类名够加了圆括号，无论有没有参数，就需要遵守这个规定
 */
open class BuildType constructor(var name: String) {
    private var minifyEnabled: Boolean = false
    private var proguardFiles: File = File("")

    open fun minifyEnabled(enabled: Boolean) {
        this.minifyEnabled = enabled
    }

    open fun proguardFiles(file: File) {
        this.proguardFiles = file
    }

    open fun minifyEnabled(): Boolean {
        return this.minifyEnabled
    }

    open fun proguardFiles(): File {
        return this.proguardFiles
    }

    override fun toString(): String {
        return " ${name}: < minifyEnabled: ${minifyEnabled} ; proguardFiles: ${proguardFiles.path} >"
    }

}