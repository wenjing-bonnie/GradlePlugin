package com.wj.gradle.seniorapplication.extensions

/**
 * Created by wenjing.liu on 2021/12/9 in J1.
 * 自动打印方法耗时时间的阀值
 * @author wenjing.liu
 */
open class AutoLogExtension {
    private var autoLogTimeout = 0L

    open fun autoLogTimeout(timeout: Long) {
        this.autoLogTimeout = timeout
    }

    open fun autoLogTimeout(): Long {
        return this.autoLogTimeout
    }
}