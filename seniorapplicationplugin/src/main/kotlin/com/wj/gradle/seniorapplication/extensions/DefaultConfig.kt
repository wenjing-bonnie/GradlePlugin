package com.wj.gradle.manifest.extensions

/**
 * Created by wenjing.liu on 2021/10/12 in J1.
 * 类似于android gradle plugin中的
//android {
//    defaultConfig {
//        applicationId "com.wj.gradle.plugin"
//        minSdkVersion 23
//    }
//}
 * @author wenjing.liu
 */
open class DefaultConfig {
    private var applicationId: String = ""
    private var minSdkVersion: Int = 0

    open fun applicationId(id: String) {
        this.applicationId = id
    }

    open fun applicationId(): String {
        return this.applicationId
    }

    open fun minSdkVersion(sdk: Int) {
        this.minSdkVersion = sdk
    }

    open fun minSdkVersion(): Int {
        return this.minSdkVersion
    }
}