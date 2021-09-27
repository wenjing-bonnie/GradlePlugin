package com.wj.gradle.manifest

import org.gradle.api.Plugin
import org.gradle.api.Project

class ManifestGroovyProject implements Plugin<Project> {
    @Override
    void apply(Project project) {
        System.out.println("Welcome ManifestGroovyProject")
    }
}