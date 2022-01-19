package com.wj.gradle.seniorapplication.tasks.transform

import com.wj.gradle.seniorapplication.utils.SystemPrint
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.TypePath

/**
 * Created by wenjing.liu on 2021/12/20 in J1.
 *  解析注解
 * @author wenjing.liu
 */
open class AnnotationClassVisitor(val visitor: ClassVisitor) : ClassVisitor(Opcodes.ASM9, visitor) {
    val TAG = "AnnotationClassVisitor"

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        SystemPrint.outPrintln(TAG, "visitAnnotation descriptor = " + descriptor + " , visible = " + visible)
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitTypeAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor {
        SystemPrint.outPrintln(TAG, "visitAnnotation descriptor = " + descriptor + " , visible = " + visible)
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible)
    }
}