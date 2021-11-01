package com.wj.gradle.manifest.extensions

import org.gradle.api.file.FileCollection
import java.io.File

/**
 * Created by wenjing.liu on 2021/10/25 in J1.
 *
 * [IncrementalOnDefaultTask]配置inputs和outputs
 *
 * @author wenjing.liu
 */
open class IncrementalExtension {
    private var inputFile: File? = null
    private var inputFiles: FileCollection? = null
    private var inputDir: File? = null
    private var outputFile: File? = null

    open fun inputFile(file: File) {
        this.inputFile = file
    }

    open fun inputFile(): File? {
        return this.inputFile
    }

    open fun inputFiles(collection: FileCollection) {
        this.inputFiles = collection
    }

    open fun inputFiles(): FileCollection? {
        return this.inputFiles
    }

    open fun inputDir(file: File) {
        this.inputDir = file
    }

    open fun inputDir(): File? {
        return this.inputDir
    }

    open fun outputFile(file: File) {
        this.outputFile = file
    }

    open fun outputFile(): File? {
        return this.outputFile
    }

    override fun toString(): String {
        return "inputFile path is \n ${inputFile()?.absoluteFile}\n" +
                "inputFiles size is \n ${inputFiles()?.files?.size}\n" +
                "inputDir path is \n ${inputDir()?.absoluteFile}\n" +
                "outputFile path is \n ${outputFile()?.absoluteFile}\n"
    }
}