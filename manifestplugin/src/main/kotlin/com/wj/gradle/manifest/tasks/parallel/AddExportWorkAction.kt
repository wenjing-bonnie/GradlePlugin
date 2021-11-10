package com.wj.gradle.manifest.tasks.parallel

import com.wj.gradle.manifest.utils.SystemPrint
import groovy.util.Node
import groovy.util.XmlParser
import groovy.xml.XmlUtil
import org.gradle.workers.WorkAction
import java.io.File

/**
 * Created by wenjing.liu on 2021/11/10 in J1.
 *
 * 为所有的未适配Android 12 exported:true属性的组件添加
 * WorkAction:完成单个manifest文件的添加功能
 * @author wenjing.liu
 */
abstract class AddExportWorkAction : WorkAction<AddExportWorkParameters> {
    private val TAG = "AddExportWorkAction"
    private val ATTRUBUTE_EXPORTED: String = "{http://schemas.android.com/apk/res/android}exported"
    private val ATTRUBUTE_NAME: String = "{http://schemas.android.com/apk/res/android}name"
    private var isPrintThreadName = false

    override fun execute() {
        isPrintThreadName = false
        val manifestFile: File = parameters.inputManifestFile.get().asFile
        readAndWriteManifestForExported(manifestFile)
    }

    /**
     * 处理非app下的manifest文件
     */
    private fun readAndWriteManifestForExported(manifest: File) {
        if (!manifest.exists()) {
            return
        }
        val node = readAndResetComponentFromManifest(manifest)
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
        //从集合中找到application的结点
        for (child in firstChildren) {
            if (notFindRightNode(child, "application")) {
                continue
            }
            //选择"application"这个结点
            var application = (child as Node).children()
            //从集合中找到里面的activity、service、receiver结点
            for (component in application) {
                if (notFindRightNode(component, "activity") &&
                    notFindRightNode(component, "service") &&
                    notFindRightNode(component, "receiver")
                ) {
                    continue
                }
                //处理activity、service、receiver结点的属性
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
            //处理含有intent-filter所在的父结点上添加android:exported属性
            var name = attributeWithoutExportedName(node)
            handlerNodeAddExported(node, name)
        }

    }

    /**
     * 为符合条件的node添加android:exported
     */
    private fun handlerNodeAddExported(node: Node, name: String) {
        if (parameters.isOnlyBuildError) {
            handlerNodeAddExportedForMainManifest(name)
            return
        }
        handlerNodeAddExportedForPackagedManifest(node, name)
    }

    /**
     * 处理app的manifest文件,仅做报错信息提示
     */
    private fun handlerNodeAddExportedForMainManifest(name: String) {
        printExecutedThread()
        SystemPrint.errorPrintln(
            TAG, "<<!!! error \n " +
                    "必须为 < $name > 添加android:exported属性,错误原因见Build Output的编译错误或https://developer.android.com/guide/topics/manifest/activity-element#exported  >>\n"
        )
    }

    /**
     * 处理被打包到APP的其他manifest文件中添加android:exported
     */
    private fun handlerNodeAddExportedForPackagedManifest(node: Node, name: String) {
        printExecutedThread()
        SystemPrint.outPrintln(
            TAG,
            "为 < $name > 添加android:exported=true"
        )
        node.attributes()["android:exported"] = true
    }


    /**
     * 将更新之后的node重新写入原文件
     */
    private fun writeComponentToManifest(manifest: File, node: Node) {
        if (parameters.isOnlyBuildError) {
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
                ((name) != node.name().toString())
    }

    /**
     * 该结点中含有android:exported属性
     */
    private fun hasAttributeExportedInNode(node: Node): Boolean {
        var attributes = node.attributes()
        for (key in attributes.keys) {
            if (ATTRUBUTE_EXPORTED == key.toString()) {
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
            if (ATTRUBUTE_NAME == key.toString()) {
                return attributes[key].toString()
            }
        }
        return ""
    }

    private fun printExecutedThread() {
        if (isPrintThreadName) {
            return
        }
        SystemPrint.outPrintln(TAG, "${Thread.currentThread().name} is executed !")
        isPrintThreadName = true
    }
}