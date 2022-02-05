package com.wj.gradle.apkprotect.tasks.base

import com.android.build.gradle.internal.tasks.NewIncrementalTask

/**
 * 没有outputs的[NewIncrementalTask]，即默认的outputs始终需要全量编译
 */
abstract class NewIncrementalWithoutOutputsTask : NewIncrementalTask() {

    init {
        outputs.upToDateWhen { false }
    }
}