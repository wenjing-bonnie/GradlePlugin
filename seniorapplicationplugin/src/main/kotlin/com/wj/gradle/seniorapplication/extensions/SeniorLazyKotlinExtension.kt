package com.wj.gradle.manifest.extensions

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import java.io.File

/**
 * Created by wenjing.liu on 2021/10/8 in J1.
 * 扩展属性
 * 类似于android gradle plugin中的
//android {
//    compileSdkVersion 31
//    buildToolsVersion "30.0.3"
//
//    defaultConfig {
//        applicationId "com.wj.gradle.plugin"
//        minSdkVersion 23
//    }
//
//    buildTypes {
//        release {
//            minifyEnabled false
//            proguardFiles file("versiondemo.xml")
//        }
//    }
//}
 * @author wenjing.liu
 */
open class SeniorLazyKotlinExtension {

    companion object {
        const val TAG: String = "seniorKotlin"
    }

    private var incremental: IncrementalExtension = IncrementalExtension()


    open fun incremental(action: Action<IncrementalExtension>) {
        action.execute(incremental)
    }

    open fun incremental(): IncrementalExtension {
        return incremental
    }

    override fun toString(): String {
        return "\nincremental: ${incremental()}\n}"
    }

}