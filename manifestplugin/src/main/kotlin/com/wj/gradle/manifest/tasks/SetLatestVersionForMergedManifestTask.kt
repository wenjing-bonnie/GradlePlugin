package com.wj.gradle.manifest.tasks

import com.android.utils.FileUtils
import com.wj.gradle.manifest.utils.SystemPrint
import groovy.util.Node
import groovy.util.XmlParser
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
        //the output is always up to date
        outputs.upToDateWhen { true }
        //https://www.tabnine.com/code/java/classes/org.gradle.api.internal.TaskOutputsInternal
//        outputs.upToDateWhen(object : Spec<Task> {
//            override fun isSatisfiedBy(p0: Task?): Boolean {
//                SystemPrint.errorPrintln(TAG, "Task name = " + p0.toString())
//                return true
//            }
//        })
    }

    /**
     * version的xml文件
     */
    private lateinit var inputVersionFile: File

    /**
     * 最终的Manifest文件
     */
    private lateinit var mainManifestFile: File

    /**
     * version name
     */
    private var versionName: String = ""

    /**
     * version code
     */
    private var versionCode: String = ""

    @InputFile
    open fun setMainMergedManifest(file: File) {
        this.mainManifestFile = file
    }

    @Incremental
    @InputFile
    open fun setInputVersionFile(file: File) {
        SystemPrint.outPrintln(
            TAG, "<<!!!  警告信息非error \n具体的版本管理文件的样式如下\n  " +
                    project.resources.text +
                    ">"
        )
        //var style:String = File()

        this.inputVersionFile = file
    }

    @TaskAction
    open fun runFullTaskAction(inputChanges: InputChanges) {
        if (!inputVersionFile.exists()) {
            return
        }
        ///Users/j1/Documents/android/code/GradlePlugin/app/verison.xml
        // determine which input files were out of date compared to a previous execution
        //是不是增量编译
        if (!inputChanges.isIncremental) {
            SystemPrint.outPrintln(TAG, "Not incremental build, clear the outputs ")
            cleanUpTaskOutputs()
        }
        runFullTaskAction()
    }

    /**
     * 全量编译
     */
    private fun runFullTaskAction() {
        SystemPrint.outPrintln(TAG, "Full action is Running ")
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
        SystemPrint.outPrintln(TAG, "versionCode: ${versionCode} , versionName: ${versionName}")

    }

    /**
     * 将versionName和versionCode写入到Manifest中
     */
    private fun writeVersionToMainManifest() {

    }

    /**
     * 删除所有的输出文件
     */
    private fun Task.cleanUpTaskOutputs() {
        for (file in outputs.files) {
            SystemPrint.outPrintln(file.absolutePath)
            if (file.isDirectory) {
                // Only clear output directory contents, keep the directory.
                FileUtils.deleteDirectoryContents(file)
            } else {
                FileUtils.deletePath(file)
            }
        }
    }
}