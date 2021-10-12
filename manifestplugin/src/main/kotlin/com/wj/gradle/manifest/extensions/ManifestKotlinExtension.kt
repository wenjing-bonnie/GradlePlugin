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

    private var compileSdkVersion: Int = 0
    private var buildToolVersion: String = ""
   // private var defaultConfig: DefaultConfig = DefaultConfig()

    open fun setCompileSdkVersion(sdk: Int) {
        this.compileSdkVersion = sdk
    }

    open fun setBuildToolVersion(version: String) {
        this.buildToolVersion = version
    }

    open fun getCompileSdkVersion(): Int {
        return this.compileSdkVersion
    }

    open fun getBuildToolVersion(): String {
        return this.buildToolVersion
    }

//    open fun setDefaultConfig(action: Action<DefaultConfig>) {
//        action.execute(defaultConfig)
//    }

    open class DefaultConfig {
        private var applicationId: String = ""
        private var minSdkVersion: Int = 0

        open fun setApplicationId(id: String) {
            this.applicationId = id
        }

        open fun getApplicationId(): String {
            return this.applicationId
        }

        open fun setMinSdkVersion(sdk: Int) {
            this.minSdkVersion = sdk
        }

        open fun getMinSdkVersion(): Int {
            return this.minSdkVersion
        }
    }

//    override fun toString(): String {
//        return "compileSdkVersion: ${compileSdkVersion}\n buildToolVersion: ${buildToolVersion}\n" +
//                "defaultConfig: {applicationId: ${defaultConfig.getApplicationId()}\n minSdkVersion: ${defaultConfig.getMinSdkVersion()}\n" +
//                " }"
//    }

}