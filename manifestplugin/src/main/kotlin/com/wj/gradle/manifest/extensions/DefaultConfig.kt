package com.wj.gradle.manifest.extensions

/**
 * Created by wenjing.liu on 2021/10/12 in J1.
 *
 * @author wenjing.liu
 */
open class DefaultConfigOfManifest {
    private var applicationIdOfManifest: String = ""
    private var minSdkVersionOfManifest: Int = 0

    open fun applicationIdOfManifest(id: String) {
        this.applicationIdOfManifest = id
    }

    open fun getApplicationIdOfManifest(): String {
        return this.applicationIdOfManifest
    }

    open fun minSdkVersionOfManifest(sdk: Int) {
        this.minSdkVersionOfManifest = sdk
    }

    open fun getMinSdkVersionOfManifest(): Int {
        return this.minSdkVersionOfManifest
    }
}