package com.wj.gradle.manifest.tasks

import com.android.utils.FileUtils
import com.android.utils.XmlUtils
import com.wj.gradle.manifest.utils.SystemPrint
import groovy.util.Node
import groovy.util.XmlParser
import groovy.xml.XmlUtil
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.execution.history.changes.IncrementalInputChanges
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File

/**
 * Created by wenjing.liu on 2021/10/18 in J1.
 *  针对最后的Manifest文件修改versionCode和versionName
 * @author wenjing.liu
 */
open class SetLatestVersionForMergedManifestTask : DefaultTask() {
    companion object {
        const val TAG = "SetLatestVersionForMergedManifest"
    }

    init {
        //https://blog.jdriven.com/2019/01/gradle-goodness-rerun-incremental-tasks-at-specific-intervals/
        //https://wiki.jikexueyuan.com/project/GradleUserGuide-Wiki/more_about_tasks/skipping_tasks_that_are_up-to-date.html
        // 计算任务的输出是否应被视为最新.
        //true:the output is always up to date;false:始终执行
        //如果一个任务只定义了输出, 如果输出不变的话, 它就会被视为 up-to-date.
        // 每次在任务执行之前, Gradle 都会为输入和输出取一个新的快照, 如果这个快照和之前的快照一样, Gradle 就会假定这个任务已经是最新的 (up-to-date) 并且跳过任务, 反之亦然
//        outputs.upToDateWhen {
//            SystemPrint.errorPrintln(TAG, "is = " + inputs.files.isEmpty + " , " + inputs.hasInputs)
//            //!inputs.hasInputs
//            false
//        }
        //https://www.tabnine.com/code/java/classes/org.gradle.api.internal.TaskOutputsInternal
//        outputs.upToDateWhen(object : Spec<Task> {
//            override fun isSatisfiedBy(p0: Task?): Boolean {
//                SystemPrint.errorPrintln(TAG, "Task name = " + p0.toString())
//                return true
//            }
//        })
        initInfo()
    }

    /**
     * version name
     */
    private var versionName: String = ""

    /**
     * version code
     */
    private var versionCode: String = ""


    private fun initInfo() {
        SystemPrint.errorPrintln(
            TAG, "!!!  警告信息非error !!!!  \n    具体的版本管理文件的样式如下\n  " +
                    "<versions>\n" +
                    "    <version latest=\"true\">\n" +
                    "        <versionDescription>新增购物车</versionDescription>\n" +
                    "        <versionCode>12</versionCode>\n" +
                    "        <versionName>2.0.0</versionName>\n" +
                    "        <date>2021/09/16</date>\n" +
                    "    </version>\n" +
                    "    <version>\n" +
                    "        <versionDescription>APP第一版本上线</versionDescription>\n" +
                    "        <versionCode>12</versionCode>\n" +
                    "        <versionName>1.0.0</versionName>\n" +
                    "        <date>2021/09/15</date>\n" +
                    "    </version>\n" +
                    "</versions> \n"

        )
        SystemPrint.errorPrintln(TAG, "!!!  警告信息非error !!!!  \n")
    }

    @TaskAction
    open fun runFullTaskAction(inputChanges: InputChanges) {
        ///Users/j1/Documents/android/code/GradlePlugin/app/verison.xml
        // determine which input files were out of date compared to a previous execution
        //是不是增量编译
        SystemPrint.outPrintln(
            TAG,
            "input  isEmpty = " + inputs.files.isEmpty + " , hasInputs = " + inputs.hasInputs
        )
//        inputs.files.forEach {
//            SystemPrint.outPrintln(TAG, "path = " + it.absolutePath)
//        }
//        inputChanges.getFileChanges(inputs.files).forEach {
//            SystemPrint.outPrintln(TAG, "type = " + it.changeType)
//        }
//        if (!inputChanges.isIncremental) {
//            SystemPrint.outPrintln(TAG, "input not incremental , not need to change ")
//            return
//        }
        SystemPrint.errorPrintln(TAG, "isIncremental  = " + inputChanges.isIncremental)
        runFullTaskAction()
    }

    /**
     * 全量编译
     */
    private fun runFullTaskAction() {
        if (!inputs.hasInputs) {
            SystemPrint.outPrintln(
                TAG,
                "NO-SOURCE interrupt the task , because input.hasInputs is false"
            )
            return
        }
        handlerVersionForMainManifest()
    }

    /**
     * 读写version信息
     */
    private fun handlerVersionForMainManifest() {
        readVersionFromVersionManager()
        writeVersionToMainManifest()
    }

    /**
     * 从配置的xml中
     */
    private fun readVersionFromVersionManager() {
        var inputVersionFile = getVersionFileFromInputs()
        if (inputVersionFile == null) {
            SystemPrint.outPrintln(
                TAG,
                "NO-SOURCE interrupt the task , because input is null when read xml"
            )
            return
        }
        var xmlParser = XmlParser()
        var node = xmlParser.parse(inputVersionFile)
        for (version in node.children()) {
            if (hasLatestVersionTag(version as Node)) {
                readVersionFromLatestNode(version)
                return
            }
        }
    }

    /**
     * 是否含有"latest"的属性的version
     */
    private fun hasLatestVersionTag(version: Node): Boolean {
        var attrs = version.attributes()
        for (key in attrs.keys) {
            if ("latest" == key.toString()) {
                return true
            }
        }
        return false
    }

    /**
     * 从"latest"的version中读取versionName和versionCode
     */
    private fun readVersionFromLatestNode(latest: Node) {
        for (child in latest.children()) {
            if (child !is Node) {
                continue
            }
            var name = child.name().toString()
            if (name == "versionCode") {
                versionCode = child.text()
            }
            if (name == "versionName") {
                versionName = child.text()
            }
        }
    }

    /**
     * 将versionName和versionCode写入到Manifest中
     */
    private fun writeVersionToMainManifest() {
        var mainManifestFile = getMainManifestFileFromOutputs()
        if (mainManifestFile == null) {
            SystemPrint.errorPrintln(
                TAG,
                "No Main Manifest xml , update the version FAILED , rebuild"
            )
            return
        }
        SystemPrint.outPrintln(
            TAG,
            "正在将 versionCode: ${versionCode} , versionName: ${versionName} 写入到 \n " +
                    "${mainManifestFile.absolutePath}"
        )
        var xmlParser = XmlParser()
        var node = xmlParser.parse(mainManifestFile)
        var count = 0
        var attrs = node.attributes()
        for (key in attrs.keys) {
            if (key.toString().endsWith("versionCode")) {
                node.attributes().replace(key, versionCode)
                SystemPrint.outPrintln(TAG, node.attribute(key).toString())
                count++
                continue
            }
            if (key.toString().endsWith("versionName")) {
                node.attributes().replace(key, versionName)
                SystemPrint.outPrintln(TAG, node.attribute(key).toString())

                count++
                continue
            }
        }
        if (count == 2) {
            //写入到原manifest文件中
            var result = XmlUtil.serialize(node)
            mainManifestFile.writeText(result, Charsets.UTF_8)
            SystemPrint.outPrintln(TAG, "写入成功")
            return
        }
    }

    /**
     * 删除所有的输出文件
     */
    private fun Task.cleanUpTaskOutputs() {
        for (file in outputs.files) {
            if (file.isDirectory) {
                // Only clear output directory contents, keep the directory.
                FileUtils.deleteDirectoryContents(file)
            } else {
                FileUtils.deletePath(file)
            }
        }
    }

    private fun getVersionFileFromInputs(): File? {
        for (file in inputs.files) {
            if (!file.exists()) {
                continue
            }
            return file
        }
        return null
    }

    private fun getMainManifestFileFromOutputs(): File? {
        for (file in outputs.files) {
            if (!file.exists()) {
                continue
            }
            return file
        }
        return null
    }

}