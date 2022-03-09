package com.wj.gradle.seniorapplication.tasks.transform.router

import com.wj.gradle.seniorapplication.utils.SystemPrint
import org.objectweb.asm.AnnotationVisitor


/**
 * create by wenjing.liu at 2022/2/23
 * 解析Router注解
 */
open class WjRouterAnnotationVisitor(
    api: Int,
    var routerClassName: String
) :
    AnnotationVisitor(api) {
    private val TAG = "WjRouterAnnotationVisitor"

    override fun visit(name: String?, value: Any?) {
        SystemPrint.outPrintln(
            TAG,
            "visit name = " + name + " , value = " + value + "  , routerClassName = " + routerClassName
        )
        if (name == null || value == null || value !is String) {
            return super.visit(name, value)
        }
        WjRouterMap.wjRouterMap.put(routerClassName, value)
        SystemPrint.outPrintln(TAG, "size = " + WjRouterMap.wjRouterMap.size)
        super.visit(name, value)
    }

//    override fun visitAnnotation(name: String?, descriptor: String?): AnnotationVisitor {
//        return super.visitAnnotation(name, descriptor)
//    }
//
//    override fun visitArray(name: String?): AnnotationVisitor {
//        return super.visitArray(name)
//    }
//
//    override fun visitEnum(name: String?, descriptor: String?, value: String?) {
//        super.visitEnum(name, descriptor, value)
//    }
//
//    override fun visitEnd() {
//        super.visitEnd()
//    }

}