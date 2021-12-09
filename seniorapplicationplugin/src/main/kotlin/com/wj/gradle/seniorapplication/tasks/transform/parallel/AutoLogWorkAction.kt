package com.wj.gradle.seniorapplication.tasks.transform.parallel

import com.wj.gradle.seniorapplication.tasks.transform.AutoLogClassFileHandler
import org.gradle.workers.WorkAction

/**
 * Created by wenjing.liu on 2021/12/9 in J1.
 *
 * @author wenjing.liu
 */
abstract class AutoLogWorkAction : WorkAction<AutoLogWorkParameters> {
    val TAG = "AutoLogWorkAction"

    override fun execute() {
        val classFileHandler =
            AutoLogClassFileHandler(
                TAG,
                parameters.autoLogTimeout.get(),
            )
        classFileHandler.handlerDirectoryInputFiles(
            parameters.directoryInput.get(),
            parameters.outputFile.asFile.get()
        )
    }
}