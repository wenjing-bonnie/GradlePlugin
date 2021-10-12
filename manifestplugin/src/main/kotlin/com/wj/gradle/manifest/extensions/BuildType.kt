package com.wj.gradle.manifest.extensions

import jdk.jfr.Enabled
import org.gradle.api.Project
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
 */
open class BuildType constructor(name: String) {
    private var minifyEnabled: Boolean = false
    private var proguardFiles: File = File("")
    private var name: String = ""


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

}