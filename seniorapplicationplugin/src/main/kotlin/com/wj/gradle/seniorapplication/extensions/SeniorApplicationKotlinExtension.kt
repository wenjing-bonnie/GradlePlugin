package com.wj.gradle.seniorapplication.extensions

import org.gradle.api.Action

/**
 * Created by wenjing.liu on 2021/10/8 in J1.
 * 扩展属性
 * @author wenjing.liu
 */
open class SeniorApplicationKotlinExtension {

    companion object {
        const val TAG: String = "seniorApplicationKotlin"
    }

    private var incremental: IncrementalExtension = IncrementalExtension()


    open fun incremental(action: Action<IncrementalExtension>) {
        action.execute(incremental)
    }

    open fun incremental(): IncrementalExtension {
        return incremental
    }

    override fun toString(): String {
        return "\nincremental: ${incremental()}\n}"
    }

}