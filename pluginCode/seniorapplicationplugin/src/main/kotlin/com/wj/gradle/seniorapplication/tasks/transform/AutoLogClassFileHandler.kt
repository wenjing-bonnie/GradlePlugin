package com.wj.gradle.seniorapplication.tasks.transform

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.utils.FileUtils
import com.wj.gradle.seniorapplication.utils.SystemPrint
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Created by wenjing.liu on 2021/12/9 in J1.
 * 用来处理directory中的.class文件
 * @author wenjing.liu
 */
open class AutoLogClassFileHandler(
    private val tag: String,
    private val timeout: Long
) {

    /**
     * 用来处理directory中的.class文件
     * @param directory 文件夹
     * @param outputFile 输出文件
     */
    open fun handlerDirectoryInputFiles(directory: DirectoryInput, outputFile: File?) {
        //处理文件
        handlerDirectoryInputFiles(directory.file)
        //写入文件
        writeOutputFile(directory, Format.DIRECTORY, outputFile)
    }

    /**
     * 处理所有文件夹里的.class文件
     */
    private fun handlerDirectoryInputFiles(inputFile: File) {
        if (inputFile.isDirectory) {
            var filesInDirectory = inputFile.listFiles()
            filesInDirectory.forEach {
                handlerDirectoryInputFiles(it)
            }
        } else {
            handlerDirectoryInputClassFileByAsm(inputFile)
        }
    }

    /**
     * 处理单个的.class文件
     */
    private fun handlerDirectoryInputClassFileByAsm(inputClassFile: File) {
        SystemPrint.outPrintln(tag, "handler .class is \n${inputClassFile.absolutePath}\n")
        if (!inputClassFile.name.endsWith(".class")) {
            return
        }
        //TODO  test
//        if (!inputClassFile.name.endsWith("ByteCode.class")) {
//            return
//        }
        //1.创建ClassReader
        val fis = FileInputStream(inputClassFile)
        val classReader = ClassReader(fis)

        //2.创建ClassWriter
        val classWriter =
            ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES)
        //3.实例化自定义的AutoLogClassVisitor
        val autoLogClassVisitor = AutoLogClassVisitor(classWriter, timeout)
        val labelClassVisitor = LabelClassVisitor(autoLogClassVisitor)
        val annotationClassVisitor = AnnotationClassVisitor(labelClassVisitor)
        //4.注册AutoLogClassVisitor
        classReader.accept(annotationClassVisitor, ClassReader.SKIP_FRAMES)

        //5.将修改之后的.class文件重新写入到该文件中
        val fos = FileOutputStream(inputClassFile)
        fos.write(classWriter.toByteArray())
        fos.close()
        fis.close()
    }

    /**
     * 复制文件内容到目标文件
     */
    open fun writeOutputFile(
        input: QualifiedContent, format: Format, outputFile: File?
    ) {
        if (outputFile == null) {
            return
        }
        if (format == Format.JAR) {
            FileUtils.copyFile(input.file, outputFile)
        } else {
            FileUtils.copyDirectory(input.file, outputFile)
        }
    }


}