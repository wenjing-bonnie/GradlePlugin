package com.wj.gradle.apkprotect.tasks.signed.parallel

import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkParameters

/**
 * create by wenjing.liu at 2022/2/14
 */
interface ApkAlignAndSignedParameters : WorkParameters {

    val apkUnsignedFile: RegularFileProperty
    val project: Property<Project>
}