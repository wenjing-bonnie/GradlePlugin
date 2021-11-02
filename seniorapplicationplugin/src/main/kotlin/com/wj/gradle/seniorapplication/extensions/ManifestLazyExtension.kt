package com.wj.gradle.seniorapplication.extensions

import org.gradle.api.provider.Property

/**
 * Created by wenjing.liu on 2021/10/28 in J1.
 * 懒加载配置属性
 * @author wenjing.liu
 */
abstract class ManifestLazyExtension {
    companion object {
        const val TAG = "manifestLazy"
    }

    abstract val lazyProperty: Property<String>
}
