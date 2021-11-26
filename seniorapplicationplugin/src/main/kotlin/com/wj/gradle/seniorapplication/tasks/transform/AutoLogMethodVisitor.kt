package com.wj.gradle.seniorapplication.tasks.transform

import com.wj.gradle.seniorapplication.utils.SystemPrint
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor

/**
 * Created by wenjing.liu on 2021/11/25 in J1.
 *
 * @author wenjing.liu
 */
open class AutoLogMethodVisitor(api: Int) : MethodVisitor(api) {
    val TAG = "AutoLogMethodVisitor"


    //生命周期
    override fun visitCode() {
        super.visitCode()
        SystemPrint.outPrintln(TAG, " -- visitCode -- ")
    }


    override fun visitEnd() {
        super.visitEnd()
        SystemPrint.outPrintln(TAG, " -- visitEnd -- ")

    }

    //Code属性
    override fun visitIntInsn(opcode: Int, operand: Int) {
        super.visitIntInsn(opcode, operand)
        SystemPrint.outPrintln(TAG, " -- visitIntInsn opcode -- ${opcode}")
    }

    override fun visitInsn(opcode: Int) {
        super.visitInsn(opcode)
        SystemPrint.outPrintln(TAG, " -- visitInsn opcode -- ${opcode}")
    }

    override fun visitVarInsn(opcode: Int, `var`: Int) {
        super.visitVarInsn(opcode, `var`)
        SystemPrint.outPrintln(TAG, " -- visitVarInsn opcode -- ${opcode}")
    }

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        SystemPrint.outPrintln(TAG, " -- visitMethodInsn opcode -- ${opcode} name ${name}")

    }

    override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {
        super.visitFieldInsn(opcode, owner, name, descriptor)
        SystemPrint.outPrintln(TAG, " -- visitFieldInsn name -- ${name}")

    }

    //

    override fun visitLabel(label: Label?) {
        super.visitLabel(label)
        SystemPrint.outPrintln(TAG, " -- visitLabel label -- ${label}")

    }

    override fun visitLocalVariable(
        name: String?,
        descriptor: String?,
        signature: String?,
        start: Label?,
        end: Label?,
        index: Int
    ) {
        super.visitLocalVariable(name, descriptor, signature, start, end, index)
        SystemPrint.outPrintln(TAG, " -- visitLocalVariable name -- ${name}")

    }

    override fun visitFrame(
        type: Int,
        numLocal: Int,
        local: Array<out Any>?,
        numStack: Int,
        stack: Array<out Any>?
    ) {
        super.visitFrame(type, numLocal, local, numStack, stack)
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack, maxLocals)
    }

}