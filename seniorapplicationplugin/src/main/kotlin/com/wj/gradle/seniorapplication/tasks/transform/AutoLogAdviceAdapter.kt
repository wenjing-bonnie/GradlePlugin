package com.wj.gradle.seniorapplication.tasks.transform

import com.wj.gradle.seniorapplication.utils.SystemPrint
import org.objectweb.asm.Attribute
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * Created by wenjing.liu on 2021/11/23 in J1.
 *
 *  @param api the ASM API version implemented by this visitor. Must be one of {@link
 *     Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
 * @author wenjing.liu
 */

open class AutoLogAdviceAdapter(
    api: Int, private val methodVisitor: MethodVisitor?,
    access: Int,
    name: String?,
    descriptor: String?
) : AdviceAdapter(api, methodVisitor, access, name, descriptor) {

    private val TAG = "AutoLogAdviceAdapter"

    override fun onMethodEnter() {
        super.onMethodEnter()
        //不处理<init>
        if (isInitMethod() || methodVisitor == null) {
            return
        }
        SystemPrint.outPrintln(TAG, " --  onMethodEnter -- ${name}")
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "java/lang/System",
            "currentTimeMillis",
            "()J",
            false
        )
        methodVisitor.visitVarInsn(Opcodes.LSTORE, 3)

    }

    /**
     * @param opcode one of {@link Opcodes#RETURN}, {@link Opcodes#IRETURN}, {@link Opcodes#FRETURN},
     *     {@link Opcodes#ARETURN}, {@link Opcodes#LRETURN}, {@link Opcodes#DRETURN} or {@link
     *     Opcodes#ATHROW}.
     */
    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        if (isInitMethod() || methodVisitor == null) {
            return
        }
        SystemPrint.outPrintln(TAG, " --  onMethodExit -- opcode ${opcode}")

        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/Object",
            "getClass",
            "()Ljava/lang/Class;",
            false
        )
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/Class",
            "getSimpleName",
            "()Ljava/lang/String;",
            false
        )
        methodVisitor.visitLdcInsn("cost time is %d")
        methodVisitor.visitInsn(Opcodes.ICONST_1)
        methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object")
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitInsn(Opcodes.ICONST_0)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "java/lang/System",
            "currentTimeMillis",
            "()L",
            false
        )
        methodVisitor.visitVarInsn(Opcodes.LLOAD, 3)
        methodVisitor.visitInsn(Opcodes.LSUB)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "java/lang/Long",
            "valueOf",
            "(J)Ljava/lang/Long;",
            false
        )
        methodVisitor.visitInsn(AASTORE)
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "java/lang/String",
            "format",
            "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;",
            false
        )
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "android/util/Log",
            "v",
            "(Ljava/lang/String;Ljava/lang/String;)I",
            false
        )
        methodVisitor.visitInsn(POP)

    }

    /**
     * init方法不处理
     */
    private fun isInitMethod(): Boolean {
        SystemPrint.outPrintln(TAG, " --  isInitMethod -- ${name}")
        return "<init>" == name
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

    override fun visitAttribute(attribute: Attribute?) {
        super.visitAttribute(attribute)
        SystemPrint.outPrintln(TAG, " -- visitAttribute name -- ${attribute}")

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
        SystemPrint.outPrintln(TAG, " -- visitFrame type -- ${type}")

    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack, maxLocals)
        SystemPrint.outPrintln(TAG, " -- visitMaxs maxStack -- ${maxStack} maxLocals ${maxLocals}")

    }


}