package com.wj.gradle.seniorapplication.tasks.transform

import com.wj.gradle.seniorapplication.utils.SystemPrint
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by wenjing.liu on 2021/11/22 in J1.
 * 自动为每个方法添加公共日志
 * TODO 需要考虑含有if/return的情况
 * TODO api会影响到方法的code属性中是否存在stackmaptable，这个应该需要配合reader和writer的flag来设置
 * @author wenjing.liu
 */
open class AutoLogClassVisitor(visitor: ClassVisitor) : ClassVisitor(Opcodes.ASM9, visitor) {

    val TAG = "AutoLogClassVisitor"

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        SystemPrint.errorPrintln(TAG, "method is ${name}")
        return AutoLogAdviceAdapter(
            api,
            super.visitMethod(access, name, descriptor, signature, exceptions),
            access,
            name,
            descriptor
        )
    }

}