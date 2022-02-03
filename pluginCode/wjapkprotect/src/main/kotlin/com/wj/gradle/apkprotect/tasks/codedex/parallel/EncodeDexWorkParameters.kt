package com.wj.gradle.apkprotect.tasks.codedex.parallel

import org.gradle.api.file.RegularFileProperty
import org.gradle.workers.WorkParameters

interface EncodeDexWorkParameters : WorkParameters {
    val dexFile: RegularFileProperty
}