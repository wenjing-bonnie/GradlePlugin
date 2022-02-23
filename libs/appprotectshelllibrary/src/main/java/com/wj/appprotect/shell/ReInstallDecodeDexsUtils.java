package com.wj.appprotect.shell;

import android.app.Application;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexFile;

/**
 * created by wenjing.liu at 2022/2/6
 * 1.获取当前应用的pathclassloader
 * 2.反射获取DexPathList的属性对象pathList
 * 3.反射修改pathList的dexElements属性
 * 3.1 把解码之后的dex数组转化成Element数组
 * 3.2 获取pathList的dexElements属性
 * 3.3 合并上面的两个数组，重新赋值到pathList的dexElements属性
 */
public class ReInstallDecodeDexsUtils {

    /**
     * 将解密之后的dex数组塞到dexElements数组中
     *
     * @param application
     * @param dexFiles
     */
    public static void reInstallDexes(Application application, List<File> dexFiles) {

        //1.获取当前应用的pathclassloader
        //使用application来获取classloader。
        // 我们要使用的是pathclassloader/dexclassloader（是加载本应用的classloader）:
        // application.getCloassloader()
        // 而不是使用baseclassloader（这个是加载framework的类的classloader）
        //application.getClass().getClassLoader()就有可能获取到baselassloader，
        // 该情况出现在：在当前app没有自定义application，那么此时的application.getClass().getClassLoader()获取的就是baseclassloader
        ClassLoader classLoader = application.getClassLoader();
        LogUtils.logV("cls = " + classLoader.toString());
        try {
            // 2.反射获取DexPathList的属性对象pathList
            //获取的是所有的dex的pathList private final DexPathList pathList;
            Field pathListField = ClassReflectUtils.findField(classLoader, "pathList");
            Object pathList = pathListField.get(classLoader);
            //LogUtils.logV(pathList.toString());

            //3.从DexPathList中反射修改pathList的dexElements属性
            //* 3.1 把解码之后的dex数组转化成Element[]数组

            //通过调用 makePathElements()来将dexPath返回成Element[]
            //files:所有的dex集合
            //optimizedDirectory:dex需要优化成odx，该参数就是odx存放的目录
            //suppressedExceptions:存放异常的集合
            //loader:
            //isTrusted:默认传入的为false
            // private static Element[] makeDexElements(List<File> files, File optimizedDirectory,
            //                    List<IOException> suppressedExceptions, ClassLoader loader, boolean isTrusted) {

            //this.dexElements = makeDexElements(splitDexPath(dexPath), optimizedDirectory,
            //                                          suppressedExceptions, definingContext, isTrusted);

            Method makePathElements = ClassReflectUtils.findMethod(pathList, "makePathElements", List.class, File.class, List.class);
            //makePathElements()为静态方法，所以传入第一个参数为null，而第二个参数就是调用的参数
            // 第一个参数：在"哪个对象"上调用该方法。若是静态方法，则传null
            // 第二个参数：方法的调用参数
            File optimizedDirectory = new File(application.getCacheDir() + "/odx");
            List<IOException> suppressedExceptions = new ArrayList();
            Object[] dexDecodeObjects = (Object[]) makePathElements.invoke(null, dexFiles, optimizedDirectory, suppressedExceptions);
            LogUtils.logV("dexDecodeObjects = " + dexDecodeObjects.length + "\n" + dexDecodeObjects.toString());

            //* 3.2 获取pathList的dexElements属性  private Element[] dexElements;
            Field dexElementsField = ClassReflectUtils.findField(pathList, "dexElements");
            Object[] dexOldElements = (Object[]) dexElementsField.get(pathList);
            LogUtils.logV("dexOldElements = " + dexOldElements.length + "\n" + dexOldElements.toString());

            // * 3.3 合并上面的两个数组，重新赋值到pathList的dexElements属性
            //可以从下面的两种方式得倒Element类：  Class.forName()
            //也可以这样dexOldElements.getClass().getComponentType()
            Object[] newDexElements = (Object[]) Array.newInstance(dexOldElements.getClass().getComponentType(), dexDecodeObjects.length + dexOldElements.length);

            System.arraycopy(dexDecodeObjects, 0, newDexElements, 0, dexDecodeObjects.length);
            System.arraycopy(dexOldElements, 0, newDexElements, dexDecodeObjects.length, dexOldElements.length);
            dexElementsField.set(pathList, newDexElements);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
