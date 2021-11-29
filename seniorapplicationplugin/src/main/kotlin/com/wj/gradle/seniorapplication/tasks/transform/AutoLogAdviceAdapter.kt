package com.wj.gradle.seniorapplication.tasks.transform

import com.wj.gradle.seniorapplication.utils.SystemPrint
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

/**
 * Created by wenjing.liu on 2021/11/23 in J1.
 *
 *  @param api the ASM API version implemented by this visitor. Must be one of {@link
 *     Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
 * @author wenjing.liu
 */

open class AutoLogAdviceAdapter(
    api: Int, methodVisitor: MethodVisitor?,
    access: Int,
    name: String?,
    descriptor: String?
) : AdviceAdapter(api, methodVisitor, access, name, descriptor) {

    val TAG = "AutoLogAdviceAdapter"

    override fun onMethodEnter() {
        super.onMethodEnter()
        SystemPrint.outPrintln(TAG, " --  onMethodEnter -- ${name}")
    }



}