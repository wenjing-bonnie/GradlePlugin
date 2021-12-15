package com.wj.gradle.seniorapplication.tasks.transform

import com.wj.gradle.seniorapplication.utils.SystemPrint
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

/**
 * Created by wenjing.liu on 2021/12/15 in J1.
 *
 * 针对Label用处的实例
 *
 * @author wenjing.liu
 */
open class LabelAdviceAdapter(
    api: Int, private val methodVisitor: MethodVisitor?,
    access: Int,
    name: String?,
    descriptor: String?,
) : AdviceAdapter(api, methodVisitor, access, name, descriptor) {

    val TAG = "LabelAdviceAdapter"

    override fun onMethodEnter() {
        super.onMethodEnter()
        if (isForLabel()) {
            handlerForLabel()
            return
        }
    }

    private fun handlerForLabel() {
        SystemPrint.outPrintln(TAG, "add for by label")
        if (methodVisitor == null) {
            return
        }
        val firstVar = newLocal(Type.INT_TYPE)
        methodVisitor.visitInsn(ICONST_3)
        methodVisitor.visitVarInsn(ISTORE, firstVar)
        //放入3
        val inForLabel = Label()
        methodVisitor.visitLabel(inForLabel)
        methodVisitor.visitVarInsn(ILOAD, firstVar)
        //放入10
        methodVisitor.visitIntInsn(BIPUSH, 10)

        val outForLabel = Label()
        methodVisitor.visitJumpInsn(IF_ICMPGE, outForLabel)
        //for循环里面的逻辑
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "java/lang/System",
            "currentTimeMillis",
            "()J",
            false
        )
        methodVisitor.visitInsn(POP2)
        //自增
        methodVisitor.visitIincInsn(1,2)
        methodVisitor.visitJumpInsn(GOTO,inForLabel)
        //for循环之后的逻辑
        returnValue()

    }

    private fun isForLabel(): Boolean {
        return LabelClassVisitor.FOR_LABEL == name
    }

}