package com.wj.gradle.seniorapplication.tasks.transform

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * Created by wenjing.liu on 2021/12/15 in J1.
 *
 * 为每个类添加Label的实例方法
 * 出现下面的内容见https://stackoverflow.com/questions/68982557/warning-when-building-app-expected-stack-map-table-for-method-with-non-linear
 * AGPBI: {"kind":"warning","text":"Expected stack map table for method with non-linear control flow.","sources":[{"file":"/Users/j1/Documents/android/code/GradlePlugin/app/build/intermediates/transforms/AutoLogTask/huawei/debug/57/com/wj/gradle/plugin/ByteCode.class"}],"tool":"D8"}
 *   methodVisitor.visitMaxs(2,2)可解决这个问题
 *  It seems that ClassWriter or ClassVisitor in ASM did not implement any method, there will be this warning, but that does not affect normal functionality.
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
        addSwitchLabelMethod()
        addTryCatchLabelMethod()
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
        methodVisitor.visitCode()
        methodVisitor.visitInsn(Opcodes.ICONST_3)
        methodVisitor.visitVarInsn(Opcodes.ISTORE, 1)
        //放入3
        val inForLabel = Label()
        methodVisitor.visitLabel(inForLabel)
        methodVisitor.visitVarInsn(Opcodes.ILOAD, 1)
        //放入10
        methodVisitor.visitIntInsn(Opcodes.BIPUSH, 10)

        val outForLabel = Label()
        methodVisitor.visitJumpInsn(Opcodes.IF_ICMPGE, outForLabel)
        //for循环里面的逻辑
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "java/lang/System",
            "currentTimeMillis",
            "()J",
            false
        )
        methodVisitor.visitInsn(Opcodes.POP2)
        //自增
        methodVisitor.visitIincInsn(1, 2)
        methodVisitor.visitJumpInsn(Opcodes.GOTO, inForLabel)

        methodVisitor.visitLabel(outForLabel)
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitMaxs(2, 2)
        methodVisitor.visitEnd()
    }

    /**
     *     private void switchLabelByteCode(int arg) {
     *       switch (arg) {
     *           case 10: {
     *               System.out.println("This is ten");
     *               break;
     *           }
     *           case 9: {
     *               System.out.println("This is nine");
     *              break;
     *           }
     *           default: {
     *               System.out.println("Not support");
     *           }
     *
     *       }
     *   }
     */
    private fun addSwitchLabelMethod() {
        val methodVisitor =
            visitor.visitMethod(Opcodes.ACC_PRIVATE, "switchLabelByteCode", "(I)V", null, null)
        methodVisitor.visitCode()
        methodVisitor.visitVarInsn(Opcodes.ILOAD, 1)
        //case三个条件
        val label10 = Label()
        val label9 = Label()
        val labelDefault = Label()
        val labelOutSwitch = Label()

        methodVisitor.visitLookupSwitchInsn(
            labelDefault,
            intArrayOf(10, 9),
            arrayOf(label10, label9)
        )
        //case 10
        methodVisitor.visitLabel(label10)
        methodVisitor.visitFieldInsn(
            Opcodes.GETSTATIC,
            "java/lang/System",
            "out",
            "Ljava/io/PrintSystem;"
        )
        methodVisitor.visitLdcInsn("This is ten")
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(Ljava/lang/String;)V",
            false
        )
        methodVisitor.visitJumpInsn(Opcodes.GOTO, labelOutSwitch)
        //case 9
        methodVisitor.visitLabel(label9)
        methodVisitor.visitFieldInsn(
            Opcodes.GETSTATIC,
            "java/lang/System",
            "out",
            "Ljava/io/PrintSystem;"
        )
        methodVisitor.visitLdcInsn("This is nine")
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(Ljava/lang/String;)V",
            false
        )
        methodVisitor.visitJumpInsn(Opcodes.GOTO, labelOutSwitch)
        //default
        methodVisitor.visitLabel(labelDefault)
        methodVisitor.visitFieldInsn(
            Opcodes.GETSTATIC,
            "java/lang/System",
            "out",
            "Ljava/io/PrintSystem;"
        )
        methodVisitor.visitLdcInsn("Not support")
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(Ljava/lang/String;)V",
            false
        )
        //labelOutSwitch
        methodVisitor.visitLabel(labelOutSwitch)
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitMaxs(2, 2)
        methodVisitor.visitEnd()
    }

    /**
     *
     *   private void tryCatchLabelByteCode() {
     *      try {
     *          Thread.sleep(1000);
     *       } catch (InterruptedException e) {
     *          e.printStackTrace();
     *      }
     *  }
     */
    private fun addTryCatchLabelMethod() {
        val methodVisitor =
            visitor.visitMethod(Opcodes.ACC_PRIVATE, "tryCatchLabelByteCode", "()V", null, null)
        methodVisitor.visitCode()
        val labelStart = Label()
        val labelEnd = Label()
        val labelHandler = Label()
        val labelOutTryCatch = Label()

        methodVisitor.visitTryCatchBlock(
            labelStart,
            labelEnd,
            labelHandler,
            "java/lang/InterruptedException"
        )
        //try 开始标识
        methodVisitor.visitLabel(labelStart)
        methodVisitor.visitLdcInsn(1000)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "java/lang/Thread",
            "sleep",
            "(I)V",
            false
        )
        //try 结束标识
        methodVisitor.visitLabel(labelEnd)
        //从try中强制跳出
        methodVisitor.visitJumpInsn(Opcodes.GOTO, labelOutTryCatch)
        //捕获到异常
        methodVisitor.visitLabel(labelHandler)
        methodVisitor.visitVarInsn(Opcodes.ASTORE, 1)
        //异常处理逻辑
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/InterruptedException",
            "printStackTrace",
            "()V",
            false
        )
        methodVisitor.visitLabel(labelOutTryCatch)
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitMaxs(2, 2)
        methodVisitor.visitEnd()
    }

}