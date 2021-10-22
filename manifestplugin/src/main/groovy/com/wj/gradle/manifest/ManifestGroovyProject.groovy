package com.wj.gradle.manifest

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class ManifestGroovyProject implements Plugin<Project> {
    @Override
    void apply(Project project) {
        System.out.println("Welcome ManifestGroovyProject")

        project.getExtensions().create(Extensions.TAG, Extensions)

        IncrementalAnnotationGroovyTask task = project.tasks.create("IncrementalAnnotationGroovyTask", IncrementalAnnotationGroovyTask, {
            inputDir = project.buildDir
            outputDir = project.buildDir
        })
        Task preTask = project.tasks.getByName("preBuild")
        preTask.dependsOn(task)
    }
}