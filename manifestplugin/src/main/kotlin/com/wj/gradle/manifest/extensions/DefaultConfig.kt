package com.wj.gradle.manifest.extensions

/**
 * Created by wenjing.liu on 2021/10/12 in J1.
 *
 * @author wenjing.liu
 */
open class DefaultConfig {
    private var applicationId: String = ""
    private var minSdkVersion: Int = 0

    open fun applicationId(id: String) {
        this.applicationId = id
    }

    open fun getApplicationId(): String {
        return this.applicationId
    }

    open fun minSdkVersion(sdk: Int) {
        this.minSdkVersion = sdk
    }

    open fun getMinSdkVersion(): Int {
        return this.minSdkVersion
    }
}