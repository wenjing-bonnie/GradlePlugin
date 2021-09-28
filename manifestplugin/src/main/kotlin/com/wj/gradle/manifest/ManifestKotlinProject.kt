package com.wj.gradle.manifest

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by wenjing.liu on 2021/9/27 in J1.
 *
 * @author wenjing.liu
 */
class ManifestKotlinProject : Plugin<Project> {

    override fun apply(p0: Project) {
        println("Welcome ManifestKotlinProject")
    }
}