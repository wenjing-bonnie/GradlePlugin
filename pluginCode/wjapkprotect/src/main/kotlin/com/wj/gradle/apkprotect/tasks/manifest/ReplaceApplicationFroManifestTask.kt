package com.wj.gradle.apkprotect.tasks.manifest

import com.wj.gradle.apkprotect.tasks.base.NewIncrementalWithoutOutputsTask
import com.wj.gradle.base.utils.SystemPrint
import groovy.util.Node
import groovy.util.XmlParser
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFile
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File
import java.lang.IllegalStateException

/**
 * created by wenjing.liu at 2022/2/12
 * 将原App的Application替换A成壳的Application
 * 依赖于processDebugManifest：为所有的变体生成最终的Manifest
 */
abstract class ReplaceApplicationFroManifestTask : NewIncrementalWithoutOutputsTask() {

    companion object {
        const val TAG = "ReplaceApplicationFroManifestTask"
    }
    val applicationName = ""
    @get:InputFile
    @get:Incremental
    abstract val mergedManifestFile: RegularFileProperty

    /**
     * 解密壳的application的名字
     */
    abstract val shellApplicationName: Property<String>


    override fun doTaskAction(inputChanges: InputChanges) {

        val manifestFile = mergedManifestFile.get().asFile
        if (!manifestFile.exists()) {
            return
        }
        //找到Application结点
        val application = readApplicationNameFromManifest(manifestFile)
            ?: throw IllegalStateException("The manifest not right ,can not find application")

    }

    private fun readApplicationNameFromManifest(manifestFile: File): Node? {
        val xmlParser = XmlParser()
        //得到所有的结点树
        val node = xmlParser.parse(manifestFile)
        //node.attributes();获取的一级内容<?xml> <manifest>里设置的内容如:key为package、encoding,value为对应的值
        //node.children();获取的二级内容 <application> <uses-sdk>
        val firstChilds = node.children()
        //从集合中找到Application结点
        for (child in firstChilds) {
            if (!isRightNode(child, "application")) {
                continue
            }
            //找到application这个结点,修改里面的name属性
            val application = child as Node
            SystemPrint.outPrintln(application.attributes().toString())
            return application
        }
        return null
    }


    private fun isRightNode(node: Any?, name: String): Boolean {
        return node is Node && node.name().equals(name)
    }
}