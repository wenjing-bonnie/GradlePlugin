package com.wj.gradle.manifest.extensions

import org.gradle.api.Action

/**
 * Created by wenjing.liu on 2021/10/8 in J1.
 * 扩展属性
 * 仿android{}的配置项
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
//            versionFile  file("version.xml")
//        }
//    }
//}
 * @author wenjing.liu
 */
open class ManifestKotlinExtension {
    companion object {
        const val TAG: String = "manifestKotlin"
    }

    private var compileSdkVersionOfManifest: Int = 0
    private var buildToolVersionOfManifest: String = ""
    private var defaultConfigOfManifest: DefaultConfigOfManifest = DefaultConfigOfManifest()

    open fun compileSdkVersionOfManifest(sdk: Int) {
        this.compileSdkVersionOfManifest = sdk
    }

    open fun buildToolVersionOfManifest(version: String) {
        this.buildToolVersionOfManifest = version
    }

    open fun getCompileSdkVersionOfManifest(): Int {
        return this.compileSdkVersionOfManifest
    }

    open fun getBuildToolVersionOfManifest(): String {
        return this.buildToolVersionOfManifest
    }

    open fun defaultConfigOfManifest(action: Action<DefaultConfigOfManifest>) {
        action.execute(defaultConfigOfManifest)
    }

    override fun toString(): String {
        return "\ncompileSdkVersion: ${compileSdkVersionOfManifest}\nbuildToolVersion: ${buildToolVersionOfManifest}\n" +
                "defaultConfig: {\n  applicationId: ${defaultConfigOfManifest.getApplicationIdOfManifest()}\n  minSdkVersion: ${defaultConfigOfManifest.getMinSdkVersionOfManifest()}" +
                " }"
    }

}