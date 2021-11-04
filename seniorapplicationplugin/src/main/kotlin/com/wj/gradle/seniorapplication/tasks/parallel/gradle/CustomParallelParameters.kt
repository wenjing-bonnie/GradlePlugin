package com.wj.gradle.seniorapplication.tasks.parallel.gradle

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.workers.WorkParameters

/**
 * Created by wenjing.liu on 2021/11/3 in J1.
 *
 * Task的输入输出参数
 * @author wenjing.liu
 */
interface CustomParallelParameters : WorkParameters {

    val testLazyOutputFile: RegularFileProperty
    val testInputFiles: ConfigurableFileCollection
}