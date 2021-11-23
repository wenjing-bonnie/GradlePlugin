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
 * @author wenjing.liu
 */
open class AutoLogClassVisitor : ClassVisitor(Opcodes.ASM7) {

    val TAG = "AutoLogClassVisitor"

    /**
     * 扫描.class开始时回调的第一个方法
     */
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        SystemPrint.outPrintln(TAG, " -- visit -- ")
    }

    override fun visitSource(source: String?, debug: String?) {
        super.visitSource(source, debug)
        SystemPrint.outPrintln(TAG, " -- visitSource -- ")

    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        SystemPrint.outPrintln(TAG, " -- visitMethod -- ${name}")
        return AutoLogMethodVisitor(api)
    }


    override fun visitEnd() {
        super.visitEnd()
        SystemPrint.outPrintln(TAG, " -- visitEnd -- ")
    }


}