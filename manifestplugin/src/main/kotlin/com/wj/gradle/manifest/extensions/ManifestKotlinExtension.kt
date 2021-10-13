package com.wj.gradle.manifest.extensions

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer

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
//            proguardFiles file("version.xml")
//        }
//    }
//}
 * @author wenjing.liu
 */
open class ManifestKotlinExtension(var buildTypes: NamedDomainObjectContainer<BuildType>) {

    companion object {
        const val TAG: String = "manifestKotlin"
    }

    private var compileSdkVersion: Int = 0
    private var buildToolVersion: String = ""
    private var defaultConfig: DefaultConfig = DefaultConfig()

    open fun compileSdkVersion(sdk: Int) {
        this.compileSdkVersion = sdk
    }

    open fun buildToolVersion(version: String) {
        this.buildToolVersion = version
    }

    open fun compileSdkVersion(): Int {
        return this.compileSdkVersion
    }

    open fun buildToolVersion(): String {
        return this.buildToolVersion
    }

    open fun defaultConfig(action: Action<DefaultConfig>) {
        action.execute(defaultConfig)
    }

    open fun buildTypes(action: Action<NamedDomainObjectContainer<BuildType>>) {
        action.execute(buildTypes)
    }

    open fun buildTypes(): NamedDomainObjectContainer<BuildType> {
        return this.buildTypes

    }

    override fun toString(): String {
        val buildBuffer = StringBuffer()
        for (build in buildTypes()) {
            buildBuffer.append("${build}\n")
        }

        return "\ncompileSdkVersion: ${compileSdkVersion()}\nbuildToolVersion: ${buildToolVersion()}\n" +
                "defaultConfig: {\n  applicationId: ${defaultConfig.applicationId()}\n  minSdkVersion: ${defaultConfig.minSdkVersion()}" +
                " }\nbuildTypes:{\n ${buildBuffer}}"
    }

}