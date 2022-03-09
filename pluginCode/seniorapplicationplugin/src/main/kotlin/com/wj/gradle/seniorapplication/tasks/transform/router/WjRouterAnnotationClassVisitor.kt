package com.wj.gradle.seniorapplication.tasks.transform.router

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by wenjing.liu on 2021/12/20 in J1.
 *  解析注解
 * @author wenjing.liu
 */
open class WjRouterAnnotationClassVisitor(val visitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM9, visitor) {
    val TAG = "AnnotationClassVisitor"
    private var routerClassName: String? = null

    // Lcom/wj/gradle/plugin/annotation/WjRouter
    val wjRouterDescriptor = "WjRouter;"

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        if (descriptor == null || !descriptor.endsWith(wjRouterDescriptor) || routerClassName == null) {
            return super.visitAnnotation(descriptor, visible)
        }
        return WjRouterAnnotationVisitor(api, routerClassName!!)
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        routerClassName = name
        super.visit(version, access, name, signature, superName, interfaces)
    }
}