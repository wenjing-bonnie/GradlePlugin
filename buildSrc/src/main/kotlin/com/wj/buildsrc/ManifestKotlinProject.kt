package com.wj.buildsrc

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by wenjing.liu on 2021/9/29 in J1.
 *
 * @author wenjing.liu
 */
class ManifestKotlinProject : Plugin<Project> {

    override fun apply(p0: Project) {
        println("Welcome ManifestKotlinProject in buildsrc")
    }
}