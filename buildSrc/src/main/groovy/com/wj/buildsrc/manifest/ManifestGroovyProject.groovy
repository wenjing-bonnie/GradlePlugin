package com.wj.buildsrc.manifest

import org.gradle.api.Plugin
import org.gradle.api.Project

class ManifestGroovyProject implements Plugin<Project> {
    @Override
    void apply(Project project) {
        println("Welcome ManifestGroovyProject in buildSrc")
    }
}