package com.wj.gradle.manifest.taskmanager

import com.wj.gradle.manifest.extensions.IncrementalExtension
import com.wj.gradle.manifest.extensions.ManifestKotlinExtension
import com.wj.gradle.manifest.tasks.android.CustomIncrementalTask
import com.wj.gradle.manifest.tasks.IncrementalOnDefaultTask
import com.wj.gradle.manifest.utils.SystemPrint
import org.gradle.api.Project
import org.gradle.api.Task
import java.nio.charset.Charset

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
        //doFirstForIncrementalOnDefaultTask(incremental)
        checkAndSetInputsOutputs(incremental)
        // doLastForIncrementalOnDefaultTask(incremental)
        preBuild.dependsOn(incremental)

        //testCustomIncrementalTask(preBuild)
    }

    private fun testCustomIncrementalTask(preBuild: Task) {
        var customIncremental = project.tasks.create(
            CustomIncrementalTask.TAG,
            CustomIncrementalTask::class.javaObjectType
        )
        customIncremental.variantName = variantName
        preBuild.dependsOn(customIncremental)
    }

    /**
     * 不做检查了，直接如果设置错误，Task就抛出异常更合理
     */
    private fun checkAndSetInputsOutputs(incremental: IncrementalOnDefaultTask) {
        incremental.testInputFile.set(getIncrementalExtension().inputFile())
        incremental.testInputFiles.from(getIncrementalExtension().inputFiles())
        incremental.testInputDir.set(getIncrementalExtension().inputDir())
        incremental.testOutFile.set(getIncrementalExtension().outputFile())
    }

    private fun doFirstForIncrementalOnDefaultTask(incremental: IncrementalOnDefaultTask) {
        incremental.doFirst {
            SystemPrint.outPrintln("do first change the output")
            incremental.testInputFile.get().asFile.writeText("12", Charset.defaultCharset())
        }
    }

    private fun doLastForIncrementalOnDefaultTask(incremental: IncrementalOnDefaultTask) {
        incremental.doLast {
            SystemPrint.outPrintln("do Last change the output")
            incremental.testOutFile.get().asFile.writeText("12", Charset.defaultCharset())
        }
    }


    private fun getIncrementalExtension(): IncrementalExtension {
        var extension = project.extensions.findByType(ManifestKotlinExtension::class.javaObjectType)
        if (extension == null) {
            return IncrementalExtension()
        }
        return extension.incremental()
    }
}