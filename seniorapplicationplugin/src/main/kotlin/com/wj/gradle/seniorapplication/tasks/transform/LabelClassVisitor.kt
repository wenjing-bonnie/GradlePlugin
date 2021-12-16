package com.wj.gradle.seniorapplication.tasks.transform

import com.wj.gradle.seniorapplication.utils.SystemPrint
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

/**
 * Created by wenjing.liu on 2021/12/15 in J1.
 *
 * 为每个类添加Label的实例方法
 *
 * AGPBI: {"kind":"warning","text":"Expected stack map table for method with non-linear control flow.","sources":[{"file":"/Users/j1/Documents/android/code/GradlePlugin/app/build/intermediates/transforms/AutoLogTask/huawei/debug/57/com/wj/gradle/plugin/ByteCode.class"}],"tool":"D8"}
 *  It seems that ClassWriter or ClassVisitor in ASM did not implement any method
 * @author wenjing.liu
 */
open class LabelClassVisitor(private val visitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM9, visitor) {
    val TAG = "LabelClassVisitor"

    companion object {
        const val FOR_LABEL = "forLabelByteCode"
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        //仅在ByteCode中添加Label相关的代码
        if ("com/wj/gradle/plugin/ByteCode" != name) {
            return
        }
        //增加新的方法forLabelByteCode()
        addForLabelMethod()

    }

    /**
     *   private void forLabelByteCode() {
     *      for (int i = 3; i < 10; i+=2) {
     *          System.currentTimeMillis();
     *      }
     *   }
     */
    private fun addForLabelMethod() {
        val methodVisitor =
            visitor.visitMethod(Opcodes.ACC_PRIVATE, FOR_LABEL, "()V", null, null)
        val firstVar = 1 //newLocal(Type.INT_TYPE)
        methodVisitor.visitInsn(AdviceAdapter.ICONST_3)
        methodVisitor.visitVarInsn(AdviceAdapter.ISTORE, firstVar)
        //放入3
        val inForLabel = Label()
        methodVisitor.visitLabel(inForLabel)
        methodVisitor.visitVarInsn(AdviceAdapter.ILOAD, firstVar)
        //放入10
        methodVisitor.visitIntInsn(AdviceAdapter.BIPUSH, 10)

        val outForLabel = Label()
        methodVisitor.visitJumpInsn(AdviceAdapter.IF_ICMPGE, outForLabel)
        //for循环里面的逻辑
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "java/lang/System",
            "currentTimeMillis",
            "()J",
            false
        )
        methodVisitor.visitInsn(AdviceAdapter.POP2)
        //自增
        methodVisitor.visitIincInsn(1, 2)
        methodVisitor.visitJumpInsn(AdviceAdapter.GOTO, inForLabel)

        methodVisitor.visitLabel(outForLabel)
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitEnd()
    }

//    override fun visitMethod(
//        access: Int,
//        name: String?,
//        descriptor: String?,
//        signature: String?,
//        exceptions: Array<out String>?
//    ): MethodVisitor {
//        SystemPrint.outPrintln(TAG, "name = " + name)
//
//        if (FOR_LABEL != name) {
//            return super.visitMethod(access, name, descriptor, signature, exceptions)
//        }
//        return LabelAdviceAdapter(
//            api,
//            super.visitMethod(access, name, descriptor, signature, exceptions),
//            access,
//            name,
//            descriptor
//        )
//
//    }

}