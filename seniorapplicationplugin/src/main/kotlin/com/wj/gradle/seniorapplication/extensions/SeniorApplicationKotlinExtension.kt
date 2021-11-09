package com.wj.gradle.seniorapplication.extensions

import org.gradle.api.Action
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input

/**
 * Created by wenjing.liu on 2021/10/8 in J1.
 * 扩展属性
 * @author wenjing.liu
 */
abstract class SeniorApplicationKotlinExtension {

    companion object {
        const val TAG: String = "seniorApplicationKotlin"
    }

    /**
     * 懒加载配置属性
     */
    @get:Input
    abstract val lazyExtensionProperty: RegularFileProperty

    /**
     * codec library for Apache Common Codec
     */
    @get:Input
    abstract val md5CodecClasspath: ConfigurableFileCollection

    private var incremental: IncrementalExtension = IncrementalExtension()


    open fun incremental(action: Action<IncrementalExtension>) {
        action.execute(incremental)
    }

    open fun incremental(): IncrementalExtension {
        return incremental
    }

    override fun toString(): String {
        return "{\nincremental: ${incremental()}\n" +
                "lazy file property : ${lazyExtensionProperty.get().asFile.absoluteFile}"
        "}"
    }

}