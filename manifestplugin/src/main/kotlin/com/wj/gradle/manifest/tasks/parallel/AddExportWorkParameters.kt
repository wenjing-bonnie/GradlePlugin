package com.wj.gradle.manifest.tasks.parallel

import org.gradle.api.file.RegularFileProperty
import org.gradle.workers.WorkParameters

/**
 * Created by wenjing.liu on 2021/11/10 in J1.
 *
 * 为所有的未适配Android 12 exported:true属性的组件添加
 * WorkParameters
 * @author wenjing.liu
 */
interface AddExportWorkParameters : WorkParameters {
    /**
     * 需要处理的manifest文件
     */
    val inputManifestFile: RegularFileProperty
    /**
     * 是否仅仅抛出编译异常
     */
    var isOnlyBuildError: Boolean
}