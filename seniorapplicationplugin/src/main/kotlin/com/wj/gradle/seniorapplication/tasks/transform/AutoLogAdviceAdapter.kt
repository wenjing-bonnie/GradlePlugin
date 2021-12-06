package com.wj.gradle.seniorapplication.tasks.transform

import com.wj.gradle.seniorapplication.utils.SystemPrint
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.Type


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
    private var startVar: Int = 0

    override fun onMethodEnter() {
        //不处理<init>
        if (isInitMethod() || methodVisitor == null) {
            return
        }
        SystemPrint.errorPrintln(TAG, " --  onMethodEnter -- ${name}" + " , nextLocal ${nextLocal}")
        SystemPrint.outPrintln(TAG, "argumentTypes size  = " + argumentTypes.size)
        val label = Label()
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "java/lang/System",
            "currentTimeMillis",
            "()J",
            false
        )
        startVar = newLocal(Type.LONG_TYPE)
        //methodVisitor.visitInsn(Opcodes.DUP)
        SystemPrint.outPrintln(TAG, "new local is ${startVar}")
        //为该变量起名字，感觉不起名字（或者加上自己特殊的前缀），灵活性更大，可以不用考虑名字重复
        /**
         * @param start the first instruction corresponding to the scope of this local variable
         *     (inclusive). 该局部变量作用范围对应的第一条指令
         * @param end the last instruction corresponding to the scope of this local variable (exclusive).
         * 该局部变量作用范围对应的最后一条指令
         * @param index the local variable's index.
         */
        //methodVisitor.visitLocalVariable("start", "J", null, label, label, startVar)
        methodVisitor.visitVarInsn(LSTORE, startVar)
        super.onMethodEnter()

    }

    /**
     * @param opcode one of {@link Opcodes#RETURN}, {@link Opcodes#IRETURN}, {@link Opcodes#FRETURN},
     *     {@link Opcodes#ARETURN}, {@link Opcodes#LRETURN}, {@link Opcodes#DRETURN} or {@link
     *     Opcodes#ATHROW}.
     */
    override fun onMethodExit(opcode: Int) {
        if (isInitMethod() || methodVisitor == null || startVar == 0) {
            return
        }
        SystemPrint.errorPrintln(
            TAG,
            " --  onMethodExit -- startVar ${startVar}  nextLocal  ${nextLocal}"
        )
        // 做减法
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "java/lang/System",
            "currentTimeMillis",
            "()J",
            false
        )
        methodVisitor.visitVarInsn(LLOAD, startVar)
        methodVisitor.visitInsn(LSUB)

        //存储sub
        val subVar = newLocal(Type.LONG_TYPE)
        SystemPrint.outPrintln(TAG, "subVar = " + subVar + "  nextLocal  ${nextLocal}")
        methodVisitor.visitVarInsn(LSTORE, subVar)


        //输出日志
        methodVisitor.visitLdcInsn("ExecutionTime")
        val log = "\' ${name} \' execution is %d ms"
        methodVisitor.visitLdcInsn(log)
        methodVisitor.visitInsn(Opcodes.ICONST_1)
        methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object")
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitInsn(Opcodes.ICONST_0)
        methodVisitor.visitVarInsn(Opcodes.LLOAD, subVar)
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
        //！！！！ 一定要有，否则该地方就会作为返回值返回
        methodVisitor.visitInsn(POP)
        returnValue()
        super.onMethodExit(opcode)
    }

    /**
     * init方法不处理
     *
     * 对于一个类（Class）来说，如果没有提供任何构造方法，Java编译器会自动生成一个默认构造方法。在所有的.class文件中，构造方法的名字是<init>()。
     * 另外，如果在.class文件中包含静态代码块，那么就会有一个<clinit>()方法。
     * 构造函数及static{}不需要增加日志
     */
    private fun isInitMethod(): Boolean {
        SystemPrint.outPrintln(TAG, " --  isInitMethod -- ${name}")
        return "<init>" == name || "<clinit>" == name
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
        super.visitMaxs(maxStack + 4, maxLocals)
        SystemPrint.outPrintln(TAG, " -- visitMaxs maxStack -- ${maxStack} maxLocals ${maxLocals}")

    }


}