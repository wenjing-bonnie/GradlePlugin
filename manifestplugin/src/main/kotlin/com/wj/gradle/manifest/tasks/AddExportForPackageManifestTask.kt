package com.wj.gradle.manifest.tasks

import com.wj.gradle.manifest.utils.SystemPrint
import groovy.util.Node
import groovy.util.XmlParser
import groovy.xml.XmlUtil
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Created by wenjing.liu on 2021/9/30 in J1.
 *
 * 用来适配Android12,自动为没有适配Android12的组件添加android:exported的属性
 * 输入所有的被打包到APP的manifest文件以及app这个module下对应的manifest文件
 * 执行该任务之后,所有符合条件的组件都会添加android:exported
 *
 * @author wenjing.liu
 */

open class AddExportForPackageManifestTask : DefaultTask() {
    companion object {
        const val TAG: String = "AddExportForPackageManifest"
    }

    private val ATTRUBUTE_EXPORTED: String = "{http://schemas.android.com/apk/res/android}exported"
    private val ATTRUBUTE_NAME: String = "{http://schemas.android.com/apk/res/android}name"

    /**
     * 所有的被打包到APP的manifest文件,但不包括app下的manifest文件
     */
    private lateinit var inputManifests: FileCollection

    /**
     * app下对应的manifest文件
     */
    private lateinit var inputMainManifest: File

    /**
     * 是否在处理app下的manifest文件,如果是app下的manifest文件只报错提示,不处理
     */
    private var isHandlerMainManifest: Boolean = false


    open fun setInputManifests(input: FileCollection) {
        this.inputManifests = input
    }

    open fun setInputMainManifest(input: File) {
        this.inputMainManifest = input
    }

    @TaskAction
    fun doTaskAction() {
        handlerNonMainManifest()
        println()
        handlerMainManifest()
    }

    /**
     * 非app下的manifest文件
     */
    private fun handlerNonMainManifest() {
        isHandlerMainManifest = false
        SystemPrint.errorPrintln(
            TAG,
            "<<!!!  警告信息非error \n" +
                    "开始为 \"所有被打包到APP的manifest文件\" 检查和增加 \"android:exported\"\n" +
                    "因为操作的第三方的manifest,所以该属性为true   >>"
        )
        for (input in inputManifests) {
            readAndWriteManifestForExported(input)
        }
    }

    /**
     * 处理主app下的manifest文件
     */
    private fun handlerMainManifest() {
        isHandlerMainManifest = true
        SystemPrint.errorPrintln(
            TAG,
            "<<!!!  警告信息非error \n" +
                    "开始为 \"app的manifest文件\" 检查和报错提示 \"android:exported\"\n" +
                    "开发者需要根据报错的组件,按照实际开发需要设置属性值   >>"
        )
        readAndWriteManifestForExported(inputMainManifest)
    }

    /**
     * 处理非app下的manifest文件
     */
    private fun readAndWriteManifestForExported(manifest: File) {
        if (!manifest.exists()) {
            return
        }
        var node = readAndResetComponentFromManifest(manifest)
        writeComponentToManifest(manifest, node)
    }

    /**
     * 读取manifest文件下的所有内容,存放到node中
     */
    private fun readAndResetComponentFromManifest(manifest: File): Node {
        var xmlParser = XmlParser()
        //得到所有的结点树
        var node = xmlParser.parse(manifest)
        //node.attributes();获取的一级内容<?xml> <manifest>里设置的内容如:key为package、encoding,value为对应的值
        //node.children();获取的二级内容 <application> <uses-sdk>

        var firstChildren = node.children()
        for (application in firstChildren) {
            if (notFindRightNode(application, "application")) {
                continue
            }
            //选择"application"这个结点,找到里面的activity结点
            var secondChild = (application as Node).children()
            for (component in secondChild) {
                if (notFindRightNode(component, "activity")) {
                    continue
                }
                //处理activity结点的属性
                handlerNodeWithoutExported(component as Node)
            }
        }

        return node
    }

    /**
     * 处理没有android:exported的component
     */
    private fun handlerNodeWithoutExported(node: Node) {
        //已经含有android:exported
        if (hasAttributeExportedInNode(node)) {
            SystemPrint.outPrintln(TAG, "已经含有\"android:exported\"")
            return
        }
        for (intentFilter in node.children()) {
            //没有intent-filter
            if (notFindRightNode(intentFilter, "intent-filter")) {
                continue
            }
            var name = attributeWithoutExportedName(node)
            handlerNodeAddExported(node, name)
        }

    }

    /**
     * 为符合条件的node添加android:exported
     */
    private fun handlerNodeAddExported(node: Node, name: String) {
        if (isHandlerMainManifest) {
            handlerNodeAddExportedForMainManifest(name)
            return
        }
        handlerNodeAddExportedForPackagedManifest(node, name)
    }

    /**
     * 处理app的manifest文件,仅做报错信息提示
     */
    private fun handlerNodeAddExportedForMainManifest(name: String) {
        SystemPrint.errorPrintln(
            TAG, "<<!!! error \n " +
                    "必须为 < ${name} > 添加android:exported属性,错误原因见Build Output的编译错误或https://developer.android.com/guide/topics/manifest/activity-element#exported  >>"
        )
    }

    /**
     * 处理被打包到APP的其他manifest文件中添加android:exported
     */
    private fun handlerNodeAddExportedForPackagedManifest(node: Node, name: String) {
        SystemPrint.outPrintln(TAG, "为 < ${name} > 添加android:exported=true")
        node.attributes().put("android:exported", true)
    }


    /**
     * 将更新之后的node重新写入原文件
     */
    private fun writeComponentToManifest(manifest: File, node: Node) {
        if (isHandlerMainManifest) {
            //如果是主app的manifest文件,只报错,不改写,需要开发者自行配置
            return
        }
        var result = XmlUtil.serialize(node)
        //重新写入原文件
        manifest.writeText(result, Charsets.UTF_8)
    }

    /**
     * 判断是不是想要的结点
     * @param node
     * @param name:Node对应的关键字
     * @return true:不符合条件;false:符合给定[name] && 并且是一个结点
     */
    private fun notFindRightNode(node: Any?, name: String): Boolean {
        return node !is Node ||
                (!(name).equals(node.name().toString()))
    }

    /**
     * 该结点中含有android:exported属性
     */
    private fun hasAttributeExportedInNode(node: Node): Boolean {
        var attributes = node.attributes()
        for (key in attributes.keys) {
            if (ATTRUBUTE_EXPORTED.equals(key.toString())) {
                return true
            }
        }
        return false
    }

    /**
     * 获取android:name对应的属性值
     */
    private fun attributeWithoutExportedName(node: Node): String {
        var attributes = node.attributes()
        for (key in attributes.keys) {
            if (ATTRUBUTE_NAME.equals(key.toString())) {
                return attributes.get(key).toString()
            }
        }
        return ""
    }

}