package com.wj.gradle.manifest.taskmanager

import com.wj.gradle.manifest.tasks.IncrementalOnDefaultTask
import org.gradle.api.Project
import java.io.File

/**
 * Created by wenjing.liu on 2021/10/21 in J1.
 *
 * 测试添加增量编译的Task
 *
 * @author wenjing.liu
 */
open class TestAddTaskDependsPreBuilderManager(
    var project: Project,
    var variantName: String
) {
    /**
     * 测试添加[IncrementalOnDefaultTask]
     */
    open fun testIncrementalOnDefaultTask() {
        var preBuild = project.tasks.getByName("preBuild")

        var incremental = project.tasks.create(
            IncrementalOnDefaultTask.TAG,
            IncrementalOnDefaultTask::class.javaObjectType
        )
        var path1 = "${project.buildDir}/test/1.txt"
        var path2 = "${project.buildDir}/test/2.txt"
        var path3 = "${project.buildDir}/test/3.txt"
        var sets = setOf(File(path1), File(path2))

//        incremental.doFirst {
//            SystemPrint.outPrintln("do first")
//            sets.plus(File(path3))
//        }
        incremental.testInputFiles.from(sets)
        incremental.testOutputDir.set(File("${project.buildDir}/outputs"))

        preBuild.dependsOn(incremental)
    }
}