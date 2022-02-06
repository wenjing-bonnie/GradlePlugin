package com.wj.appprotect.shell;

import android.app.Application;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

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

    public void reInstallDexes(Application application) {

        //1.获取当前应用的pathclassloader
        //使用application来获取classloader。
        // 我们要使用的是pathclassloader/dexclassloader（是加载本应用的classloader）:
        // application.getCloassloader()
        // 而不是使用baseclassloader（这个是加载framework的类的classloader）
        //application.getClass().getClassLoader()就有可能获取到baselassloader，
        // 该情况出现在：在当前app没有自定义application，那么此时的application.getClass().getClassLoader()获取的就是baseclassloader
        ClassLoader classLoader = application.getClassLoader();
        try {
            // 2.反射获取DexPathList的属性对象pathList
            Object pathList = ClassReflectUtils.findField(classLoader, "pathList");

            //3.反射修改pathList的dexElements属性
            //* 3.1 把解码之后的dex数组转化成Element[]数组
            //通过makePathElements 来将dexPath返回成Element[]
            Method makePathElements = ClassReflectUtils.findMethod(pathList, "makePathElements", List.class, File.class, List.class);
            // 第一个参数：在哪个对象上调用该方法。若是静态方法，则传null
            // 后面的为方法的调用参数
            //TODO 未完
            //makePathElements.invoke(null,)
            //* 3.2 获取pathList的dexElements属性
            // * 3.3 合并上面的两个数组，重新赋值到pathList的dexElements属性
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
