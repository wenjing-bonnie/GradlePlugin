package com.wj.gradle.apkprotect.tasks.signed

import com.wj.gradle.apkprotect.tasks.base.NewIncrementalWithoutOutputsTask
import com.wj.gradle.apkprotect.utils.AppProtectRuntimeUtils
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File

/**
 * 对Apk签名对齐
 */
abstract class ApkAlignAndSignedIncrementalTask : NewIncrementalWithoutOutputsTask() {
    companion object {
        val TAG = "ApkAlignAndSignedIncrementalTask"
    }

    @get:InputDirectory
    @get:Incremental
    abstract val apkUnsignedDirectory: DirectoryProperty

    abstract val keystoreProperty: Property<String>
    abstract val keyAliasProperty: Property<String>
    abstract val storePassProperty: Property<String>
    abstract val keyPassProperty: Property<String>

    override fun doTaskAction(inputChanges: InputChanges) {
        val workQueue = workerExecutor.noIsolation()
        val apks = apkUnsignedDirectory.get().asFile.listFiles()
        for (apk in apks) {
            //SystemPrint.outPrintln(TAG, apk.absolutePath)
            if (apk.isDirectory || !apk.name.endsWith(".apk")) {
                continue
            }
            // Could not serialize value of type DefaultProject，所以不使用并行Task
//            workQueue.submit(ApkAlignAndSignedAction::class.javaObjectType) {
//                it.apkUnsignedFile.set(apk)
//                it.project.set(project)
//            }
            zipAlign(apk)
            apkSigned(apk)
        }
    }

    /**
     * 对齐 https://developer.android.google.cn/studio/command-line/zipalign.html
     * zipalign 是对zip包对齐的工具,使APK包内未压缩的数据有序排列对齐,从而减少APP运行时内存消耗
     * zipalign可以在V1签名后执行, 但zipalign不能在V2签名后执行,只能在V2签名之前执行！！！
     * //zipalign -p -f -v 4 infile.apk outfile.apk
     * //如需确认 existing.apk 的对齐方式，请运行以下命令：
     * //zipalign -c -v 4 existing.apk
     * TODO 还没有处理！！！！！ 2022/03/09
     * Installation failed due to: '-124: Failed parse during installPackageLI: Targeting R+ (version 30 and above) requires the resources.arsc of installed APKs to be stored uncompressed and aligned on a 4-byte boundary'
     */
    private fun zipAlign(apkUnsignedFile: File) {
        val originalApk = apkUnsignedFile.name
        val signApk = "${apkUnsignedFile.parent}/sign-${originalApk}"
        val command =
            "zipalign -p -f -v 4 ${apkUnsignedFile.absolutePath} $signApk"
        val error = AppProtectRuntimeUtils.runtimeExecCommand(command, project)
        val okValue = "zip align is ok !"
        AppProtectRuntimeUtils.printRuntimeResult(error, okValue)

        ///确认对齐结果命令，按需使用
        val confirmCommand = "zipalign -c -v 4 ${signApk}"
        val confirmError = AppProtectRuntimeUtils.runtimeExecCommand(confirmCommand, project)
        val confirmOkValue = "zip align confirmed ok !"
        AppProtectRuntimeUtils.printRuntimeResult(confirmError, confirmOkValue)

        val deleteCommand = "rm ${apkUnsignedFile.absolutePath}"
        val deleteError = AppProtectRuntimeUtils.runtimeExecCommand(deleteCommand)
        val deleteErrorOkValue = "delete is ok !"
        AppProtectRuntimeUtils.printRuntimeResult(deleteError, deleteErrorOkValue)

        File(signApk).renameTo(apkUnsignedFile)
    }

    /**
     * 签名 https://developer.android.google.cn/studio/command-line/apksigner
     * apksigner sign --ks 密钥库名 --ks-key-alias 密钥别名 xxx.apk
     *
     * 默认的debug模式的签名在/Users/liuwenjing/.android/debug.keystore
     * Keystore name: “debug.keystore”
     * Keystore password: “android”
     * Key alias: “androiddebugkey”
     * Key password: “android”
     */
    private fun apkSigned(apkUnsignedFile: File) {
        val keystore = keystoreProperty.get() //"/Users/liuwenjing/.android/debug.keystore"
        val keyAlias = keyAliasProperty.get()//"androiddebugkey"
        val keyStorePass = storePassProperty.get() //"android"
        val keyPass = keyPassProperty.get() //"android"
        val command =
            "apksigner sign --ks ${keystore} --ks-key-alias ${keyAlias} --ks-pass pass:${keyStorePass} --key-pass pass:${keyPass}  ${apkUnsignedFile.absolutePath}"
        val error = AppProtectRuntimeUtils.runtimeExecCommand(command, project)
        val okValue = "apk signed is ok !"
        AppProtectRuntimeUtils.printRuntimeResult(error, okValue)
    }
}