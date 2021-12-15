package com.wj.gradle.seniorapplication.tasks.transform

import com.wj.gradle.seniorapplication.utils.SystemPrint
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by wenjing.liu on 2021/12/15 in J1.
 *
 * 为每个类添加Label的实例方法
 * @author wenjing.liu
 */
open class LabelClassVisitor(private val visitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM9, visitor) {

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
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitEnd()
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {

        if (FOR_LABEL != name) {
            return super.visitMethod(access, name, descriptor, signature, exceptions)
        }
        return LabelAdviceAdapter(
            api,
            super.visitMethod(access, name, descriptor, signature, exceptions),
            access,
            name,
            descriptor
        )

    }

}