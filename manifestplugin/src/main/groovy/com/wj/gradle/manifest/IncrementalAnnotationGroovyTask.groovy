package com.wj.gradle.manifest


import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

abstract class IncrementalAnnotationGroovyTask extends DefaultTask {


    @Incremental
    @InputDirectory
    abstract DirectoryProperty getInputDir()

    @Incremental
    @OutputDirectory
    abstract DirectoryProperty getOutputDir()

    @TaskAction
    void runTaskAction(InputChanges inputChanges) {
        println("running .....")

    }
}