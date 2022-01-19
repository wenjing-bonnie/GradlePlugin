package com.wj.gradle.seniorapplication.taskmanager

import com.wj.gradle.seniorapplication.extensions.IncrementalExtension
import com.wj.gradle.seniorapplication.extensions.SeniorApplicationKotlinExtension
import com.wj.gradle.seniorapplication.tasks.others.IncrementalOnDefaultTask
import com.wj.gradle.seniorapplication.utils.SystemPrint
import org.gradle.api.Project
import java.nio.charset.Charset

/**
 * Created by wenjing.liu on 2021/10/21 in J1.
 *
 * 测试添加增量编译的Task
 *
 * @author wenjing.liu
 */
open class AddIncrementalTaskManager(
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
        setInputsOutputs(incremental)
        // doLastForIncrementalOnDefaultTask(incremental)
        preBuild.dependsOn(incremental)
    }

    /**
     * 不做检查了，直接如果设置错误，Task就抛出异常更合理
     */
    private fun setInputsOutputs(incremental: IncrementalOnDefaultTask) {
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
        var extension = project.extensions.findByType(SeniorApplicationKotlinExtension::class.javaObjectType)
        if (extension == null) {
            return IncrementalExtension()
        }
        return extension.incremental()
    }
}