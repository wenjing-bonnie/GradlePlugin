package com.wj.gradle.seniorapplication.tasks.transform.parallel

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.TransformOutputProvider
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkParameters

/**
 * Created by wenjing.liu on 2021/12/9 in J1.
 *
 * 里面的成员变量必须用val进行修饰,否则会抛出空指针异常
 * @author wenjing.liu
 */
interface AutoLogWorkParameters : WorkParameters {
    /**
     * 每个需要处理的.class文件
     */
    val directoryInput: Property<DirectoryInput>

    /**
     * 打印日志阀值
     */
    val autoLogTimeout: Property<Long>

    /**
     * 输出文件
     */
    val outputFile: RegularFileProperty
}